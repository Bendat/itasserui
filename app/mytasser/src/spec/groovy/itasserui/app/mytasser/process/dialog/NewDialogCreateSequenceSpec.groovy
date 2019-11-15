package itasserui.app.mytasser.process.dialog

import itasser.app.mytasser.app.process.newDialog.NewSequenceCss
import itasserui.common.utils.SafeWaitKt

class NewDialogCreateSequenceSpec extends AbstractNewDialogSpec {

    void "Verifies the sequence name and required dirs are autofilled"() {
        given: "Controls fields"
        def createButton = lookup(NewSequenceCss.createButton.render()).queryButton()
        def errorPrompt = { -> lookup(NewSequenceCss.errorLabel.render()).queryLabeled() }
        def userField = lookup(NewSequenceCss.userField.render()).queryComboBox()
        def description = lookup(NewSequenceCss.description.render()).queryTextInputControl()
        def name = lookup(NewSequenceCss.name.render()).queryTextInputControl()
        def seqName = lookup(NewSequenceCss.sequenceName.render()).queryTextInputControl()
        def datadir = lookup("${NewSequenceCss.dataDir.render()} .text-input").queryTextInputControl()
        def outdir = lookup("${NewSequenceCss.outDir.render()} .text-input").queryTextInputControl()

        and: "Login dialog fields"
        def loginUserPassword = { -> lookup("#password_field").queryTextInputControl() }

        when: "Logging in and completing form"
        clickOn("$userField .text-field").write(account.username.value)
        clickOn(createButton)
        loginWithModal(loginUserPassword)
        clickOn(name).write("Amoxyl Dioxyn Junior")
        clickOn(description).write("This boys got a lot amoxy")

        then: "The Directories should match those of the selected profile"
        datadir.text == view.model.item.profile?.dataDir?.unixPath?.toString()
        outdir.text == view.model.item.profile?.outDir?.unixPath?.toString()

        and: "The sequence name should match the seqname value"
        seqName.text == name.text
    }

    void "Wait"(){
        expect:
        SafeWaitKt.safeWait(10000)
    }

    void "Verifies creating the sequence"() {
        given: "Controls fields"
        def createButton = lookup(NewSequenceCss.createButton.render()).queryButton()
        def errorPrompt = { -> lookup(NewSequenceCss.errorLabel.render()).queryLabeled() }
        def userField = lookup(NewSequenceCss.userField.render()).queryComboBox()
        def description = lookup(NewSequenceCss.description.render()).queryTextInputControl()
        def name = lookup(NewSequenceCss.name.render()).queryTextInputControl()
        def seqName = lookup(NewSequenceCss.sequenceName.render()).queryTextInputControl()
        def seqFileField = lookup(".new-sequence-seq-file .file-chooser-input").queryTextInputControl()
        def datadir = lookup("${NewSequenceCss.dataDir.render()} .text-input").queryTextInputControl()
        def outdir = lookup("${NewSequenceCss.outDir.render()} .text-input").queryTextInputControl()

        and: "Login dialog fields"
        def loginUserPassword = { -> lookup("#password_field").queryTextInputControl() }

        when: "Logging in and completing form"
        clickOn(userField).write(account.username.value)
        clickOn(createButton)
        loginWithModal(loginUserPassword)
        clickOn(name).write("Amoxyl Dioxyn Junior")
        println("SeqFile is $seqFile")
        clickOn(seqFileField).write(seqFile.toString())
        clickOn(description).write("This boys got a lot amoxy")
        clickOn(createButton)

        then:
        extractor.proc.processes.all.size == 1
        SafeWaitKt.safeWait(15000)
    }
}
