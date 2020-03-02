package itasserui.app.viewer

import itasserui.app.Styles
import itasserui.app.viewer.ui.Viewer
import javafx.application.Application
import tornadofx.App

class MyApp: App(Viewer::class, Styles::class)

/**
 * The main method is needed to support the mvn jfx:run goal.
 */
fun main(args: Array<String>) {
    Application.launch(MyApp::class.java, *args)
}
