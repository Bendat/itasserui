package itasserui.app.mytasser.process.dialog

import itasser.app.mytasser.app.process.newDialog.NewProteinDialogCss

class NewDialogLoginRequiredSpec extends AbstractNewDialogSpec {
    void "Tapping create button without being logged in"() {
        given: "Dialog fields"
        def userField = lookup(".${NewProteinDialogCss.userField.name}").queryComboBox()
        def createButton = lookup(".${NewProteinDialogCss.createButton.name}").queryButton()
        and: "Login dialog fields"
        def loginUserField = { -> lookup("#username_field").queryComboBox() }
        def loginCancel = { -> lookup("#login_cancel").queryButton() }

        when: "Entering the user name and clicking create"
        clickOn(userField).write(user.username.value)
        clickOn(createButton)

        then: "A login dialog should appear with the same username autofille"
        userField.value == user.username.value
        loginUserField().value == userField.value

        cleanup:
        clickOn(loginCancel())
    }

    void "Logging in and creating invalid process"() {
        given: "Dialog fields"
        def userField = lookup(".${NewProteinDialogCss.userField.name}").queryComboBox()
        def createButton = lookup(".${NewProteinDialogCss.createButton.name}").queryButton()
        def errorPrompt = { -> lookup(".${NewProteinDialogCss.errorLabel.name}").queryLabeled() }

        and: "Login dialog fields"
        def loginUserPassword = { -> lookup("#password_field").queryTextInputControl() }

        when: "Logging in with valid credentials"
        clickOn(userField).write(user.username.value)
        clickOn(createButton)
        loginWithModal(loginUserPassword)
        clickOn("#login_login")
        clickOn(NewProteinDialogCss.name.render()).write("AmoxyPoxy")

        then: "Verify creation fails because necessary fields are empty"
        errorPrompt().isVisible()

    }

}
