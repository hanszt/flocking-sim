package hzt.controller;

import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static hzt.model.AppConstants.*;

public class AppManager {

    private static final Logger LOGGER = LogManager.getLogger(AppManager.class);

    private static int instances = 0;
    private final int instance;
    final Stage stage;
    private final SceneManager sceneManager;

    public AppManager(Stage stage) {
        this.stage = stage;
        this.sceneManager = new SceneManager(stage);
        this.instance = ++instances;
    }

    public void start() {
        sceneManager.setupScene(Scene.MAIN_SCENE);
        configureStage(stage);
        String startingMessage = String.format("Starting instance %d of %s at %s...%n",
                instance, TITLE, sceneManager.getCurSceneController().startTimeSim.format(DateTimeFormatter.ISO_TIME));
        LOGGER.trace(startingMessage);

        stage.show();
        String startedMessage = String.format("instance %d started%n", instance);
        LOGGER.trace(startedMessage);
    }

    public void configureStage(Stage stage) {
        stage.setTitle(String.format("%s (%d)", TITLE, instance));
        stage.setMinWidth(MIN_STAGE_DIMENSION.getWidth());
        stage.setMinHeight(MIN_STAGE_DIMENSION.getHeight());
        stage.setOnCloseRequest(e -> printClosingText());
//        if (instances == 1) stage.setMaximized(true);
    }

    private void printClosingText() {
        LocalTime startTimeSim = sceneManager.getCurSceneController().startTimeSim;
        LocalTime stopTimeSim = LocalTime.now();
        Duration runTimeSim = Duration.millis((stopTimeSim.toNanoOfDay() - startTimeSim.toNanoOfDay()) / 1e6);
        String message = String.format("%s%nAnimation Runtime of instance %d: %.2f seconds%n%s%n", CLOSING_MESSAGE,
                instance, runTimeSim.toSeconds(), DOTTED_LINE);
        LOGGER.trace(message);
    }

}
