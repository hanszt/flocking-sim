package hzt.controller.sub_pane;

import hzt.controller.FXMLController;
import hzt.controller.SceneManager;
import hzt.controller.scenes.MainSceneController;
import hzt.controller.scenes.SceneController;
import hzt.model.Resource;
import hzt.model.Theme;
import hzt.service.BackgroundService;
import hzt.service.IBackgroundService;
import hzt.service.IThemeService;
import hzt.service.ThemeService;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static hzt.model.AppConstants.INIT_BG_COLOR;
import static java.lang.Boolean.TRUE;

public class AppearanceController extends FXMLController {

    @FXML
    private ToggleButton opacityStageButton;
    @FXML
    private ComboBox<Resource> backgroundCombobox;
    @FXML
    private ComboBox<Theme> themeCombobox;
    @FXML
    private ToggleButton fullScreenButton;
    @FXML
    private ColorPicker backgroundColorPicker;

    private final IThemeService themeService = new ThemeService();
    private final IBackgroundService backgroundService = new BackgroundService();
    private final MainSceneController mainSceneController;

    public AppearanceController(MainSceneController mainSceneController) throws IOException {
        super("appearancePane.fxml");
        this.mainSceneController = mainSceneController;
        configureComboBoxes();
        configureColorPickers();
        configureStageControlButtons();
    }

    private void configureColorPickers() {
        backgroundColorPicker.setValue(INIT_BG_COLOR);
        mainSceneController.getBackgroundColorPicker().valueProperty()
                .bindBidirectional(backgroundColorPicker.valueProperty());
    }

    public void configureStageControlButtons() {
        Stage stage = mainSceneController.getSceneManager().getStage();
        fullScreenButton.setOnAction(e -> stage.setFullScreen(!stage.isFullScreen()));
        stage.fullScreenProperty().addListener((o, c, n) -> fullScreenButton.setSelected(n));
        stage.addEventFilter(KeyEvent.KEY_PRESSED, key -> setFullScreenWhenF11Pressed(stage, key));
        bindStageOpacityToOpacityButton(stage);
    }

    private void setFullScreenWhenF11Pressed(Stage stage, KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.F11) {
            stage.setFullScreen(!stage.isFullScreen());
        }
    }

    private void bindStageOpacityToOpacityButton(Stage stage) {
        opacityStageButton.selectedProperty().addListener((o, c, n) -> stage.setOpacity(TRUE.equals(n) ? .8 : 1));
    }


    private void configureComboBoxes() {
        themeService.getThemes().forEach(theme -> themeCombobox.getItems().add(theme));
        themeService.currentThemeProperty().bind(themeCombobox.valueProperty());
        themeService.styleSheetProperty().addListener(this::changeStyle);
        themeCombobox.setValue(IThemeService.DEFAULT_THEME);

        backgroundService.getResources().forEach(r -> backgroundCombobox.getItems().add(r));
        backgroundCombobox.setValue(BackgroundService.NO_PICTURE);
    }


    public void changeStyle(ObservableValue<? extends String> o, String c, String newVal) {
        SceneManager sceneManager = mainSceneController.getSceneManager();
        Collection<SceneController> sceneControllers = sceneManager.getSceneControllerMap().values();
        for (SceneController sceneController : sceneControllers) {
            ObservableList<String> styleSheets = sceneController.getScene().getStylesheets();
            styleSheets.removeIf(filter -> !styleSheets.isEmpty());
            if (newVal != null) styleSheets.add(newVal);
        }
    }

    @FXML
    private void backgroundComboBoxAction() {
        String path = backgroundCombobox.getValue().getPathToResource();
        path = path != null ? path : "";
        if (!path.isEmpty()) {
            InputStream inputStream = backgroundService.getClass().getResourceAsStream(path);
            Image image = new Image(inputStream);
            AnchorPane animationPane = mainSceneController.getAnimationPane();
            animationPane.setBackground(background(image, animationPane));
        } else backgroundColorPickerAction();
    }

    @NotNull
    private Background background(Image image, AnchorPane animationPane) {
        return new Background(new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(animationPane.getWidth(), animationPane.getHeight(),
                        false, false, false, true)));
    }

    @FXML
    private void backgroundColorPickerAction() {
        mainSceneController.getAnimationPane().setBackground(new Background(
                new BackgroundFill(backgroundColorPicker.getValue(), CornerRadii.EMPTY, Insets.EMPTY)));
        backgroundCombobox.setValue(BackgroundService.NO_PICTURE);
    }

    @Override
    protected FXMLController getBean() {
        return this;
    }
}
