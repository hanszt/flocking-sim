package hzt.controller;

import hzt.model.AppConstants;
import javafx.stage.Stage;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.EnumMap;

import static hzt.model.AppConstants.Scene.ABOUT_SCENE;
import static hzt.model.AppConstants.Scene.MAIN_SCENE;

@Getter
public class SceneManager {

    private static final Logger LOGGER = LogManager.getLogger(SceneManager.class);

    private final Stage stage;
    private final EnumMap<AppConstants.Scene, AbstractSceneController> sceneControllerMap;
    AbstractSceneController curSceneController;

    public SceneManager(Stage stage) {
        this.stage = stage;
        this.sceneControllerMap = new EnumMap<>(AppConstants.Scene.class);
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

    public void setupScene(AppConstants.Scene scene) {
        String message = "setting up " + scene.getEnglishDescription() + "...";
        LOGGER.trace(message);
        curSceneController = sceneControllerMap.get(scene);
        stage.setScene(curSceneController.getScene());
        curSceneController.setup();
    }
}
