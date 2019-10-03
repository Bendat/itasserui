package itasserui.app.mytasser.process.pane.widget

import itasserui.app.user.ProfileManager
import itasserui.lib.process.manager.ProcessManager
import itasserui.lib.store.Database
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class KodeinExtractor(kodein: Kodein) {
    val pm: ProfileManager by kodein.instance()
    val proc: ProcessManager by kodein.instance()
    val db: Database by kodein.instance()
}