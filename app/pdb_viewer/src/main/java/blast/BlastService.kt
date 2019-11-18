package blast

import javafx.concurrent.Service
import javafx.concurrent.Task

import blast.RemoteBlastClient.Status

/**
 * Service allowing to concurrently call a BLAST function and run it.
 */
class BlastService : Service<String>() {

    private var sequence: String? = null

    /**
     * Set the sequence to BLAST.
     *
     * @param sequence The sequence to BLAST.
     */
    fun setSequence(sequence: String) {
        this.sequence = sequence
    }

    override fun createTask(): Task<String> {
        return object : Task<String>() {
            override fun call(): String {
                // No sequence was set, the task needs to fail.
                if (sequence == null)
                    throw Exception("No sequence set. Cannot BLAST.")
                // Build the result
                val result = StringBuilder()
                // Use and call the client to handle BLAST queries
                val remoteBlastClient = RemoteBlastClient()
                remoteBlastClient.setProgram(RemoteBlastClient.BlastProgram.blastp).database = "nr"

                // Set the Task's title to this
                updateTitle("BLAST sequence...")
                remoteBlastClient.startRemoteSearch(sequence)

                updateMessage(
                    "Request id: " + remoteBlastClient.requestId + "\n" +
                            "Estimated time: " + remoteBlastClient.estimatedTime + "s"
                )
                updateProgress(0, remoteBlastClient.estimatedTime.toLong())
                val startTime = System.currentTimeMillis()
                var status: Status? = null
                // Query BLAST for status, if sequence is done or not.
                do {
                    if (status != null)
                        Thread.sleep(5000)
                    status = remoteBlastClient.remoteStatus
                    updateMessage(
                        "Request id: " + remoteBlastClient.requestId + "\n" +
                                "Estimated time: " + remoteBlastClient.estimatedTime + "s\n" +
                                "Passed time: " + (System.currentTimeMillis() - startTime) / 1000 + "s"
                    )
                    updateProgress(
                        (System.currentTimeMillis() - startTime) / 1000,
                        remoteBlastClient.estimatedTime.toLong()
                    )
                    if (isCancelled)
                        break
                } while (status == Status.searching)

                if (isCancelled) {
                    updateTitle("Cancelled")
                    result.append("Cancelled")
                    return result.toString()
                }

                when (status) {
                    Status.hitsFound -> {
                        updateTitle("BLAST done: Hits found.")
                        for (line in remoteBlastClient.remoteAlignments!!) {
                            result.append(line + "\n")
                        }
                    }
                    Status.noHitsFound -> {
                        updateTitle("BLAST done: no hits were found.")
                        result.append("No hits found.")
                        System.err.println("No hits")
                    }
                    else -> {
                        updateMessage("BLAST failed.")
                        updateTitle("BLAST failed.")
                        System.err.println("Status: " + status!!)
                        throw Exception("This might be because you are not connected to the Internet.")
                    }
                }

                System.err.println("Actual time: " + remoteBlastClient.actualTime + "s")
                return result.toString()
            }
        }
    }
}
