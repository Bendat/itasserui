package itasserui.lib.process.manager

import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.utils.uuid
import itasserui.lib.process.details.ExecutionState
import itasserui.lib.process.details.ExecutionState.*
import java.nio.file.Paths

class ProcessListTests : DescribeSpec({
    val manager = ProcessManager(autoRun = false)
    describe("Process queues filtering correctly") {
        context("creating the processes") {
            ExecutionState.states.forEach {
                it("Should create a new $it'd process") {
                    manager.new(
                        processId = uuid,
                        priority = 0,
                        seqFile = Paths.get(""),
                        name = "Process[$it]",
                        args = listOf(),
                        createdBy = uuid,
                        dataDir = Paths.get(""),
                        state = it
                    )
                }
            }

        }

        context("Verifying the processes are in their correct queue") {
            it("Should verify the $Queued is in the queued queue") {
                manager.process[Queued].size should be(1)
            }

            it("Should verify the $Completed is in the queued queue") {
                manager.process[Completed].size should be(1)
            }

            it("Should verify the $Paused is in the queued queue") {
                manager.process[Paused].size should be(1)
            }

            it("Should verify the $Running is in the queued queue") {
                manager.process[Running].size should be(1)
            }
        }

        it("Should verify the process queue is size ${ExecutionState.states.size}") {
            manager.process.size should be(ExecutionState.states.size)
        }

    }
})