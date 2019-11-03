package itasserui.lib.process.runner

import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.should
import io.kotlintest.shouldNot
import io.kotlintest.specs.DescribeSpec
import itasserui.common.`typealias`.Err
import itasserui.common.extensions.ms
import itasserui.common.utils.safeWait
import itasserui.lib.filemanager.FS
import itasserui.lib.process.process.ITasser
import itasserui.test_utils.matchers.Be

class ExecutionTimerTests : DescribeSpec({
    val file = FS[javaClass.getResource("/Infinity.pl").file]
    describe("Starting and Stopping a process") {
        lateinit var runner: ITasser

        it("Creates the process") {
            runner = getProcess(file)
        }

        it("Starts the process") {
            runner.executor.start() shouldNot beInstanceOf<Err>()
        }
        it("Stops the process") {
            runner.executor.kill(1000)
        }

        it("Verifies the process has run for 1 second") {
            runner.executionTime should Be.closeTo(1000L, 200)
        }

        it("Starts the process again") {
            runner.executor.start()
        }
        it("Stops the process again") {
            runner.executor.kill(1000)
        }

        it("Verifies the process has run for 2 seconds") {
            runner.executionTime should Be.closeTo(2000L, 500)
        }

        it("Stops the process a final time") {
            runner.executor.kill()
        }
    }
})