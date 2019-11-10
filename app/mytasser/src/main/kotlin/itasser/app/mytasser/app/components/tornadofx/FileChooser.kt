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
    acceptEmpty: Boolean = false,
    disableManual: Boolean = false,
    op: FileChooser .() -> Unit = {}
): FileChooser = FileChooser(property, type, acceptEmpty, disableManual).also { it.root.attachTo(this) }.apply(op)

class FileChooser(
    val property: Property<SimpleObjectProperty<Path?>>,
    private val type: FileChooserType = File,
    private val acceptEmpty: Boolean = false,
    private val disableManual: Boolean = false
) :
    View("File Choose"), Logger {
    private lateinit var input: TextField
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
                addClass("file-chooser-input")
                validator {
                    val resolveText = text?.replace("~", System.getProperty("user.home")) ?: ""
                    val path = Paths.get(resolveText)
                    if (resolveText.isBlank() && !acceptEmpty)
                        error("Path can't be empty")
                    else if (type == Directory)
                        if (!Files.isDirectory(path))
                            error("Must be valid directory")
                        else null
                    else if (type == File && !Files.isRegularFile(path) or !Files.exists(path))
                        if (acceptEmpty && resolveText.isBlank())
                            null
                        else error("Must be a valid file")
                    else if (!Files.exists(path))
                        error("File does not exist")
                    else null
                }
            }
            if (disableManual) {
                checkbox {
                    tooltip(
                        """This field is automatically generated based on your user. 
                            |You can manually set a dirctory when this is selected. """.trimMargin()
                    )
                    addClass("set-manually-toggle")
                    input.enableWhen { selectedProperty() }
                }
            }

            button(graphic = sicon) {
                disableProperty().bind(input.disableProperty())
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
val intConverter
    get() = object : StringConverter<SimpleObjectProperty<Int?>>() {
        override fun toString(path: SimpleObjectProperty<Int?>) =
            path.value?.toString()

        override fun fromString(string: String?): SimpleObjectProperty<Int?> {
            val res = when (string?.isInt()) {
                true -> string.toInt()
                else -> null
            }
            return SimpleObjectProperty(res)
        }
    }