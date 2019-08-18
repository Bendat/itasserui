package itasserui.app.mytasser

import com.github.javafaker.Faker
import itasserui.common.utils.FakeKt
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec
import spock.lang.Shared
import spock.lang.Stepwise
import tornadofx.UIComponent

@Stepwise
abstract class AppSpec<T extends UIComponent> extends ApplicationSpec {
    @Shared
    Stage stage = null
    Faker fake = FakeKt.getFake()
    public T view = null

    void setup() {
        System.setProperty("itasserui.testmode", true.toString())
    }

    @Override
    void init() throws Exception {
        FxToolkit.registerStage { new Stage(StageStyle.UNIFIED) }
    }

    @Override
    void start(Stage stage) {
        if (this.stage == null) {
            this.view = create()
            def scene = new Scene(view.root)
            stage.setScene(scene)
            stage.show()
            this.stage = stage
        }
    }

    abstract T create()

    void clearText(String selector) {
        interact { lookup(selector).queryTextInputControl().clear() }
    }
}
