package itasser.app.mytasser.app.components.tornadofx

import itasser.app.mytasser.app.components.tornadofx.FileChooserType.Directory
import itasser.app.mytasser.app.components.tornadofx.FileChooserType.File
import itasser.app.mytasser.app.styles.MainStylee
import itasserui.common.logger.Logger
import javafx.stage.FileChooser.ExtensionFilter
import javafx.util.StringConverter
import lk.kotlin.observable.property.StandardObservableProperty
import tornadofx.*
import tornadofx.FileChooserMode.Single
import java.nio.file.Path
import java.nio.file.Paths

enum class FileChooserType {
    Directory,
    File
}

class FileChooser(val type: FileChooserType = File) : View("File Choose"), Logger {
    val model: FileChooserViewModel by inject()
    val controller by lazy { model.item }
    override val root = anchorpane {
        hbox {
            val sicon = imageview(resources["/icons/folder.png"]) {
                addClass(MainStylee.paddedImage2)
                this.isSmooth = true
                fitHeight = 16.0
                isPreserveRatio = true
                spacing = 10.0
            }
            val input = textfield(model.path, converter) { }
            button(graphic = sicon) {
                setOnMouseClicked {
                    val ef = arrayOf(ExtensionFilter("All Files", "*.*"))
                    val item: Path? = when (type) {
                        Directory -> chooseDirectory { }
                        File -> chooseFile("Choose a file", ef, Single).firstOrNull()
                    }?.toPath()
                    input.text = item?.toAbsolutePath()?.toString() ?: ""
                }
            }
            spacer {}
        }
    }
}

val converter
    get() = object : StringConverter<StandardObservableProperty<Path?>>() {
        override fun toString(path: StandardObservableProperty<Path?>) =
            path.value?.toAbsolutePath()?.toString() ?: ""

        override fun fromString(string: String?): StandardObservableProperty<Path?> =
            StandardObservableProperty(Paths.get(string ?: ""))
    }

class FileChooserController : Controller() {
    val path = StandardObservableProperty<Path?>(null)
}

class FileChooserViewModel : ItemViewModel<FileChooserController>(FileChooserController()) {
    val path = bind(FileChooserController::path)
}