package hzt.controller;

import hzt.controller.about_scene.AboutController;
import hzt.controller.main_scene.MainSceneController;
import hzt.controller.services.AnimationService;
import javafx.stage.Stage;

import static hzt.controller.AppConstants.STAGE_OPACITY;
import static hzt.controller.AppConstants.Scene.ABOUT_SCENE;
import static hzt.controller.AppConstants.Scene.MAIN_SCENE;
import static hzt.controller.AppConstants.TITLE;

public class AppManager extends AppVars {

    private final AnimationService animationService;

    public AppManager(Stage stage) {
        super(stage);
        sceneControllerMap.put(MAIN_SCENE, new MainSceneController(this));
        sceneControllerMap.put(ABOUT_SCENE, new AboutController(this));
        animationService = new AnimationService();
    }

    public void configureStage(Stage stage) {
        stage.setTitle(TITLE);
        stage.setOpacity(STAGE_OPACITY);
    }

    public void setupScene(AppConstants.Scene scene) {
        curSceneController = sceneControllerMap.get(scene);
        stage.setScene(curSceneController.getScene());
        curSceneController.setup();
    }

    public AnimationService getAnimationService() {
        return animationService;
    }
}
