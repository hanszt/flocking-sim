package hzt.controller.scenes;

import hzt.controller.AppManager;
import hzt.controller.FXMLController;
import hzt.controller.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalTime;

import static hzt.model.AppConstants.INIT_SCENE_DIMENSION;
import static hzt.model.AppConstants.Scene.ABOUT_SCENE;

public abstract class SceneController extends FXMLController {

    private boolean setup;

    protected final SceneManager sceneManager;
    protected final Scene scene;
    protected final LocalTime startTimeSim;

    protected SceneController(String fxmlFileName, SceneManager sceneManager) throws IOException {
        super(fxmlFileName);
        this.startTimeSim = LocalTime.now();
        this.sceneManager = sceneManager;
        scene = new Scene(getRoot(), INIT_SCENE_DIMENSION.getWidth(), INIT_SCENE_DIMENSION.getHeight());
    }

    public abstract void setup();

    @FXML
    void newInstance() {
        new AppManager(new Stage()).start();
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
        sceneManager.setupScene(ABOUT_SCENE);
    }

    public boolean isSetup() {
        boolean temp = setup;
        setup = true;
        return temp;
    }

    public Scene getScene() {
        return scene;
    }

    public LocalTime getStartTimeSim() {
        return startTimeSim;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }
}
