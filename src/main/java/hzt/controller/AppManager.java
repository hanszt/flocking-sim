package hzt.controller;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Timer;

import static hzt.model.AppConstants.CLOSING_MESSAGE;
import static hzt.model.AppConstants.DOTTED_LINE;
import static hzt.model.AppConstants.MIN_STAGE_DIMENSION;
import static hzt.model.AppConstants.Scene;
import static hzt.model.AppConstants.TITLE;
import static hzt.utils.TimerUtilsKt.taskFor;

public class AppManager {

    private static final Logger LOGGER = LogManager.getLogger(AppManager.class);

    private static int instances;
    private final int instance;
    private final Stage stage;
    private final SceneManager sceneManager;

    public AppManager(Stage stage) {
        this.stage = stage;
        this.sceneManager = new SceneManager(stage);
        this.instance = ++instances;
    }

    public void start() {
        sceneManager.setupScene(Scene.MAIN_SCENE);
        configureStage(stage);
        LOGGER.info(this::startingMessage);

        new Timer().schedule(taskFor(() -> Platform.runLater(stage::show)), 1000);

        LOGGER.info(() -> String.format("instance %d started%n", instance));
    }

    private String startingMessage() {
        final LocalTime startTimeSim = sceneManager.getCurSceneController().getStartTimeSim();
        return String.format("Starting instance %d of %s at %s...%n",
                instance, TITLE, startTimeSim.format(DateTimeFormatter.ofPattern("hh:mm:ss")));
    }

    public void configureStage(Stage stage) {
        stage.setTitle(String.format("%s (%d)", TITLE, instance));
        stage.setMinWidth(MIN_STAGE_DIMENSION.getWidth());
        stage.setMinHeight(MIN_STAGE_DIMENSION.getHeight());
        stage.setOnCloseRequest(e -> printClosingText());

        Optional.ofNullable(getClass().getResourceAsStream("/icons/fx-icon.png"))
                .map(Image::new)
                .ifPresent(stage.getIcons()::add);
    }

    private void printClosingText() {
        LocalTime startTimeSim = sceneManager.getCurSceneController().getStartTimeSim();
        LocalTime stopTimeSim = LocalTime.now();
        Duration runTimeSim = Duration.millis((stopTimeSim.toNanoOfDay() - startTimeSim.toNanoOfDay()) / 1e6);
        LOGGER.info(() -> closingMessage(runTimeSim));
    }

    private String closingMessage(Duration runTimeSim) {
        return String.format("%s%nAnimation Runtime of instance %d: %.2f seconds%n%s%n", CLOSING_MESSAGE,
                instance, runTimeSim.toSeconds(), DOTTED_LINE);
    }
}
