package itasser.app.mytasser

import itasser.app.mytasser.app.mainview.SplitView
import javafx.application.Application
import tornadofx.App
import tornadofx.UIComponent
import kotlin.reflect.KClass

class MyApp : App(SplitView::class, Styles::class)

/**
 * The main method is needed to support the mvn jfx:run goal.
 */
fun main(args: Array<String>) {
    Application.launch(MyApp::class.java, *args)
}


class TestApp<T : UIComponent>(clazz: KClass<T>) : App(clazz) {
    val view = primaryView

    constructor(clazz: Class<T>): this(clazz.kotlin)
}