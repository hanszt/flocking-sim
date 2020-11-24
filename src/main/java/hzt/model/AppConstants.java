package hzt.model;

import javafx.geometry.Dimension2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Getter;

import static javafx.scene.paint.Color.NAVY;

public final class AppConstants {

    public static final int INIT_NUMBER_OF_BALLS = 120;
    public static final int MAX_NUMBER_OF_BALLS = 200;
    public static final int INIT_ACCELERATION_USER_SELECTED_BALL = 50;
    public static final int INIT_ATTRACTION = 3;
    public static final int INIT_REPEL_FACTOR = 10;
    public static final int INIT_REPEL_DISTANCE_FACTOR = 3;
    public static final int INIT_MAX_BALL_SIZE = 5;
    public static final int INIT_PERCEPTION_RADIUS = 25;
    public static final int INIT_MAX_SPEED = 150;
    public static final double INIT_FRICTION = 1;
    public static final boolean INIT_BOUNCE_WALLS_BUTTON_VALUE = true;
    public static final boolean INIT_SHOW_CONNECTIONS = false;
    public static final boolean INIT_SHOW_PATH = false;
    public static final boolean INIT_SHOW_VELOCITY = false;
    public static final boolean INIT_SHOW_ACCELERATION = false;
    public static final boolean INIT_SHOW_PERCEPTION = false;
    public static final boolean INIT_SHOW_REPEL_CIRCLE = false;

    public static final int INIT_FRAME_RATE = 30; // f/s
    public static final Duration INIT_FRAME_DURATION = Duration.seconds(1. / INIT_FRAME_RATE); // s/f

    public static final int MIN_RADIUS = 3;
    public static final int MAX_RADIUS = 10;
    public static final int MAX_PATH_SIZE_ALL = 200;
    public static final int MAX_PATH_SIZE = 50;
    public static final int MAX_VECTOR_LENGTH = 80;
    public static final Color INIT_UNIFORM_BALL_COLOR = Color.ORANGE;
    public static final Color INIT_SELECTED_BALL_COLOR = Color.RED;
    public static final Color INIT_BG_COLOR = NAVY;

    public static final Dimension2D MIN_STAGE_DIMENSION = new Dimension2D(750, 500);
    public static final Dimension2D INIT_SCENE_DIMENSION = new Dimension2D(1200, 800);

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    public static final String TITLE = "Flocking Simulation";
    public static final String DOTTED_LINE = "----------------------------------------------------------------------------------------\n";
    public static final String CLOSING_MESSAGE = ANSI_BLUE + "See you next Time! :)" + ANSI_RESET;
    public static final float STAGE_OPACITY = 0.8f;

    private AppConstants() {
    }

    @Getter
    public enum Scene {

        MAIN_SCENE("mainScene.fxml", "Main Scene"),
        ABOUT_SCENE("aboutScene.fxml", "About Scene");

        private final String fxmlFileName;
        private final String englishDescription;

        Scene(String fxmlFileName, String englishDescription) {
            this.fxmlFileName = fxmlFileName;
            this.englishDescription = englishDescription;
        }

    }
}
