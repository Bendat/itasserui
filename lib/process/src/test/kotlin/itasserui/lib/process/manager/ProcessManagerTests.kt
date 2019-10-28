package itasserui.lib.process.manager

import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.extensions.forEach
import itasserui.common.utils.safeWait
import itasserui.common.utils.uuid
import itasserui.lib.process.process.ITasser
import itasserui.test_utils.matchers.Be
import org.joda.time.DateTime
import java.nio.file.Paths

class ProcessManagerTests : DescribeSpec({
    val waitTime = 500L
    val manager = ProcessManager()

    describe("Executing processes sequentially upon completion") {
        val file = Paths.get(javaClass.getResource("/Infinity.pl").file)
        context("Creating the processes") {
            4.forEach {
                it("Creates a new $it") {
                    manager.new(
                        processId = uuid,
                        priority = 0,
                        seqFile = Paths.get(""),
                        name = "Process[$it]",
                        args = listOf(file.toString()),
                        createdBy = uuid,
                        dataDir = Paths.get("")
                    )
                }
            }
        }

        it("Verifies there are 3 processes running") {

            manager.processes.running.size should be(3)
        }

        context("Killing processes manually") {

            it("Kills the first process") {
                println("Running was ${manager.processes.running.toList()}")
                val proc = manager.processes.running[0]
                println("Old running at  [${DateTime()}] is ${manager.processes.running.map { it }} ")
                proc.executor.kill()
                println("All is ${manager.processes.running.toList()}")

            }
            context("Not sure") {
                it("Verifies there are still 3 running processes") {
                    safeWait(1000)
                    manager.processes.running.size should be(3)
                }
            }

            it("Kills the second process") {
                manager.processes.running[0].executor.kill() should Be.ok()
            }

            it("Verifies again there are still 3 running processes") {
                safeWait(1000)
                manager.processes.running.size should be(3)
            }

            it("Kills the 3rd process") {
                manager.processes.running[0].executor.kill()
            }

            it("Verifies there are 2 running processes") {
                safeWait(1000)
                manager.processes.running.size should be(2)
            }

            it("Kills the 4th process") {
                manager.processes.running[0].executor.kill()
            }

            it("Verifies there is 1 running processes") {
                safeWait(1000)
                manager.processes.running.size should be(1)
            }

            it("Kills the 5th process") {
                manager.processes.running[0].executor.kill()
            }

            it("Verifies there is 0 running processes") {
                safeWait(1000)
                manager.processes.running.size should be(0)
            }
        }

        context("Allowing a process") {
            val newFile = Paths.get(javaClass.getResource("/Loop2.pl").file)
            lateinit var proc: ITasser
            it("Adds a process that will execute for 2 seconds") {
                proc = manager.new(
                    processId = uuid,
                    priority = 0,
                    seqFile = Paths.get(""),
                    name = "Process[Loop2]",
                    args = listOf(newFile.toString()),
                    createdBy = uuid,
                    dataDir = Paths.get("")
                )
                safeWait(200)
            }
            it("Verifies the process  is running ") {
                println("Processes are " + manager.processes.paused.map { it })
                manager.processes.nextRunning should Be.some(proc)
            }
            it("Waits for 2 seconds") {
                safeWait(1100)
            }

            it("Verifies the process finished") {
                manager.processes.nextRunning should Be.none()
                manager.processes.completed.size should be(1)
            }
        }
    }
}) {
    init {
    }
}