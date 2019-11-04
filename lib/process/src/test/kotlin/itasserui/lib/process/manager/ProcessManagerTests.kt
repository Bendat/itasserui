package itasserui.lib.process.manager

import arrow.core.Option
import io.kotlintest.be
import io.kotlintest.milliseconds
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.extensions.forEach
import itasserui.common.utils.uuid
import itasserui.lib.process.process.ITasser
import itasserui.test_utils.matchers.Be
import org.apache.commons.lang3.SystemUtils
import org.joda.time.DateTime
import java.nio.file.Paths

class ProcessManagerTests : DescribeSpec({
    val manager = ProcessManager()
    if (!SystemUtils.IS_OS_LINUX) {
        """
           Currently Broken on linux, kotlintest seems to execute 
            in parallel by  default
        """.trim()
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
                lateinit var proc: ITasser
                lateinit var next: Option<ITasser>
                it("Kills the first process") {
                    println("Running was ${manager.processes.running.toList()}")
                    proc = manager.processes.running[0]
                    next = manager.processes.next
                    println("Old running at  [${DateTime()}] is ${manager.processes.running.map { it }} ")
                    proc.executor.kill()
                    println("All is ${manager.processes.running.toList()}")

                }
                it("Verifies there are still 3 running processes") {
                    next.map { it.executor.awaitStart() }
                    manager.processes.running.size should be(3)
                }


                it("Kills the second process") {
                    proc = manager.processes.running[0]
                    next = manager.processes.next
                    proc.executor.kill() should Be.ok()
                }

                it("Verifies again there are still 3 running processes") {
                    proc.executor.await()
                    next.map { it.executor.awaitStart() }
                    manager.processes.running.size should be(3)
                }

                it("Kills the 3rd process") {
                    proc = manager.processes.running[0]
                    next = manager.processes.next
                    proc.executor.kill()
                }

                it("Verifies there are 2 running processes") {
                    proc.executor.await()
                    next.map { it.executor.awaitStart() }
                    manager.processes.running.size should be(2)
                }

                it("Kills the 4th process") {
                    proc = manager.processes.running[0]
                    next = manager.processes.next
                    proc.executor.kill()
                }

                it("Verifies there is 1 running processes") {
                    proc.executor.await()
                    next.map { it.executor.awaitStart() }
                    manager.processes.running.size should be(1)
                }

                it("Kills the 5th process") {
                    proc = manager.processes.running[0]
                    next = manager.processes.next
                    proc.executor.kill()
                }

                it("Verifies there is 0 running processes") {
                    proc.executor.await()
                    next should Be.none()
                    manager.processes.running.size should be(0)
                }
            }

            context("Allowing a process") {
                val newFile = Paths.get(javaClass.getResource("/Loop2.pl").file)
                lateinit var nextRunning: Option<ITasser>
                it("Adds a process that will execute for 2 seconds") {
                    manager.new(
                        processId = uuid,
                        priority = 0,
                        seqFile = Paths.get(""),
                        name = "Process[Loop2]",
                        args = listOf(newFile.toString()),
                        createdBy = uuid,
                        dataDir = Paths.get("")
                    )
                }
                it("Verifies the process  is running ") {
                    nextRunning = manager.processes.nextRunning
                    manager.processes.next should Be.none()
                }
                it("Waits for 2 seconds") {
                    nextRunning.map { it.executor.waitForFinish(2000.milliseconds) }
                }

                it("Verifies the process finished") {
                    manager.processes.next should Be.none()
                    manager.processes.completed.size should be(1)
                }
            }
        }
    }
})