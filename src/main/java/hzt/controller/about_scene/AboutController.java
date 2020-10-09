package hzt.controller.about_scene;

import hzt.controller.AbstractSceneController;
import hzt.controller.AppManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import static hzt.controller.AppConstants.Scene.ABOUT_SCENE;
import static hzt.controller.AppConstants.Scene.MAIN_SCENE;

public class AboutController extends AbstractSceneController {

    public AboutController(AppManager appManager) {
        super(ABOUT_SCENE.getEnglishDescription(), ABOUT_SCENE.getFxmlFileName(), appManager);
    }

    @Override
    public void setup() {

    }

    @FXML
    public void goBack() {
        appManager.setupScene(MAIN_SCENE);
    }

    protected AbstractSceneController getBean() {
        return this;
    }

}
