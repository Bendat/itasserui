package itasserui.app.mytasser.process.pane

import itasser.app.mytasser.app.process.pane.widget.WidgetCss
import itasserui.common.utils.SafeWaitKt

import static itasserui.common.utils.SafeWaitKt.*

class ProcessPaneSpec extends AbstractProcessPaneSpec {
    void "Basic interactions with existing process"() {
        given:
        def id = itasser.process.id
        expect:
        lookup(".queued-list").queryListView().items.size() == 1

        when:
        clickOn(".queued-fold")
        clickOn(".$id .${WidgetCss.controlButton.name}")
        login()

        then:
        lookup(".running-list").queryListView().items.size() == 1

        and:
        clickOn(".queued-fold")
        clickOn(".running-fold")

        clickOn(".$id .${WidgetCss.controlButton.name}")
        clickOn(".paused-fold")

        then:
        lookup(".paused-list").queryListView().items.size() == 1
    }

}
