package itasser.app.mytasser.app.styles

import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import tornadofx.*

class MainStylee : Stylesheet() {
    companion object {
        // Define our styles
        val transparentButton by cssclass()
        val paddedImage2 by cssclass()
        // Define our colors
        val dangerColor = c("#a94442")
        val hoverColor = c("#d49942")
    }

    init {
        transparentButton {
            backgroundColor += c(0, 0, 0, 0.0)
            hover {
                backgroundColor += c(0, 0, 0, 0.5)
            }
        }

        paddedImage2 {
            padding = box(20.0.px)
            borderWidth = multi(box(20.0.px))
            borderColor = multi(box(Color.GREEN))
            borderStyle = multi(BorderStrokeStyle.DOTTED)
        }

        text {
            padding = box(20.0.px)
        }

    }
}