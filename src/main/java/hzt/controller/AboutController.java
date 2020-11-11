package hzt.controller;

import javafx.fxml.FXML;

import java.io.IOException;

import static hzt.model.AppConstants.Scene.ABOUT_SCENE;
import static hzt.model.AppConstants.Scene.MAIN_SCENE;

public class AboutController extends AbstractSceneController {

    public AboutController(SceneManager sceneManager) throws IOException {
        super(ABOUT_SCENE.getFxmlFileName(), sceneManager);
    }

    @Override
    public void setup() {

    }

    @FXML
    public void goBack() {
        sceneManager.setupScene(MAIN_SCENE);
    }

    protected AbstractSceneController getBean() {
        return this;
    }

}
