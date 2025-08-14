package hzt.controller;

import hzt.controller.scenes.SceneType;
import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Timer;

import static hzt.model.PropertyLoader.parsedIntAppProp;
import static hzt.utils.TimerUtilsKt.taskFor;

public final class AppManager {

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

    public AppManager(Clock clock, Stage stage) {
        this.stage = stage;
        this.sceneManager = new SceneManager(clock, stage);
        this.instance = ++instances;
    }

    public void start() {
        sceneManager.setupScene(SceneType.MAIN_SCENE);
        configureStage(stage);
        LOGGER.atInfo().setMessage(this::startingMessage).log();

        new Timer().schedule(taskFor(() -> Platform.runLater(stage::show)), 1000);

        LOGGER.info("instance {} started", instance);
    }

    private String startingMessage() {
        final var startTimeSim = sceneManager.getCurSceneController().getStartTimeSim();
        return String.format("Starting instance %d of %s at %s...%n",
                instance, TITLE, DateTimeFormatter.ofPattern("hh:mm:ss").format(startTimeSim.atZone(sceneManager.getClock().getZone())));
    }

    public void configureStage(Stage stage) {
        stage.setTitle(String.format("%s (%d)", TITLE, instance));
        stage.setMinWidth(MIN_STAGE_DIMENSION.getWidth());
        stage.setMinHeight(MIN_STAGE_DIMENSION.getHeight());
        stage.setOnCloseRequest(_ -> printClosingText());

        Optional.ofNullable(getClass().getResourceAsStream("/icons/fx-icon.png"))
                .map(Image::new)
                .ifPresent(stage.getIcons()::add);
    }

    private void printClosingText() {
        final var startTimeSim = sceneManager.getCurSceneController().getStartTimeSim();
        final var runTimeSim = java.time.Duration.between(startTimeSim, sceneManager.getClock().instant());
        LOGGER.atInfo().setMessage(() -> closingMessage(runTimeSim)).log();
    }

    private String closingMessage(Duration runTimeSim) {
        return String.format("%s%nAnimation Runtime of instance %d: %.2f seconds%n%s%n", CLOSING_MESSAGE,
                instance, runTimeSim.toMillis() / 1000.0, DOTTED_LINE);
    }
}
