package hzt.view;

import hzt.controller.AppManager;
import javafx.application.Application;
import javafx.stage.Stage;
import kotlin.random.Random;

import java.time.Clock;

public final class Launcher extends Application {

    public static void main(final String[] args) {
        launch();
    }

    @Override
    public void start(final Stage stage) {
        new AppManager(Clock.systemDefaultZone(), Random.Default, stage).start();
    }

}
