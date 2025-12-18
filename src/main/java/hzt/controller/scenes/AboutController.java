package hzt.controller.scenes;

import hzt.controller.SceneManager;
import hzt.service.AboutService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.io.IOException;

/// A controller that manages the about-pane
public final class AboutController extends SceneController {
    @FXML
    private ComboBox<AboutService.AboutText> textComboBox;
    @FXML
    private TextArea textArea;

    private final AboutService aboutService = new AboutService();

    public AboutController(final SceneManager sceneManager) throws IOException {
        super(SceneType.ABOUT_SCENE.getFxmlFileName(), sceneManager);
    }

    @Override
    public void setup() {
        textArea.setPrefSize(INIT_SCENE_DIMENSION.getWidth(), INIT_SCENE_DIMENSION.getHeight());
        textArea.setEditable(false);
        textComboBox.getItems().addAll(aboutService.loadContent());
        textComboBox.setValue(textComboBox.getItems().getFirst());
    }

    @FXML
    private void textComboboxAction() {
        textArea.setText(textComboBox.getValue().text());
    }

    @FXML
    private void goBack() {
        sceneManager.setupScene(SceneType.MAIN_SCENE);
    }
}
