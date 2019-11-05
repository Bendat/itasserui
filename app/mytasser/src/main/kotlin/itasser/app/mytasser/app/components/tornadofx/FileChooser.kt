package itasser.app.mytasser.app.components.tornadofx

import tornadofx.View
import tornadofx.button
import tornadofx.hbox
import tornadofx.textfield

enum class FileChooserType {
    Directory,
    File
}

class FileChooser() : View("File Choose") {
    override val root = hbox {
        textfield {  }
        button {}
    }
}