package itasser.app.mytasser.app.process.pane.widget

import tornadofx.CssRule
import tornadofx.cssclass

val CssRule.value get() = ".$name"
object WidgetCss {
    @JvmStatic
    val timerLabel by cssclass("process-widget-timer-label-value")
    @JvmStatic
    val startDate by cssclass("process-widget-start-date-label")
    @JvmStatic
    val startTime by cssclass("process-widget-start-time-label")
    @JvmStatic
    val sequenceName by cssclass("process-widget-sequence-name-label")
    @JvmStatic
    val controlButton by cssclass("process-widget-run-pause-button")

}