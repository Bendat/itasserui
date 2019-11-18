package itasserui.app.mytasser.process.dialog

import itasser.app.mytasser.app.process.newDialog.NewProteinDialog
import itasserui.app.mytasser.UserAppSpec

class AbstractNewDialogSpec extends UserAppSpec<NewProteinDialog> {

    @Override
    NewProteinDialog create() {
        setupStuff()
        return new NewProteinDialog(testScope)
    }

}