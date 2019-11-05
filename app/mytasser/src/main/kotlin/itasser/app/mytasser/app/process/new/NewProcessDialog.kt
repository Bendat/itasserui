package itasser.app.mytasser.app.process.new

import itasser.app.mytasser.app.login.LoginModal.LoginDuration.Seconds
import itasserui.lib.process.ArgParam.*
import itasserui.lib.process.ArgParam.Range.*
import itasserui.lib.process.ArgParam.SimpleText.Text
import itasserui.lib.process.Args
import tornadofx.*

class NewProcessDialog : View("New Process Dialog") {
    override val root = vbox {
        prefWidth = 320.0
        prefHeight = 500.0
        scrollpane {
            form {
                fieldset("Sequence") {
                    spacer { }

                    field("Name") { textfield { } }
                    spacer { }

                    field("Fasta file") { textfield { } }
                    spacer { }

                }
                fieldset("ITasser arguments") {
                    Args.values.forEach {
                        if (it.useOnNew) {
                            when (val type = it.argType) {
                                is Directory -> field(it.simpleName) {
                                    hbox {
                                        textfield { }
                                        button("...") { }
                                    }
                                }
                                is File -> field(it.simpleName) {
                                    hbox {
                                        textfield { }
                                        button("...") { }
                                    }
                                }

                                is Text -> field(it.simpleName) {
                                    hbox {
                                        textfield { }
                                        button("...") { }
                                    }
                                }

                                is BooleanParam -> field(it.simpleName) { checkbox { } }
                                is SimpleText -> field(it.simpleName) { textfield { } }
                                is FloatRange -> field(it.simpleName) {
                                    spinner(
                                        type.range.start,
                                        type.range.endInclusive,
                                        type.default,
                                        0.1,
                                        false,
                                        null,
                                        true
                                    )
                                    { maxWidth = 80.0 }
                                }
                                is IntegerRange -> field(it.simpleName) {
                                    spinner(
                                        type.range.first,
                                        type.range.last,
                                        type.default,
                                        1,
                                        false,
                                        null,
                                        true
                                    )
                                    { maxWidth = 80.0 }
                                }
                                is TextSelection -> field(it.simpleName){
                                    combobox(null, type.range) { value = type.default }

                                }
                            }
                        }

                    }
                }
            }

        }
        form {
            fieldset {
                hbox(10) {
                    field { button("Cancel") { } }
                    field { button("Create") {} }
                }
            }
        }
    }
}