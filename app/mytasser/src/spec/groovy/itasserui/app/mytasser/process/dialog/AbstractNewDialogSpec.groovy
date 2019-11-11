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

    protected void loginWithModal(Closure<TextInputControl> loginUserPassword) {
        clickOn(loginUserPassword()).write(account.password.value)
        for (int i = 0; i < 30; i++) {
            clickOn(".login-modal .increment-arrow-button")
        }

        clickOn("#login_login")
    }
}