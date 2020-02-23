package itasserui.app.fxutils

import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.specs.StringSpec
import itasserui.common.utils.uuid
import javafx.scene.Parent
import tornadofx.Controller
import tornadofx.ItemViewModel
import tornadofx.View
import tornadofx.vbox
import java.util.*

class TestController(val id: UUID) : Controller()
class TestViewModel(controller: TestController) :
    InjectViewModel<TestController>(controller)

class TestView : View("Hi") {
    val uuid = UUID.randomUUID()
    val viewModel = TestViewModel(TestController(uuid))
    val controller by inject<TestController>()
    override val root: Parent
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

}

class InjectViewModelTest : StringSpec({
    val view = TestView()

    "Test view should generate correctly"{
        view.uuid should be(view.controller.id)
    }
})
