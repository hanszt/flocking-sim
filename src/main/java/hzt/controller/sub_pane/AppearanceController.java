package hzt.controller.sub_pane;

import hzt.controller.FXMLController;
import hzt.controller.SceneManager;
import hzt.controller.scenes.MainSceneController;
import hzt.controller.scenes.SceneController;
import hzt.model.Resource;
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
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import static hzt.model.AppConstants.INIT_BG_COLOR;
import static java.lang.Boolean.TRUE;

public class AppearanceController extends FXMLController {

    @FXML
    private ToggleButton opacityStageButton;
    @FXML
    private ComboBox<Resource> backgroundCombobox;
    @FXML
    private ComboBox<Resource> themeCombobox;
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

    public final void configureStageControlButtons() {
        Stage stage = mainSceneController.getSceneManager().getStage();
        fullScreenButton.setOnAction(e -> stage.setFullScreen(!stage.isFullScreen()));
        stage.fullScreenProperty().addListener((o, c, n) -> fullScreenButton.setSelected(n));
        stage.addEventFilter(KeyEvent.KEY_PRESSED, key -> setFullScreenWhenF11Pressed(stage, key));
        bindStageOpacityToOpacityButton(stage);
    }

    private static void setFullScreenWhenF11Pressed(Stage stage, KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.F11) {
            stage.setFullScreen(!stage.isFullScreen());
        }
    }

    private void bindStageOpacityToOpacityButton(Stage stage) {
        opacityStageButton.selectedProperty().addListener((o, c, n) -> stage.setOpacity(TRUE.equals(n) ? .8 : 1));
    }

    private void configureComboBoxes() {
        themeCombobox.getItems().addAll(themeService.getThemes());
        themeService.currentThemeProperty().bind(themeCombobox.valueProperty());
        themeService.styleSheetProperty().addListener(this::changeStyle);
        themeCombobox.setValue(IThemeService.DEFAULT_THEME);

        backgroundCombobox.getItems().addAll(backgroundService.getResources());
        backgroundCombobox.setValue(BackgroundService.NO_PICTURE);
    }


    public void changeStyle(ObservableValue<? extends String> o, String c, String newVal) {
        SceneManager sceneManager = mainSceneController.getSceneManager();
        Collection<SceneController> sceneControllers = sceneManager.getSceneControllerMap().values();
        for (SceneController sceneController : sceneControllers) {
            ObservableList<String> styleSheets = sceneController.getScene().getStylesheets();
            styleSheets.removeIf(filter -> !styleSheets.isEmpty());
            if (newVal != null) {
                styleSheets.add(newVal);
            }
        }
    }

    @FXML
    private void backgroundComboBoxAction() {
        Optional.of(backgroundCombobox)
                .map(ComboBoxBase::getValue)
                .map(Resource::getInputStream)
                .map(Image::new)
                .ifPresentOrElse(this::setBackgroundImage, this::backgroundColorPickerAction);
    }

    @FXML
    private void backgroundColorPickerAction() {
        mainSceneController.getAnimationPane().setBackground(new Background(
                new BackgroundFill(backgroundColorPicker.getValue(), CornerRadii.EMPTY, Insets.EMPTY)));
        backgroundCombobox.setValue(BackgroundService.NO_PICTURE);
    }

    @Override
    protected FXMLController getController() {
        return this;
    }

    private void setBackgroundImage(Image image) {
        AnchorPane animationPane = mainSceneController.getAnimationPane();
        animationPane.setBackground(new Background(new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(animationPane.getWidth(), animationPane.getHeight(),
                        false, false, false, true))));
    }
}
