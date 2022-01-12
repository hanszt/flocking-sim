package hzt.controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.scene.Parent;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;

import static hzt.controller.AppManager.MIN_STAGE_DIMENSION;

public abstract class FXMLController {

    private static final String FXML_FILE_LOCATION = "/hzt/view/fxml/";

    protected static final Dimension2D INIT_SCENE_DIMENSION = new Dimension2D(
            MIN_STAGE_DIMENSION.getWidth() < 750 ? MIN_STAGE_DIMENSION.getWidth() : 750,
            MIN_STAGE_DIMENSION.getHeight() < 500 ? MIN_STAGE_DIMENSION.getHeight() : 500
    );

    protected static final Color INIT_BG_COLOR = Color.NAVY;

    private final Parent root;

    protected FXMLController(String fxmlFileName) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(c -> getController());
        URL url = getClass().getResource(FXML_FILE_LOCATION + fxmlFileName);
        loader.setLocation(url);
        this.root = loader.load();
    }

    protected abstract FXMLController getController();

    public Parent getRoot() {
        return root;
    }
}
