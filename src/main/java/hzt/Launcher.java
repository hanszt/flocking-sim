package hzt;

import hzt.controller.AppManager;
import javafx.application.Application;
import javafx.stage.Stage;

import static hzt.controller.AppConstants.Scene.MAIN_SCENE;

public class Launcher extends Application {

    public static void main(String[] args) {
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
