package itasserui.app.mytasser.process.dialog

import itasser.app.mytasser.app.process.newDialog.NewProcessDialog
import itasserui.app.mytasser.UserAppSpec
import javafx.scene.control.TextInputControl


class AbstractNewDialogSpec extends UserAppSpec<NewProcessDialog> {

    @Override
    NewProcessDialog create() {
        setupStuff()
        return new NewProcessDialog(testScope)
    }

}