package hzt.controller.about_scene;

import hzt.controller.AbstractSceneController;
import hzt.controller.AppManager;

import static hzt.controller.AppConstants.Scene.ABOUT_SCENE;
import static hzt.controller.AppConstants.Scene.MAIN_SCENE;

public class AboutController extends AbstractSceneController {

    private final AboutSceneService as;

    public AboutController(AppManager appManager) {
        super(ABOUT_SCENE.getEnglishDescription(), ABOUT_SCENE.getFxmlFileName(), appManager);
        as = new AboutSceneService();
    }

    @Override
    public void setup() {

    }

    protected AbstractSceneController getBean() {
        return this;
    }

    public void goBack() {
        appManager.setupScene(MAIN_SCENE);
    }
}
