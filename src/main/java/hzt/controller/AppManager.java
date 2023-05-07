package hzt.controller;

import hzt.controller.scenes.Scene;
import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Timer;

import static hzt.model.PropertyLoader.parsedIntAppProp;
import static hzt.utils.TimerUtilsKt.taskFor;

public class AppManager {

    public static final Dimension2D MIN_STAGE_DIMENSION = new Dimension2D(
            parsedIntAppProp("init_scene_width", 1200),
    parsedIntAppProp("init_scene_height", 800));

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String TITLE = "Flocking Simulation";
    private static final String DOTTED_LINE = "----------------------------------------------------------------------------------------\n";
    private static final String CLOSING_MESSAGE = ANSI_BLUE + "See you next Time! :)" + ANSI_RESET;
    private static final Logger LOGGER = LoggerFactory.getLogger(AppManager.class);

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
        LOGGER.info("{}", startingMessage());

        new Timer().schedule(taskFor(() -> Platform.runLater(stage::show)), 1000);

        LOGGER.info("instance {} started", instance);
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
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("{}", closingMessage(runTimeSim));
        }
    }

    private String closingMessage(Duration runTimeSim) {
        return String.format("%s%nAnimation Runtime of instance %d: %.2f seconds%n%s%n", CLOSING_MESSAGE,
                instance, runTimeSim.toSeconds(), DOTTED_LINE);
    }
}
