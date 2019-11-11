package itasser.app.mytasser.app.mainview.consoletab

import tornadofx.*

class ConsoleView : View("ITasser console") {
    override val root = vbox {
        textarea {

        }

        hbox{
            button("Play")
            button("Stop")
        }
    }
}