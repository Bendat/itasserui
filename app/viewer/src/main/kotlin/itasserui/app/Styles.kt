package itasserui.app

import javafx.scene.paint.Color
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val countBar by cssclass()
    }

    init {
        countBar {
            backgroundColor += Color.ANTIQUEWHITE
            borderRadius = multi(box(10.px))
        }
    }
}