package itasser.app.mytasser.app.process.newDialog

import itasser.app.mytasser.app.components.tornadofx.FileChooserType
import itasser.app.mytasser.app.components.tornadofx.itFileChooser
import itasser.app.mytasser.app.components.tornadofx.pathConverter
import itasserui.lib.process.Arg
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TextField
import tornadofx.*
import java.nio.file.Path

class NewProcessDialog : View("New Process Dialog") {
    val model: NewProcessDialogModel by inject()
    override val complete: BooleanExpression = model.valid(
        model.dataDir, model.outDir, model.seqName,
        model.tempExcl, model.restraint1, model.restraint2,
        model.restraint3, model.restraint4
    )
    @Suppress("UNCHECKED_CAST")
    override val root = vbox {
        print("Hello world")
        prefWidth = 400.0
        prefHeight = 690.0
        lateinit var user: Field
        form {
            fieldset() {
                user = field("User") {
                    combobox(null, listOf("Earl.gray", "Steve,Man")) {
                        isEditable = true
                    }
                }
            }
        }
        separator { }
        scrollpane {
            lateinit var nameField: TextField
            form {
                fieldset("Sequence details") {

                    spacer { }

                    field("Name") {
                        nameField = textfield {
                            user.prefWidthProperty().bind(user.widthProperty())
                        }
                    }
                    spacer { }
                    vbox {
                        label("Description")
                        textarea {
                            prefColumnCount = 20
                            prefRowCount = 5
                            isWrapText = true
                        }
                    }

                }
                separator { }

                fieldset("ITasser Required Parameters") {
                    field("Sequence Name") {
                        textfield(model.seqName, pathConverter) {
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
                        itFileChooser(model.dataDir, FileChooserType.Directory) {
                            validator {
                                if (text.isNullOrBlank()) error("Sequence name must not be empty")
                                else null
                            }
                        }
                    }

                    field("Out Directory") {
                        itFileChooser(model.outDir, FileChooserType.Directory) {
                            validator(ValidationTrigger.OnBlur) {
                                if (text.isNullOrBlank()) error("Sequence name must not be empty")
                                else null
                            }
                        }
                    }


                }
                separator { }

                fieldset("Default parameters") {

                    field("ID Cut") {
                        val type = Arg.IdCut.argType
                        spinner(
                            type.range.start,
                            type.range.endInclusive,
                            type.default,
                            0.1,
                            true,
                            model.idCut,
                            true
                        ) {
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
                            true
                        ) {
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
                            model.nTemp,
                            true
                        ) {
                            maxWidth = 80.0
                        }
                    }

                    field("EC") {
                        checkbox(null, model.ec) { }
                    }

                    field("EBS") {
                        checkbox(null, model.lbs) { }
                    }

                    field("GO") {
                        checkbox(null, model.go) { }
                    }

                    field("Light") {
                        checkbox { }
                    }

                }
                separator { }

                fieldset("Optional Parameters") {
                    field("Homoflag") {
                        val items = Arg
                            .HomoFlag
                            .argType
                            .range
                        combobox(model.homoFlag, items) { }
                    }

                    field("Temp Exclude") {
                        itFileChooser(model.tempExcl) {}
                    }

                    field("Restraint 1") {
                        itFileChooser(model.restraint1) {}
                    }

                    field("Restraint  2") {
                        itFileChooser(model.restraint2) {}
                    }

                    field("Restraint 3") {
                        itFileChooser(model.restraint3) {}
                    }

                    field("Restraint 4") {
                        itFileChooser(model.restraint4) {}
                    }

                    field("Traj") {
                        checkbox(null, model.traj) { }
                    }

                    field("Hours") {
                        textfield(model.hours) { }
                    }
                }
            }
        }

        form {
            fieldset {
                hbox(10) {
                    spacer()
                    field { button("Cancel") { } }
                    field {
                        button("Create") {
                            setOnMouseClicked {
                                model.commit()
                                if(model.isValid)
                                    print("foo")
                            }
                        }
                    }
                }
            }
        }
    }
}

class NewProcessController : Controller() {
    val seqDirProperty = SimpleObjectProperty<Path?>(null)
    val seqNameProperty = SimpleObjectProperty<Path?>(null)
    val dataDirProperty = SimpleObjectProperty<Path?>(null)
    val outDirProperty = SimpleObjectProperty<Path?>(null)
    val homoFlagProperty = SimpleObjectProperty(Arg.HomoFlag.argType.default)
    val idCutProperty: Property<Double> = SimpleObjectProperty(Arg.IdCut.argType.default)
    val nTempProperty = SimpleObjectProperty(Arg.NTemp.argType.default)
    val nModelProperty = SimpleObjectProperty(Arg.NModel.argType.default)
    val ecProperty = SimpleObjectProperty(Arg.EC.argType.default)
    val lbsProperty = SimpleObjectProperty(Arg.LBS.argType.default)
    val goProperty = SimpleObjectProperty(Arg.GO.argType.default)
    val tempExclProperty = SimpleObjectProperty<Path?>(null)
    val restraint1Property = SimpleObjectProperty<Path?>(null)
    val restraint2Property = SimpleObjectProperty<Path?>(null)
    val restraint3Property = SimpleObjectProperty<Path?>(null)
    val restraint4Property = SimpleObjectProperty<Path?>(null)
    val trajProperty = SimpleObjectProperty<Boolean>(null)
    val lightProperty = SimpleObjectProperty(false)
    val hoursProperty = SimpleObjectProperty<String>("")

}

class NewProcessDialogModel : ItemViewModel<NewProcessController>(NewProcessController()) {
    val seqName = bind(NewProcessController::seqNameProperty)
    val seqFile = bind(NewProcessController::seqDirProperty)
    val dataDir = bind(NewProcessController::dataDirProperty)
    val outDir = bind(NewProcessController::outDirProperty)
    val homoFlag = bind(NewProcessController::homoFlagProperty)
    val idCut = bind(NewProcessController::idCutProperty)
    val nTemp = bind(NewProcessController::nTempProperty)
    val nModel = bind(NewProcessController::nModelProperty)
    val ec = bind(NewProcessController::ecProperty)
    val lbs = bind(NewProcessController::lbsProperty)
    val go = bind(NewProcessController::goProperty)
    val tempExcl = bind(NewProcessController::tempExclProperty)
    val restraint1 = bind(NewProcessController::restraint1Property)
    val restraint2 = bind(NewProcessController::restraint2Property)
    val restraint3 = bind(NewProcessController::restraint3Property)
    val restraint4 = bind(NewProcessController::restraint4Property)
    val traj = bind(NewProcessController::trajProperty)
    val light = bind(NewProcessController::lightProperty)
    val hours = bind(NewProcessController::hoursProperty)
}

