package hzt.model;

import javafx.geometry.Dimension2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static javafx.scene.paint.Color.NAVY;

public final class AppConstants {

    private static final Logger LOGGER = LogManager.getLogger(AppConstants.class);
    private static final Properties PROPS = configProperties();

    public static final int INIT_FRAME_RATE = parsedIntAppProp("framerate", 30); // f/s
    public static final Duration INIT_FRAME_DURATION = Duration.seconds(1. / INIT_FRAME_RATE); // s/f

    public static final int MIN_SIZE = 3;

    public static final Color INIT_UNIFORM_BALL_COLOR = Color.ORANGE;
    public static final Color INIT_SELECTED_BALL_COLOR = Color.RED;
    public static final Color INIT_BG_COLOR = NAVY;

    public static final Dimension2D MIN_STAGE_DIMENSION;
    public static final Dimension2D INIT_SCENE_DIMENSION;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    public static final String TITLE = "Flocking Simulation";
    public static final String DOTTED_LINE = "----------------------------------------------------------------------------------------\n";
    public static final String CLOSING_MESSAGE = ANSI_BLUE + "See you next Time! :)" + ANSI_RESET;
    public static final double STAGE_OPACITY = parsedDoubleAppProp("stage_opacity", .8);


    private AppConstants() {
    }

    static {
        int sceneWidthProp = parsedIntAppProp("init_scene_width", 1200);
        int sceneHeightProp = parsedIntAppProp("init_scene_height", 800);
        INIT_SCENE_DIMENSION = new Dimension2D(sceneWidthProp, sceneHeightProp);
        MIN_STAGE_DIMENSION = determineMinStageDimension();
    }

    public static double parsedDoubleAppProp(String property, double defaultVal) {
        double value = defaultVal;
        String propertyVal = PROPS.getProperty(property);
        if (propertyVal != null) {
            try {
                value = Double.parseDouble(propertyVal);
            } catch (NumberFormatException e) {
                LOGGER.warn(String.format("Property '%s' with value '%s' could not be parsed to a double... " +
                        "Falling back to default: %f...", property, propertyVal, defaultVal));
            }
        } else {
            LOGGER.warn(() -> String.format("Property '%s' not found. Falling back to default: %f",
                    property, defaultVal));
        }
        return value;
    }

    public static int parsedIntAppProp(String property, int defaultVal) {
        int value = defaultVal;
        String propertyVal = PROPS.getProperty(property);
        if (propertyVal != null) {
            try {
                value = Integer.parseInt(propertyVal);
            } catch (NumberFormatException e) {
                LOGGER.warn(String.format("Property '%s' with value '%s' could not be parsed to an int... " +
                        "Falling back to default: %d...", property, propertyVal, defaultVal));
            }
        } else {
            LOGGER.warn(() -> String.format("Property '%s' not found. Falling back to default: %d",
                    property, defaultVal));
        }
        return value;
    }


    private static Dimension2D determineMinStageDimension() {
        int defaultMinStageWidth = 750;
        int defaultMinStageHeight = 500;
        double minStageWidth = INIT_SCENE_DIMENSION.getWidth() < defaultMinStageWidth ?
                INIT_SCENE_DIMENSION.getWidth() : defaultMinStageWidth;
        double minStageHeight = INIT_SCENE_DIMENSION.getHeight() < defaultMinStageHeight ?
                INIT_SCENE_DIMENSION.getHeight() : defaultMinStageHeight;
        return new Dimension2D(minStageWidth, minStageHeight);
    }

    private static Properties configProperties() {
        Properties properties = new Properties();
        String pathName = "./src/main/resources/app.properties";
        File file = new File(pathName);
        try (InputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            properties.load(stream);
        } catch (IOException e) {
            LOGGER.warn(() -> pathName + " not found...", e);
        }
        return properties;
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
