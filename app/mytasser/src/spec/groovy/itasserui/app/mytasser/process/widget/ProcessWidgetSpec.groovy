package itasserui.app.mytasser.process.widget

import com.anotherchrisberry.spock.extensions.retry.RetryOnFailure
import itasser.app.mytasser.app.process.pane.widget.WidgetCss
import itasserui.common.annotations.RetryReason
import org.joda.time.DateTime
import org.testfx.matcher.base.NodeMatchers
import org.testfx.matcher.control.LabeledMatchers
import org.testfx.service.query.NodeQuery

import static itasserui.common.utils.SafeWaitKt.safeWait
import static org.testfx.api.FxAssert.verifyThat

class ProcessWidgetSpec extends ProcessPaneWidgetSpec {
    NodeQuery timerLabel
    NodeQuery startDate
    NodeQuery startTime
    NodeQuery sequenceName
    NodeQuery controlButton
    DateTime dateTime

    void setup() {
        dateTime = DateTime.now()

        setup: "The common nodes"
        timerLabel = lookup(".$WidgetCss.timerLabel.name")
        startDate = lookup(".$WidgetCss.startDate.name")
        sequenceName = lookup(".$WidgetCss.sequenceName.name")
        controlButton = lookup(".$WidgetCss.controlButton.name")
        startTime = lookup(".$WidgetCss.startTime.name")

        expect: "The nodes are initialized and retrieved"
        timerLabel != null
        startDate != null
        sequenceName != null
        controlButton != null
        startTime != null

        and: "Nodes have correct visibility"
        verifyThat(startDate, NodeMatchers.isInvisible())
        verifyThat(startTime, NodeMatchers.isInvisible())
    }


    @RetryOnFailure(times = 2)
    @RetryReason("The time may rollover by a minute if run near the end of a minute i.e 10:30:59 and 10:31:XX")
    void "Executing a process should change the state of the widget"() {
        given: "The time strings to verify"
        def date = dateTime.toString("dd / MM / YYYY")
        def time = dateTime.toString("hh:mm")
        when: "The run button is clicked"
        clickOn(controlButton.queryButton())
        login()
        safeWait(1000)

        then:
        view.model.runPauseIcon.value == view.model.item.runStopIcons.pause
        verifyThat(startDate, NodeMatchers.isVisible())
        verifyThat(startTime, NodeMatchers.isVisible())
        verifyThat(startDate, LabeledMatchers.hasText(date))
        verifyThat(startTime, LabeledMatchers.hasText(time))
        verifyThat(timerLabel, LabeledMatchers.hasText("0:00:01"))
    }

    void "Pausing an executing process"() {
        when: "The process for this widget is executed and paused"
        clickOn(controlButton.queryButton())
        login()
        safeWait(1000)
        clickOn(controlButton.queryButton())
        clickOn(controlButton.queryButton())
        safeWait(1000)
        clickOn(controlButton.queryButton())

        then:
        view.model.runPauseIcon.value == view.model.item.runStopIcons.play
        verifyThat(timerLabel, LabeledMatchers.hasText("0:00:02"))
    }

    void "Attempting to use the widget while logged out"() {
        when: "The process for this widget is executed and paused"
        clickOn(controlButton.queryButton())
        login(false)
        safeWait(1000)
        clickOn("OK")

        then:
        view.model.runPauseIcon.value == view.model.item.runStopIcons.play
        verifyThat(timerLabel, LabeledMatchers.hasText(""))

    }


}