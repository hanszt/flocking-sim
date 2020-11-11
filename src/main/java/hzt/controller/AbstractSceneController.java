package hzt.controller;

import hzt.view.Launcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;
import java.time.LocalTime;

import static hzt.model.AppConstants.FXML_FILE_LOCATION;
import static hzt.model.AppConstants.Scene.ABOUT_SCENE;

@Getter
public abstract class AbstractSceneController {

    private final String fxmlFileName;
    protected final SceneManager sceneManager;
    protected final Scene scene;
    protected final LocalTime startTimeSim;

    public AbstractSceneController(String fxmlFileName, SceneManager sceneManager) throws IOException {
        this.startTimeSim = LocalTime.now();
        this.fxmlFileName = fxmlFileName;
        this.sceneManager = sceneManager;
        FXMLLoader fxmlLoader = new FXMLLoader();
        setControllerFactory(fxmlLoader);
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
    }

    //to be able to pass arguments to the constructor, it's necessary to specify the controller factory of the loader
    private void setControllerFactory(FXMLLoader loader) {
        loader.setControllerFactory(c -> getBean());
        loader.setLocation(getClass().getResource(FXML_FILE_LOCATION + getFxmlFileName()));
    }

    protected abstract Object getBean();

    public abstract void setup();

    @FXML
    public void newInstance() {
        new Launcher().start(new Stage());
    }

    @FXML
    void quitInstance() {
        sceneManager.getStage().close();
    }

    @FXML
    void exitProgram() {
        System.exit(0);
    }

    @FXML
    void showAbout() {
        sceneManager.setupScene(ABOUT_SCENE);
    }

}
