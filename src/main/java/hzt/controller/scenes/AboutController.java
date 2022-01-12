package hzt.controller.scenes;

import hzt.controller.SceneManager;
import hzt.service.AboutService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class AboutController extends SceneController {
    @FXML
    private ComboBox<AboutService.AboutText> textComboBox;
    @FXML
    private TextArea textArea;

    private final AboutService aboutService = new AboutService();

    public AboutController(SceneManager sceneManager) throws IOException {
        super(Scene.ABOUT_SCENE.getFxmlFileName(), sceneManager);
    }

    @Override
    public void setup() {
        textArea.setPrefSize(INIT_SCENE_DIMENSION.getWidth(), INIT_SCENE_DIMENSION.getHeight());
        textArea.setEditable(false);
        textComboBox.getItems().addAll(aboutService.loadContent());
        textComboBox.setValue(textComboBox.getItems().get(0));
    }

    @FXML
    private void textComboboxAction() {
        textArea.setText(textComboBox.getValue().text());
    }

    @FXML
    private void goBack() {
        sceneManager.setupScene(Scene.MAIN_SCENE);
    }

    protected SceneController getController() {
        return this;
    }
}
