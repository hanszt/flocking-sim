package hzt.controller.scenes;

import hzt.controller.AppManager;
import hzt.controller.FXMLController;
import hzt.controller.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Instant;

/// An abstract controller class that manages a specific scene in a JavaFX application
/// using an associated FXML file. It serves as a base class for defining the behavior
/// and lifecycle of individual scenes and their controllers.
///
/// Responsibilities include:
/// - Loading and managing the FXML layout of the scene.
/// - Providing a way to interact with the `SceneManager` to manage scene transitions.
/// - Implementing lifecycle events like setup for the scene.
/// - Handling user interactions and triggering specific behaviors such as quitting
///   the application or navigating to other scenes.
///
/// The class also maintains state for the initial dimensions of scenes, along with
/// metadata like the simulation start time and references to the associated
/// `Scene` and `SceneManager`.
public abstract class SceneController extends FXMLController {

    public static final Dimension2D INIT_SCENE_DIMENSION = new Dimension2D(600, 800);

    private boolean setup;

    protected final SceneManager sceneManager;
    protected final Scene scene;
    protected final Instant startTimeSim;

    protected SceneController(final String fxmlFileName, final SceneManager sceneManager) throws IOException {
        super(fxmlFileName);
        this.startTimeSim = sceneManager.getClock().instant();
        this.sceneManager = sceneManager;
        scene = new Scene(getRoot(), INIT_SCENE_DIMENSION.getWidth(), INIT_SCENE_DIMENSION.getHeight());
    }

    public abstract void setup();

    @FXML
    void newInstance() {
        new AppManager(sceneManager.getClock(), sceneManager.getRandom(), new Stage()).start();
    }

    @FXML
    void quitInstance() {
        sceneManager.getStage().close();
    }

    @FXML
    void exitProgram() {
        Platform.exit();
    }

    @FXML
    void showAbout() {
        sceneManager.setupScene(hzt.controller.scenes.SceneType.ABOUT_SCENE);
    }

    public boolean isSetup() {
        final var temp = setup;
        setup = true;
        return temp;
    }

    public Scene getScene() {
        return scene;
    }

    public Instant getStartTimeSim() {
        return startTimeSim;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }
}
