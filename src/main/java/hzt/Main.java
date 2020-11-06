package hzt;

import hzt.controller.AppManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static hzt.controller.AppConstants.Screen.MAIN_SCENE;

public class Main extends Application {


    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("hoi");
        launch();
    }

    @Override
    public void start(Stage stage) {
        AppManager appManager = new AppManager(stage);
        appManager.configureStage(stage);
        appManager.setupScene(MAIN_SCENE);
        stage.show();
    }

}
