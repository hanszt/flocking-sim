package hzt.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public abstract class FXMLController {

    private static final String FXML_FILE_LOCATION = "/fxml/";

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
