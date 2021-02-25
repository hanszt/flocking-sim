package hzt.controller;

import hzt.controller.scenes.AboutController;
import hzt.controller.scenes.MainSceneController;
import hzt.controller.scenes.SceneController;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.EnumMap;

import static hzt.model.AppConstants.Scene;
import static hzt.model.AppConstants.Scene.ABOUT_SCENE;
import static hzt.model.AppConstants.Scene.MAIN_SCENE;

public class SceneManager {

    private static final Logger LOGGER = LogManager.getLogger(SceneManager.class);

    private final Stage stage;
    private final EnumMap<Scene, SceneController> sceneControllerMap;
    private SceneController curSceneController;

    public SceneManager(Stage stage) {
        this.stage = stage;
        this.sceneControllerMap = new EnumMap<>(Scene.class);
        loadFrontend();
    }

    private void loadFrontend() {
        try {
            sceneControllerMap.put(MAIN_SCENE, new MainSceneController(this));
            sceneControllerMap.put(ABOUT_SCENE, new AboutController(this));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.fatal("Something went wrong when loading fxml frontend...");
        }
    }

    public void setupScene(Scene scene) {
        curSceneController = sceneControllerMap.get(scene);
        stage.setScene(curSceneController.getScene());
        if (!curSceneController.isSetup()) {
            String message = "setting up " + scene.getEnglishDescription() + "...";
            LOGGER.info(message);
            curSceneController.setup();
        }
    }

    public Stage getStage() {
        return stage;
    }

    public SceneController getCurSceneController() {
        return curSceneController;
    }

    public EnumMap<Scene, SceneController> getSceneControllerMap() {
        return sceneControllerMap;
    }
}
