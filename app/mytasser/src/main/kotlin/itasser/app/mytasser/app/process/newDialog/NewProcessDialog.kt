package itasser.app.mytasser.app.process.newDialog

import arrow.core.Either.Right
import itasser.app.mytasser.app.components.tornadofx.FileChooserType
import itasser.app.mytasser.app.components.tornadofx.intConverter
import itasser.app.mytasser.app.components.tornadofx.itFileChooser
import itasser.app.mytasser.app.components.tornadofx.pathConverter
import itasser.app.mytasser.app.process.pane.widget.loginModal
import itasserui.common.interfaces.inline.Username
import itasserui.common.logger.Logger
import itasserui.lib.process.Arg
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextField
import org.controlsfx.control.textfield.TextFields.bindAutoCompletion
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Paths
import itasser.app.mytasser.app.process.newDialog.NewSequenceCss as css

class NewProcessDialog(scope: Scope? = null) : View("Create New ITasser Process"), Logger {
    override val scope: Scope = scope ?: super.scope
    val model: NewProcessDialogModel by inject()
    override val complete: BooleanExpression = model.valid(
        model.seqName, model.dataDir, model.outDir, model.seqName
    )
    @Suppress("UNCHECKED_CAST")
    override val root = vbox {
        print("Hello world")
        prefWidth = 450.0
        prefHeight = 690.0
        lateinit var user: Field
        val proceedDetails = SimpleBooleanProperty(false)
        form {
            fieldset {
                user = field("User") {
                    combobox<String>(model.user) {
                        addClass(css.userField)
                        itemsProperty().bind(model.users)
                        isEditable = true
                        bindAutoCompletion(editor, model.users.value)
                        model.user.onChange {
                            val prof = model.item.profileManager.findUser(Username(it ?: ""))
                            if (prof is Right) {
                                model.item.profile = prof.b
                                proceedDetails.value = true
                            }
                        }
                    }
                }
            }
        }
        separator { }
        scrollpane {
            val canContinue = SimpleBooleanProperty(false)

            lateinit var nameField: TextField
            form {
                enableWhen(proceedDetails)
                separator { }
                fieldset("Sequence Details") {
                    field("Name") {
                        nameField = textfield(model.name) {
                            addClass(css.name)
                            textProperty().bindBidirectional(model.name)
                            user.prefWidthProperty().bind(user.widthProperty())
                        }
                        spacer { }
                    }
                    spacer { }
                    vbox {
                        label("Description")
                        textarea {
                            addClass(css.description)
                            prefColumnCount = 20
                            prefRowCount = 5
                            isWrapText = true
                        }
                    }

                    field("Fasta location") {
                        itFileChooser(model.seqFile, FileChooserType.Directory) {
                            addClass("new-sequence-seq-file")
                            textProperty.onChange {
                                if (it != null && Files.exists(Paths.get(it))) canContinue.value = true
                            }
                        }
                    }

                }

                fieldset("ITasser Required Parameters") {
                    separator { }
                    enableWhen(canContinue)
                    field("Sequence Name") {
                        textfield(model.seqName) {
                            addClass(css.sequenceName)
                            nameField.textProperty()
                                .addListener { _, old, new ->
                                    if (old == text)
                                        this.text = new
                                }
                            validator {
                                if (text.isNullOrBlank()) error("Sequence name must not be empty")
                                else null
                            }
                        }
                    }

                    field("Data Directory") {
                        itFileChooser(model.dataDir, FileChooserType.Directory, disableManual = true) {
                            textProperty.bindBidirectional(model.dataDir, pathConverter)
                            addClass(css.dataDir)
                            model.name.onChange { value ->
                                text = model.item.profile?.dataDir?.resolve(value ?: "/no-user-defined").toString()
                            }
                        }
                    }

                    field("Out Directory") {
                        itFileChooser(model.outDir, FileChooserType.Directory, disableManual = true) {
                            addClass(css.outDir)
                            model.name.onChange { value ->
                                text = model.item.profile?.outDir?.resolve(value ?: "/no-user-defined").toString()
                            }
                        }
                    }


                }

                fieldset("Default parameters") {
                    separator { }
                    field("ID Cut") {
                        val type = Arg.IdCut.argType
                        spinner(
                            type.range.start,
                            type.range.endInclusive,
                            type.default,
                            0.1,
                            true,
                            model.idCut,
                            false
                        ) {
                            addClass(css.idCut)
                            maxWidth = 80.0
                        }
                    }

                    field("N Temp") {
                        val type = Arg.NTemp.argType
                        spinner<Int>(
                            type.range.first,
                            type.range.last,
                            type.default,
                            1,
                            true,
                            model.nTemp,
                            false
                        ) {
                            addClass(css.nTemp)
                            maxWidth = 80.0
                        }
                    }

                    field("N Model") {
                        val type = Arg.NModel.argType
                        spinner<Int>(
                            type.range.first,
                            type.range.last,
                            type.default,
                            1,
                            true,
                            model.nModel,
                            false
                        ) {
                            addClass(css.nModel)
                            maxWidth = 80.0
                        }
                    }

                    field("EC") {
                        checkbox(null, model.ec) {
                            addClass(css.ec)
                        }
                    }

                    field("LBS") {
                        checkbox(null, model.lbs) {
                            addClass(css.lbs)

                        }
                    }

                    field("GO") {
                        checkbox(null, model.go) {
                            addClass(css.go)
                        }
                    }

                    field("Light") {
                        checkbox(null, model.light) {
                            addClass(css.light)
                        }
                    }

                    field("Traj") {
                        checkbox(null, model.traj) {
                            addClass(css.traj)

                        }
                    }
                }

                fieldset("Optional Parameters") {
                    separator { }
                    field("Homoflag") {
                        val items = Arg
                            .HomoFlag
                            .argType
                            .range
                        combobox(model.homoFlag, items) {
                            addClass(css.homoflag)
                        }
                    }

                    field("Temp Excl") {
                        itFileChooser(model.tempExcl, acceptEmpty = true) {
                            addClass(css.tempexcl)
                        }
                    }

                    field("Restraint 1") {
                        itFileChooser(model.restraint1, acceptEmpty = true) {
                            addClass(css.restraint1)
                        }
                    }

                    field("Restraint  2") {
                        itFileChooser(model.restraint2, acceptEmpty = true) {
                            addClass(css.restraint2)
                        }
                    }

                    field("Restraint 3") {
                        itFileChooser(model.restraint3, acceptEmpty = true) {
                            addClass(css.restraint3)
                        }
                    }

                    field("Restraint 4") {
                        itFileChooser(model.restraint4, acceptEmpty = true) {
                            addClass(css.restraint4)
                        }
                    }

                    field("Hours") {
                        textfield(model.hours, intConverter) {
                            filterInput {
                                addClass(css.hours)
                                it.controlNewText.isInt()
                            }
                        }
                    }
                }
            }
        }
        form {
            fieldset {
                hbox(10) {
                    spacer()
                    val invalidLabel = label("Please complete all necessary fields") {
                        addClass(css.errorLabel)
                        isVisible = false
                    }
                    field {
                        button("Cancel") {
                            addClass(css.cancelButton)
                            setOnMouseClicked { currentStage?.close() }
                        }
                    }
                    field {
                        button("Create") {
                            addClass(css.createButton)
                            setOnMouseClicked {
                                val username = Username(model.user.value)
                                model.profileManager
                                    .value
                                    .perform(username, { loginModal(username.value) }) {
                                        model.commit()
                                        if (model.isValid) {
                                            model.moveFasta()
                                            model.makeProcess()
                                            currentStage?.close()
                                        }
                                        invalidLabel.isVisible = !model.isValid
                                    }

                            }
                        }
                    }
                }
            }
        }
    }
}

