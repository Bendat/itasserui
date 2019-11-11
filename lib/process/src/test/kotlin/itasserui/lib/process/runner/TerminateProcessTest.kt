@file:Suppress("RemoveExplicitTypeArguments")

package itasserui.lib.process.runner

import arrow.core.Either
import arrow.core.Some
import io.kotlintest.be
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.matchers.beLessThan
import io.kotlintest.should
import io.kotlintest.shouldNot
import io.kotlintest.specs.DescribeSpec
import itasserui.common.`typealias`.Err
import itasserui.common.`typealias`.OK
import itasserui.common.utils.safeWait
import itasserui.common.utils.until
import itasserui.lib.filemanager.FS
import itasserui.lib.process.details.ExecutionState
import itasserui.lib.process.details.ExecutionState.Paused
import itasserui.lib.process.details.ExecutionState.Running
import itasserui.lib.process.details.ExitCode
import itasserui.lib.process.process.ITasser

class TerminateProcessTest : DescribeSpec({
    val file = FS[javaClass.getResource("/Loop5.pl").file]
    describe("Gracefully destroying a long running process") {
        lateinit var runner: ITasser
        it("Creates the process") {
            runner = getProcess(file)
        }

        it("Starts the process") {
            runner.executor.start() shouldNot beInstanceOf<Err>()
        }

        it("Will wait for the process to be in it's $Running state") {
            until { runner.state == Running }
            safeWait(1000)
            runner.state should be<ExecutionState>(Running)
        }

        it("Should destroy the process") {
            safeWait(1000) 
            runner.executor.kill() shouldNot beInstanceOf<Err>()
        }

        it("Should wait for a result for no more than 1 second") {
            runner.executor.await() as Either.Right
        }

        it("Should verify the process did not execute to completion") {
            runner.std.out.size should beLessThan(5)
        }

        it("Verifies the exitcode is accurate") {
            runner.executor.exitCode.map { it should beInstanceOf<ExitCode.SigTerm>() } as Some
        }

        it("Should verify the state is $Paused") {
            runner.state should beInstanceOf<Paused>()
        }
    }

    describe("Forcefully destroying a long running process") {
        lateinit var runner: ITasser
        it("Creates the process") {
            runner = getProcess(file)
        }

        it("Starts the process") {
            runner.executor.start() shouldNot beInstanceOf<Err>()
        }

        it("Will tait for the process to be in its $Running state") {
            until { runner.state == Running }
            runner.state should be<ExecutionState>(Running)
        }

        it("Should destroy the process") {
            runner.executor.destroy() should beInstanceOf<OK<*>>()
        }

        it("Should wait for a result for no more than 1 second") {
            runner.executor.await().also { println("Res is [$it]") } as OK
        }

        it("Verifies the exitcode is accurate") {
            runner.executor.exitCode.map { it should beInstanceOf<ExitCode.SigKill>() } as Some
        }

        it("Should verify the process did not execute to once") {
            println("STD ${runner.std.out}")
            runner.std.out.size should beLessThan(1)
        }

        it("Should verify the state is $Paused") {
            runner.state should beInstanceOf<Paused>()
        }
    }
})