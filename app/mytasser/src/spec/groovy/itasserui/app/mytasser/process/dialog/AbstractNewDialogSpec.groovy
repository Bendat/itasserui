package itasserui.app.mytasser.process.dialog

import itasser.app.mytasser.app.process.newDialog.NewSequenceDialog
import itasserui.app.mytasser.UserAppSpec

class AbstractNewDialogSpec extends UserAppSpec<NewSequenceDialog> {

    @Override
    NewSequenceDialog create() {
        setupStuff()
        return new NewSequenceDialog(testScope)
    }

}