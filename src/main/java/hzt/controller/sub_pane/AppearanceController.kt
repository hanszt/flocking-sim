package hzt.controller.sub_pane

import hzt.controller.FXMLController
import hzt.controller.scenes.MainSceneController
import hzt.controller.scenes.SceneController
import hzt.model.Resource
import hzt.service.BackgroundService
import hzt.service.IBackgroundService
import hzt.service.IThemeService
import hzt.service.ThemeService
import hzt.utils.inverseFullScreen
import hzt.utils.onNewValue
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.control.ColorPicker
import javafx.scene.control.ComboBox
import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.util.*

class AppearanceController(private val mainSceneController: MainSceneController) :
    FXMLController("appearancePane.fxml") {

    @FXML
    private lateinit var opacityStageButton: ToggleButton
    @FXML
    private lateinit var backgroundCombobox: ComboBox<Resource>
    @FXML
    private lateinit var themeCombobox: ComboBox<Resource>
    @FXML
    private lateinit var fullScreenButton: ToggleButton
    @FXML
    private lateinit var backgroundColorPicker: ColorPicker

    private val themeService: IThemeService = ThemeService()
    private val backgroundService: IBackgroundService = BackgroundService()

    init {
        configureComboBoxes()
        configureColorPickers()
        configureStageControlButtons()
    }

    private fun configureColorPickers() {
        backgroundColorPicker.value = INIT_BG_COLOR
        mainSceneController.backgroundColorPicker.valueProperty()
            .bindBidirectional(backgroundColorPicker.valueProperty())
        backgroundColorPicker.valueProperty().onNewValue(::backgroundColorPickerAction)
    }

    private fun configureStageControlButtons() {
        val stage = mainSceneController.sceneManager.stage
        fullScreenButton.configure(stage)
        stage.bindOpacityToOpacityButton(opacityStageButton)
    }

    private fun ToggleButton.configure(stage: Stage) {
        mainSceneController.fullScreenButton.selectedProperty().bindBidirectional(selectedProperty())
        onAction = EventHandler { stage.inverseFullScreen() }
        stage.fullScreenProperty().onNewValue { isSelected = it }
        stage.addEventFilter(KeyEvent.KEY_PRESSED) {
            if (it.code == KeyCode.F11) {
                stage.inverseFullScreen()
            }
        }
    }

    private fun Stage.bindOpacityToOpacityButton(opacityStageButton: ToggleButton) =
        opacityStageButton.selectedProperty().onNewValue { opacity = if (it) .8 else 1.0 }

    private fun configureComboBoxes() {
        themeCombobox.items.addAll(themeService.themes)
        themeService.currentThemeProperty().bind(themeCombobox.valueProperty())
        themeService.styleSheetProperty().onNewValue(::changeStyle)
        themeCombobox.value = IThemeService.DEFAULT_THEME
        backgroundCombobox.items.addAll(backgroundService.resources)
        backgroundCombobox.value = BackgroundService.NO_PICTURE
    }

    private fun changeStyle(newVal: String?) {
        val sceneManager = mainSceneController.sceneManager
        val sceneControllers: Collection<SceneController> = sceneManager.sceneControllerMap.values
        for (sceneController in sceneControllers) {
            val styleSheets = sceneController.scene.stylesheets
            styleSheets.removeIf { styleSheets.isNotEmpty() }
            Optional.ofNullable(newVal).ifPresent(styleSheets::add)
        }
    }

    @FXML
    private fun backgroundComboBoxAction() = Optional.of(backgroundCombobox)
        .map { it.value }
        .map(Resource::getInputStream)
        .map(::Image)
        .ifPresentOrElse(::setBackgroundImage) { backgroundColorPickerAction(backgroundColorPicker.value) }

    private fun backgroundColorPickerAction(newColor: Color) {
        backgroundCombobox.value = BackgroundService.NO_PICTURE
        mainSceneController.animationPane.background = Background(
            BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY)
        )
    }

    override fun getController(): FXMLController = this

    private fun setBackgroundImage(image: Image) {
        val animationPane = mainSceneController.animationPane
        animationPane.background = Background(
            BackgroundImage(
                image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                BackgroundSize(animationPane.width, animationPane.height, false, false, false, true)
            )
        )
    }

    companion object {
        val INIT_BG_COLOR = Color.NAVY
    }
}
