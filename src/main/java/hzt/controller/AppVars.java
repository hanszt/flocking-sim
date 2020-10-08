package hzt.controller;

import javafx.animation.Timeline;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public abstract class AppVars {

    final Stage stage;
    final Map<AppConstants.Scene, AbstractSceneController> sceneControllerMap;
    AbstractSceneController curSceneController;

    public AppVars(Stage stage) {
        this.stage = stage;
        this.sceneControllerMap = new HashMap<>();
    }

    public Stage getStage() {
        return stage;
    }

    public AbstractSceneController getCurSceneController() {
        return curSceneController;
    }
}
