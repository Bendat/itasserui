package itasserui.app.mytasser

import com.github.javafaker.Faker
import itasser.app.mytasser.TestApp
import itasserui.common.utils.FakeKt
import javafx.scene.Scene
import javafx.scene.control.DialogPane
import javafx.stage.Stage
import javafx.stage.Window
import org.testfx.api.FxRobot
import org.testfx.framework.spock.ApplicationSpec
import org.testfx.service.query.NodeQuery
import spock.lang.Stepwise
import tornadofx.UIComponent

import java.nio.file.Files
import java.nio.file.Path

import static org.junit.Assert.assertNotNull

abstract class AppSpec<T extends UIComponent> extends ApplicationSpec {
    Path tmpdirPath = Files.createTempDirectory("e2e").toAbsolutePath()
    Stage stage = null
    Faker fake = FakeKt.getFake()
    T view = null

    String password = fake.internet().password(8, 10, true, true, true) + "A]a"
    String username = "admin"
    String email = fake.internet().emailAddress()

    Path pkg = tmpdirPath.resolve("runI-TASSER.pl").toAbsolutePath()
    Path datadir = tmpdirPath.resolve("datadir").toAbsolutePath()
    Path libdir = tmpdirPath.resolve("lib").toAbsolutePath()
    Path javaHome = tmpdirPath.resolve("jdk").toAbsolutePath()


    void setup() {
        System.setProperty("itasserui.testmode", true.toString())
        System.setProperty("itasser.home", tmpdirPath.toString())
        print("Test is: ${this.class.simpleName}")
    }

    @Override
    void start(Stage stage) {
        this.view = create()
        def scene = new Scene(view.root)
        stage.setScene(scene)
        stage.show()
        this.stage = stage
    }

    abstract T create()

    void clearText(String selector) {
        clearText(lookup(selector))
    }

    void clearText(NodeQuery selector) {
        interact { selector.queryTextInputControl().clear() }
    }

    /**
     * Checks the current alert dialog displayed (on the top of the window stack) has the expected contents.
     *
     * From https://stackoverflow.com/a/48654878/8355496
     * Licenced under cc by-sa 3.0 with attribution required https://creativecommons.org/licenses/by-sa/3.0/
     * @param expectedHeader Expected header of the dialog
     * @param expectedContent Expected content of the dialog
     */
    private void alertDialogHasHeaderAndContent(final String expectedHeader, final String expectedContent) {
        final Stage actualAlertDialog = getTopModalStage();
        assertNotNull(actualAlertDialog);

        final DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(expectedHeader, dialogPane.getHeaderText());
        assertEquals(expectedContent, dialogPane.getContentText());
    }

    String alertHeader() {
        final Stage actualAlertDialog = getTopModalStage();
        assertNotNull("Alert dialog should exist", actualAlertDialog);
        final DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot()
        return dialogPane.getHeaderText()
    }

    String alertContent() {
        final Stage actualAlertDialog = getTopModalStage();
        assertNotNull("Alert dialog should exist", actualAlertDialog);
        final DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot()
        return dialogPane.getContentText()
    }

    /**
     * Get the top modal window.
     *
     * Adapted from https://stackoverflow.com/a/48654878/8355496
     * Licenced under cc by-sa 3.0 with attribution required https://creativecommons.org/licenses/by-sa/3.0/
     * @return the top modal window
     */
    Stage getTopModalStage() {
        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        // It is needed to get the first found modal window.
        final List<Window> allWindows = new ArrayList<>(new FxRobot().robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        return (Stage) allWindows
                .stream()
                .filter { window -> window instanceof Stage }
                .findFirst()
                .orElse(null)
    }
}
