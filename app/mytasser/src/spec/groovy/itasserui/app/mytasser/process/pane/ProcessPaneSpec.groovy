package itasserui.app.mytasser.process.pane

import static itasserui.common.utils.SafeWaitKt.safeWait

class ProcessPaneSpec extends AbstractProcessPaneSpec{
    void "foobar"(){
//        given:
//        itasser.executor.start$process()
        expect:
        safeWait(10000)
    }
}
