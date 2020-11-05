package hzt.controller;

import hzt.controller.about_scene.AboutController;
import hzt.controller.main_scene.MainSceneController;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalTime;

import static hzt.controller.AppConstants.*;
import static hzt.controller.AppConstants.Screen.ABOUT_SCENE;
import static hzt.controller.AppConstants.Screen.MAIN_SCENE;
import static java.lang.System.out;

public class AppManager extends AppVars {

    LocalTime startTimeSim;
    Duration runTimeSim;
    private static int instances = 0;
    private final int instance;

    public AppManager(Stage stage) {
        super(stage);
        instance = ++instances;
        try {
            startTimeSim = LocalTime.now();
            System.out.printf("Starting instance %d of %s at %s...\n", instance, TITLE, startTimeSim.toString().substring(0, 5));
            sceneControllerMap.put(MAIN_SCENE, new MainSceneController(this));
            sceneControllerMap.put(ABOUT_SCENE, new AboutController(this));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Something went wrong when loading fxml frontend...");
        }
    }

    public void configureStage(Stage stage) {
        stage.setTitle(String.format("%s (%d)", TITLE, instance));
        stage.setMinWidth(400);
        stage.setMinHeight(750);
        stage.setOnCloseRequest(e -> printClosingText());
    }

    public void setupScene(Screen screen) {
        curSceneController = sceneControllerMap.get(screen);
        stage.setScene(curSceneController.getScene());
        curSceneController.setup();
    }

    private void printClosingText() {
        LocalTime stopTimeSim = LocalTime.now();
        runTimeSim = Duration.seconds(stopTimeSim.toSecondOfDay() - startTimeSim.toSecondOfDay());
        out.printf("%s\nAnimation Runtime of instance %d: %.2f seconds\n%s\n", CLOSING_MESSAGE,
                instance, runTimeSim.toSeconds(), DOTTED_LINE);
    }

}
