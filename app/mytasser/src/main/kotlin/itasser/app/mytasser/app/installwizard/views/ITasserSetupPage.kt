package itasser.app.mytasser.app.installwizard.views

import itasser.app.mytasser.app.components.extensions.textinput
import itasser.app.mytasser.app.installwizard.viewmodel.InstallWizardViewModel
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.TextField
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


fun ValidationContext.validateDirectory(path: Path): ValidationMessage? {
    return if (!Files.exists(path)) error("Directory does not exist")
    else if (!Files.isDirectory(path)) error("Must be a directory")
    else null
}

fun ValidationContext.validateFile(path: Path): ValidationMessage? {
    return if (!Files.exists(path)) error("File does not exist")
    else if (!Files.isRegularFile(path)) error("Must be a File")
    else null
}

class ITasserSetupPage : View("ITasser setup"), InstallWizardPage {
    override val model by inject<InstallWizardViewModel>()
    override val complete = model.valid(
        model.pkgDir, model.libDir,
        model.javaHome, model.dataDir
    )

    private val nullMessage get() = "Must not be empty"
    override val root: Parent = form {
        fieldset("ITasser Parameters", labelPosition = Orientation.VERTICAL) {
            field("Package Dir") {
                textinput(model.pkgDir) {
                    addClass("pkgdir")
                    promptText = "Directory containing the runITASSER.pl script"
                    validator { validatePkgdir(this@textinput, this) }
                }
            }

            field("Lib Dir") {
                textinput(model.libDir) {
                    addClass("libdir")
                    promptText = "Directory of the ITASSER template library"
                    validator { validateDirectory(this@textinput, this) }
                }
            }

            field("Data Dir") {
                textinput(model.dataDir) {
                    addClass("datadir")
                    promptText = "Directory where datadirs will be created for sequences"
                    validator { validateDirectory(this@textinput, this) }
                }
            }

            field("Java Home") {
                textinput(model.javaHome) {
                    addClass("java_home")
                    promptText = "The directory containing bin/java"
                    validator { validateDirectory(this@textinput, this) }
                }
            }
            field("Run Style") {
                val items = FXCollections.observableArrayList(
                    arrayListOf("serial", "parallel", "gnuparallel")
                )
                combobox<String>(model.runStyle, items) {
                    validator {
                        if (text == null) {
                            error(nullMessage)
                        } else {
                            if (this@combobox.selectedItem.isNullOrBlank())
                                error("Combobox must be Selected")
                            else null
                        }
                    }
                }
            }
        }
    }

    private fun validateDirectory(
        textField: TextField,
        validationContext: ValidationContext
    ): ValidationMessage? {
        return if (textField.text == null) {
            validationContext.error(nullMessage)
        } else {
            val chosen = Paths.get(textField.text)
            validationContext.validateDirectory(chosen)
        }
    }

    private fun validatePkgdir(
        textField: TextField,
        validationContext: ValidationContext
    ): ValidationMessage? {
        return if (textField.text == null) {
            validationContext.error(nullMessage)
        } else {
            val chosen = Paths.get(textField.text)
            val hasname = Files.exists(chosen.resolve("run-ITASSER.pl"))
            if (hasname)
                validationContext.validateFile(chosen.resolve("run-ITASSER.pl"))
            else validationContext.error("Could not find 'run-ITASSER.pl'")
        }
    }
}