package hzt.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public abstract class FXMLController {

    private static final String FXML_FILE_LOCATION = "/fxml/";

    private final Parent root;

    protected FXMLController(final String fxmlFileName) throws IOException {
        final var loader = new FXMLLoader();
        loader.setControllerFactory(_ -> this);
        final var url = getClass().getResource(FXML_FILE_LOCATION + fxmlFileName);
        loader.setLocation(url);
        this.root = loader.load();
    }

    public Parent getRoot() {
        return root;
    }
}
