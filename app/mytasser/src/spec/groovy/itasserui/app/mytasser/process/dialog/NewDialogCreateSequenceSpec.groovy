package itasserui.app.mytasser.process.dialog


import itasserui.common.utils.SafeWaitKt
import javafx.scene.input.KeyCode
import org.testfx.matcher.base.NodeMatchers

import static itasser.app.mytasser.app.process.newDialog.NewSequenceCss.*
import static org.testfx.api.FxAssert.verifyThat

class NewDialogCreateSequenceSpec extends AbstractNewDialogSpec {
    void "Sequence details should be disabled by default"() {
        given: "A locator for the sequence details fieldset"
        def detailSet = lookup(".protein-details-fieldset")
        def requiredParams = lookup(".protein-details-required-parameters")

        expect: "The New Sequence Details should be disabled"
        verifyThat(detailSet, NodeMatchers.isDisabled())

        and: "The required parameter set should remain disabled"
        verifyThat(requiredParams, NodeMatchers.isDisabled())
    }

    void "Sequence details should be enabled when user selected"() {
        given: "A user"
        def user = account

        and: "A locator for the sequence details fieldset"
        def detailSet = lookup(".protein-details-fieldset")
        def requiredParams = lookup(".protein-details-required-parameters")
        def defaultParams = lookup(".protein-details-default-parameters")
        def optionalParams = lookup(".protein-details-optional-parameters")

        when: "The username is entered"
        clickOn(userField.render()).write(user.username.value)
        type(KeyCode.ENTER)

        then: "The New Protein Details fieldset should be enabled"
        verifyThat(detailSet, NodeMatchers.isEnabled())

        and: "The remaining fieldsets should remain disabled"
        verifyThat(requiredParams, NodeMatchers.isDisabled())
        verifyThat(defaultParams, NodeMatchers.isDisabled())
        verifyThat(optionalParams, NodeMatchers.isDisabled())
    }

    void "Optional and parameter fieldsets should be enabled when Required parameters are set"() {
        given: "A user"
        def user = account

        and: "A locator for the sequence details fieldset"
        def requiredParams = lookup(requiredFieldSet.render())
        def defaultParams = lookup(defaultParams.render())
        def optionalParams = lookup(optionalParams.render())

        and: "The locators for the outdir and datadir fields"
        def outDirField = lookup("${outDir.render()} .text-input").queryTextInputControl()
        def dataDirField = lookup("${dataDir.render()} .text-input").queryTextInputControl()

        when: "A user is selected"
        clickOn(userField.render()).write(user.username.value)
        type(KeyCode.ENTER)

        and: "The protein details are entered"
        clickOn(name.render()).write("Amoxylinezine")
        clickOn(description.render()).write("This isn't gonna work")
        clickOn(fastaLocation.render()).write(tmpdirPath.toString())

        then: "Required, Default and Optional parameters should be enabled"
        verifyThat(requiredParams, NodeMatchers.isEnabled())
        verifyThat(defaultParams, NodeMatchers.isEnabled())
        verifyThat(optionalParams, NodeMatchers.isEnabled())

        and: "The outdir and datadir have been filled"
        println("Relative root is ${user.relativeRootName}")
        dataDirField.text == tmpdirPath
                .resolve("users")
                .resolve(user.relativeRootName)
                .resolve("datadir")
                .resolve("Amoxylinezin")
                .toString()
        outDirField.text == tmpdirPath
                .resolve("users")
                .resolve(user.relativeRootName)
                .resolve("outdir")
                .resolve("Amoxylinezin")
                .toString()

        and: "The model should be valid"
        view.model.isValid()
    }

    void "Successfully creating an ITasser sequence"() {
        given: "A user"
        def user = account

        and: "A locator for password input"
        def loginUserPassword = { -> lookup("#password_field").queryTextInputControl() }

        when: "A user is selected"
        clickOn(userField.render()).write(user.username.value)
        type(KeyCode.ENTER)

        and: "The protein details are entered"
        clickOn(name.render()).write("Amoxylinezine")
        clickOn(description.render()).write("This isn't gonna work")
        clickOn(".new-protein-seq-file").write(tmpdirPath.toString())

        and: "The create button is clicked"
        clickOn(createButton.render())
        loginWithModal(loginUserPassword, account, false)

        then:
        extractor.proc.processes.size == 1
    }

}
