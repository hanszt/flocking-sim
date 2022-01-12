package hzt.controller;

import hzt.controller.scenes.AboutController;
import hzt.controller.scenes.MainSceneController;
import hzt.controller.scenes.Scene;
import hzt.controller.scenes.SceneController;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class SceneManager {

    private static final Logger LOGGER = LogManager.getLogger(SceneManager.class);

    private final Stage stage;
    private final Map<Scene, SceneController> sceneControllerMap;
    private SceneController curSceneController;

    public SceneManager(Stage stage) {
        this.stage = stage;
        this.sceneControllerMap = new EnumMap<>(Scene.class);
        loadFrontend();
    }

    private void loadFrontend() {
        try {
            sceneControllerMap.put(Scene.MAIN_SCENE, new MainSceneController(this));
            sceneControllerMap.put(Scene.ABOUT_SCENE, new AboutController(this));
        } catch (IOException e) {
            LOGGER.fatal("Something went wrong when loading fxml frontend...", e);
        }
    }

    public void setupScene(Scene scene) {
        curSceneController = sceneControllerMap.get(scene);
        stage.setScene(curSceneController.getScene());
        if (!curSceneController.isSetup()) {
            LOGGER.info(() -> "setting up " + scene.getEnglishDescription() + "...");
            curSceneController.setup();
        }
    }

    public Stage getStage() {
        return stage;
    }

    public SceneController getCurSceneController() {
        return curSceneController;
    }

    public Map<Scene, SceneController> getSceneControllerMap() {
        return sceneControllerMap;
    }
}
