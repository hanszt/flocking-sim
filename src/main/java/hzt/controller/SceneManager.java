package hzt.controller;

import hzt.controller.scenes.AboutController;
import hzt.controller.scenes.MainSceneController;
import hzt.controller.scenes.SceneController;
import hzt.controller.scenes.SceneType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
import kotlin.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Clock;
import java.util.EnumMap;
import java.util.Map;

public final class SceneManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SceneManager.class);

    private final Clock clock;
    private final Random random;
    private final Stage stage;
    private final Map<SceneType, SceneController> sceneControllerMap = new EnumMap<>(SceneType.class);
    private final ObjectProperty<SceneController> curSceneController = new SimpleObjectProperty<>();

    public SceneManager(final Clock clock, final Random random, final Stage stage) {
        this.clock = clock;
        this.random = random;
        this.stage = stage;
        curSceneController.addListener((_, _, n) -> LOGGER.info("Current scene controller changed to {}...", n.getClass().getSimpleName()));
        loadFrontend();
    }

    private void loadFrontend() {
        try {
            sceneControllerMap.put(SceneType.MAIN_SCENE, new MainSceneController(this));
            sceneControllerMap.put(SceneType.ABOUT_SCENE, new AboutController(this));
        } catch (final IOException e) {
            LOGGER.error("Something went wrong when loading fxml frontend...", e);
        }
    }

    public void setupScene(final SceneType sceneType) {
        curSceneController.set(sceneControllerMap.get(sceneType));
        final var sceneController = curSceneController.get();
        stage.setScene(sceneController.getScene());
        if (!sceneController.isSetup()) {
            LOGGER.info("setting up {}...", sceneType.getEnglishDescription());
            sceneController.setup();
        }
    }

    public Stage getStage() {
        return stage;
    }

    public Clock getClock() {
        return clock;
    }

    public Random getRandom() {
        return random;
    }

    public SceneController getCurSceneController() {
        return curSceneController.get();
    }

    public Map<SceneType, SceneController> getSceneControllerMap() {
        return sceneControllerMap;
    }
}
