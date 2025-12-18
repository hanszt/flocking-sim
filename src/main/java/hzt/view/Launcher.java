package hzt.view;

import hzt.controller.AppManager;
import javafx.application.Application;
import javafx.stage.Stage;
import kotlin.random.Random;

import java.time.Clock;

/// The main entry-point for the app.
public final class Launcher extends Application {

    @Override
    public void start(final Stage stage) {
        new AppManager(Clock.systemDefaultZone(), Random.Default, stage).start();
    }

}
