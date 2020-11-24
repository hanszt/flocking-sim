package hzt.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.IOException;

import static hzt.model.AppConstants.INIT_SCENE_DIMENSION;
import static hzt.model.AppConstants.Scene.ABOUT_SCENE;
import static hzt.model.AppConstants.Scene.MAIN_SCENE;

public class AboutController extends AbstractSceneController {

    @FXML
    private TextArea textArea;

    public AboutController(SceneManager sceneManager) throws IOException {
        super(ABOUT_SCENE.getFxmlFileName(), sceneManager);
    }

    @Override
    public void setup() {
        textArea.setPrefSize(INIT_SCENE_DIMENSION.getWidth(), INIT_SCENE_DIMENSION.getHeight());
    }

    @FXML
    public void goBack() {
        sceneManager.setupScene(MAIN_SCENE);
    }

    protected AbstractSceneController getBean() {
        return this;
    }

}
