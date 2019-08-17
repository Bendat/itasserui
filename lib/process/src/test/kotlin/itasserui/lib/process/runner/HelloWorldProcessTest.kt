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

class HelloWorldProcessTest : DescribeSpec({
    describe("Creating a basic process which prints 'hello world' and exits") {
        val file = FS[javaClass.getResource("/HelloWorld.pl").file]

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

        it("Should verify that the stdout contains at least one entry") {
            runner.std.output.size should be(1)
        }

        it("Should verify the stdout of the program contains 'Hello World'") {
            runner.std.output.first().item should be("Hello World")
        }
        it("Should verify the state is ${ExecutionState.Completed}") {
            runner.state should beInstanceOf<ExecutionState.Completed>()
        }
    }

    describe("Creating a looping process which writes to stdout 5 times") {
        val file = FS[javaClass.getResource("/Loop5.pl").file]
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
            runner.state should be<ExecutionState>(ExecutionState.Completed)

        }

        it("Should verify that the stdout contains at least one entry") {
            runner.std.output.size should be(6)

        }

        listOf(0, 1, 2, 3, 4, 5).forEach { index ->
            it("Should verify that stdout[$index] is [$index]'") {
                runner.std.output[index].item should be(index.toString())
            }
        }

        it("Should verify the state is ${ExecutionState.Completed}") {
            runner.state should beInstanceOf<ExecutionState.Completed>()
        }
    }
})