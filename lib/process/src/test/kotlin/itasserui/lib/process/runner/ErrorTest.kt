package itasserui.lib.process.runner

import arrow.core.None
import io.kotlintest.be
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.should
import io.kotlintest.shouldNot
import io.kotlintest.specs.DescribeSpec
import itasserui.common.`typealias`.Err
import itasserui.common.utils.until
import itasserui.lib.filemanager.FS
import itasserui.lib.process.details.ExecutionState
import itasserui.lib.process.process.ITasser

class ErrorTest : DescribeSpec({
    describe("Receiving an error message") {
        val file = FS[javaClass.getResource("/Error.pl").file]

        lateinit var runner: ITasser
        it("Creates the process") {
            runner = getProcess(file)
        }

        it("Starts the process") {
            runner.executor.start() shouldNot beInstanceOf<Err>()
        }

        it("Should verify the process has a Future property") {
            runner.executor.future shouldNot beInstanceOf<None>()
        }

        it("Wait until the process is running") {
            until { runner.state == ExecutionState.Running }
            runner.state should be<ExecutionState>(ExecutionState.Running)
        }

        it("Block the thread until the process exits") {
            runner.executor.future.map { it.get() }
        }

        it("Should verify that the stdout is empty") {
            runner.std.output.size should be(0)
        }

        it("Should verify that the stderr has exactly one item") {
            runner.std.err.size should be(1)
        }

        it("Should verify the stdout of the program contains 'Error Message'") {
            runner.std.err.first().item should be("Error Message")
        }

        it("Should verify the state is ${ExecutionState.Completed}") {
            runner.state should beInstanceOf<ExecutionState.Completed>()
        }
    }

})