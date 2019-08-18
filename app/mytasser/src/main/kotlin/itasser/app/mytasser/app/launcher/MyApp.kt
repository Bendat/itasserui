package itasser.app.mytasser.app.launcher

import tornadofx.App

class MyApp: App(::class, Styles::class)

/**
 * The main method is needed to support the mvn jfx:run goal.
 */
fun main(args: Array<String>) {
    Application.launch(MyApp::class.java, *args)
}