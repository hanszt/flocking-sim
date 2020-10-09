package hzt.controller;

import hzt.Launcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static hzt.controller.AppConstants.FXML_FILE_LOCATION;
import static hzt.controller.AppConstants.Scene.ABOUT_SCENE;

public abstract class AbstractSceneController implements Controller {

    private final String sceneName;
    private final String fxmlFileName;
    protected final AppManager appManager;
    protected Scene scene;

    public AbstractSceneController(String sceneName, String fxmlFileName, AppManager appManager) {
        this.sceneName = sceneName;
        this.fxmlFileName = fxmlFileName;
        this.appManager = appManager;
        FXMLLoader fxmlLoader = new FXMLLoader();
        setControllerFactory(fxmlLoader);
        try {
            Parent root = fxmlLoader.load();
            scene = new Scene(root);
        } catch (IOException e) {
            System.err.println("Scene could not be set...");
            e.printStackTrace();
        }
    }

    //to be able to pass arguments to the constructor, it's necessary to specify the controller factory of the loader
    private void setControllerFactory(FXMLLoader loader) {
        loader.setControllerFactory(c -> getBean());
        loader.setLocation(getClass().getResource(FXML_FILE_LOCATION + getFxmlFileName()));
    }

    @FXML
    public void newInstance() {
        new Launcher().start(new Stage());
    }

    @FXML
    void quitInstance() {
        appManager.getStage().close();
    }

    @FXML
    void exitProgram() {
        System.exit(0);
    }

    @FXML
    void showAbout(ActionEvent event) {
        appManager.setupScene(ABOUT_SCENE);
    }

    @FXML
    void showPreferences(ActionEvent event) {

    }

    public String getSceneName() {
        return sceneName;
    }

    public String getFxmlFileName() {
        return fxmlFileName;
    }

    protected abstract AbstractSceneController getBean();

    public Scene getScene() {
        return scene;
    }

    public AppManager getAppManager() {
        return appManager;
    }
}