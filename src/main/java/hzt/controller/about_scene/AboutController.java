package hzt.controller.about_scene;

import hzt.controller.AbstractSceneController;
import hzt.controller.AppManager;
import javafx.fxml.FXML;

import java.io.IOException;

import static hzt.controller.AppConstants.Screen.ABOUT_SCENE;
import static hzt.controller.AppConstants.Screen.MAIN_SCENE;

public class AboutController extends AbstractSceneController {

    public AboutController(AppManager appManager) throws IOException {
        super(ABOUT_SCENE.getFxmlFileName(), appManager);
    }

    @Override
    public void setup() {

    }

    @FXML
    public void goBack() {
        AbstractSceneController mc = appManager.getSceneControllerMap().get(MAIN_SCENE);
        appManager.getStage().setScene(mc.getScene());
    }

    protected AbstractSceneController getBean() {
        return this;
    }

}
