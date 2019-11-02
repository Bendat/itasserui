package itasserui.app.mytasser

import itasserui.test_utils.KLog
import itasserui.test_utils.Logger
import kotlin.jvm.functions.Function0

import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.testfx.framework.spock.ApplicationSpec

abstract class GuiSpec extends ApplicationSpec implements Logger {
    def log = new KLog()

    org.slf4j.Logger getKlog() {
        return log.klog
    }

    void info(@Nullable Object op) {
        log.info(op)
    }

    void trace(@Nullable Object op) {
        log.trace(op)
    }

    void debug(@Nullable Object op) {
        log.debug(op)
    }

    void error(@Nullable Object op) {
        log.error(op)
    }

    void warn(@Nullable Object op) {
        log.warn(op)
    }

    void info(boolean format = true, @NotNull Function0<?> op) {
        log.info(format, op)
    }

    void trace(boolean format = true, @NotNull Function0<?> op) {
        super.trace(format, op)
    }

    void debug(boolean format = true, @NotNull Function0<?> op) {
        log.debug(format, op)
    }

    void error(boolean format = true, @NotNull Function0<?> op) {
        log.error(format, op)
    }

    void warn(boolean format = true, @NotNull Function0<?> op) {
        log.warn(format, op)
    }

}
