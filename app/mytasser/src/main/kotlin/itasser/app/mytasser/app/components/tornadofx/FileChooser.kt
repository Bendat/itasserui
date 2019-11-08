package itasser.app.mytasser.app.components.tornadofx

import itasser.app.mytasser.app.components.tornadofx.FileChooserType.Directory
import itasser.app.mytasser.app.components.tornadofx.FileChooserType.File
import itasser.app.mytasser.app.styles.MainStylee
import itasserui.common.logger.Logger
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.scene.control.TextField
import javafx.stage.FileChooser.ExtensionFilter
import javafx.util.StringConverter
import tornadofx.*
import tornadofx.FileChooserMode.Single
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

enum class FileChooserType {
    Directory,
    File
}

fun EventTarget.itFileChooser(
    property: Property<SimpleObjectProperty<Path?>>,
    type: FileChooserType = File,
    op: FileChooser.() -> Unit = {}
): FileChooser = FileChooser(property, type).also { it.root.attachTo(this) }.apply(op)

class FileChooser(property: Property<SimpleObjectProperty<Path?>>, val type: FileChooserType = File) :
    View("File Choose"), Logger {
    lateinit var input: TextField
    override val root = anchorpane {
        hbox {
            val sicon = imageview(resources["/icons/folder.png"]) {
                addClass(MainStylee.paddedImage2)
                this.isSmooth = true
                fitHeight = 16.0
                isPreserveRatio = true
                spacing = 10.0
            }
            input = textfield(property, pathConverter) {
                validator {
                    val path = Paths.get(text)
                    if (text.isNullOrBlank())
                        error("Path can't be empty")
                    else if (type == Directory)
                        if (!Files.isDirectory(path))
                            error("Must be valid directory")
                        else null
                    else if (type == File && !Files.isRegularFile(path) or !Files.exists(path))
                        error("Must be a valid file")
                    else if (!Files.exists(path))
                        error("File does not exist")
                    else null
                }
            }
            button(graphic = sicon) {
                setOnMouseClicked {
                    val ef = arrayOf(ExtensionFilter("All Files", "*.*"))
                    val item: String = when (type) {
                        Directory -> chooseDirectory { }
                        File -> chooseFile("Choose a file", ef, Single).firstOrNull()
                    }?.toString() ?: ""
                    input.text = item
                }
            }
            spacer {}
        }
    }

    fun validator(
        trigger: ValidationTrigger = ValidationTrigger.OnChange(),
        validator: ValidationContext.(String?) -> ValidationMessage?
    ) = input.validator(trigger, validator)
}

val pathConverter
    get() = object : StringConverter<SimpleObjectProperty<Path?>>() {
        override fun toString(path: SimpleObjectProperty<Path?>) =
            path.value?.toAbsolutePath()?.toString() ?: ""

        override fun fromString(string: String?): SimpleObjectProperty<Path?> =
            SimpleObjectProperty(Paths.get(string ?: ""))
    }