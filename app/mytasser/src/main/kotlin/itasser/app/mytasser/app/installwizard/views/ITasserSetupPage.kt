package itasser.app.mytasser.app.installwizard.views

import itasser.app.mytasser.app.installwizard.viewmodel.InstallWizardViewModel
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.Parent
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun ValidationContext.validateDirectory(path: Path): ValidationMessage? {
    return if (!Files.exists(path)) error("Directory does not exist")
    else if (!Files.exists(path)) error("Must be a directory")
    else null
}

class ITasserSetupPage : View("ITasser setup") {
    val model by inject<InstallWizardViewModel>()
    override val complete = model.valid(model.pkgDir, model.libDir, model.javaHome, model.dataDir)
    private val nullMessage get() = "Must not be empty"
    override val root: Parent = form {
        fieldset("ITasser Parameters", labelPosition = Orientation.VERTICAL) {
            field("Package Dir") {
                textfield(model.pkgDir) {
                    addClass("pkgdir")
                    promptText = "Directory containing the runITASSER.pl script"
                    validator {
                        if (text == null) {
                            error(nullMessage)
                        } else {
                            val chosen = Paths.get(text)
                            val script = chosen.resolve("runI-TASSER.pl")
                            validateDirectory(chosen)
                                ?: if (!Files.exists(script))
                                    error("Could not find 'runI-TASSER.pl'")
                                else null
                        }

                    }
                }
            }


            field("Lib Dir") {
                textfield(model.libDir) {
                    addClass("libdir")
                    promptText = "Directory of the ITASSER template library"
                    validator {
                        if (text == null) {
                            error(nullMessage)
                        } else {
                            val chosen = Paths.get(text)
                            validateDirectory(chosen)
                        }
                    }
                }
            }
            field("Data Dir") {
                textfield(model.dataDir) {
                    addClass("datadir")
                    promptText = "Directory where datadirs will be created for sequences"
                    validator {
                        if (text == null) {
                            error(nullMessage)
                        } else {
                            val chosen = Paths.get(text)
                            validateDirectory(chosen)
                        }
                    }
                }
            }
            field("Java Home") {
                textfield(model.javaHome) {
                    addClass("java_home")
                    promptText = "The directory containing bin/java"

                    validator {
                        if (text == null) {
                            error(nullMessage)
                        } else {
                            val chosen = Paths.get(text)
                            validateDirectory(chosen)
                        }
                    }
                }
            }

            field("Run Style") {
                val items = FXCollections.observableArrayList(
                    arrayListOf("serial", "parallel", "gnuparallel")
                )
                model.runStyle.value = "gnuparallel"
                combobox<String>(model.runStyle, items) {
                    validator {
                        if(text == null){
                            error(nullMessage)
                        }
                        else{
                            if (this@combobox.selectedItem.isNullOrBlank())
                                error("Combobox must be Selected")
                            else null
                        }
                    }

                }
            }
        }

    }
}