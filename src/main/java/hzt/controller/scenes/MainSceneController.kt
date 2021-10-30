package hzt.controller.scenes

import hzt.controller.SceneManager
import hzt.controller.sub_pane.AppearanceController
import hzt.controller.sub_pane.StatisticsController
import hzt.model.AppConstants.INIT_BG_COLOR
import hzt.model.AppConstants.INIT_SELECTED_BALL_COLOR
import hzt.model.AppConstants.INIT_UNIFORM_BALL_COLOR
import hzt.model.AppConstants.STAGE_OPACITY
import hzt.model.AppConstants.Scene.*
import hzt.model.AppConstants.parsedIntAppProp
import hzt.model.FlockProperties
import hzt.model.entity.Flock
import hzt.model.entity.Flock.FlockType
import hzt.model.entity.boid.Boid
import hzt.model.utils.Engine
import hzt.model.utils.Engine.FlockingSim
import hzt.service.AnimationService
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Dimension2D
import javafx.geometry.Insets
import javafx.scene.*
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.time.LocalTime

class MainSceneController(sceneManager: SceneManager?) : SceneController(MAIN_SCENE.fxmlFileName, sceneManager) {
    @FXML
    private lateinit var appearanceTab: Tab

    @FXML
    private lateinit var statisticsTab: Tab

    @FXML
    lateinit var animationPane: AnchorPane

    @FXML
    private lateinit var physicsEngineComboBox: ComboBox<FlockingSim>

    @FXML
    private lateinit var flockSettingsComboBox: ComboBox<FlockType>

    @FXML
    private lateinit var showVelocityVectorButton: ToggleButton

    @FXML
    private lateinit var showAccelerationVectorButton: ToggleButton

    @FXML
    private lateinit var bounceWallsButton: ToggleButton

    @FXML
    private lateinit var showPathSelectedButton: ToggleButton

    @FXML
    private lateinit var showPerceptionButton: ToggleButton

    @FXML
    private lateinit var showPerceptionSelectedBallButton: ToggleButton

    @FXML
    private lateinit var showRepelCircleButton: ToggleButton

    @FXML
    private lateinit var showConnectionsButton: ToggleButton

    @FXML
    private lateinit var fullScreenButton: ToggleButton

    @FXML
    private lateinit var showAllPathsButton: ToggleButton

    @FXML
    private lateinit var uniformFlockColorPicker: ColorPicker

    @FXML
    lateinit var backgroundColorPicker: ColorPicker

    @FXML
    private lateinit var selectedBallColorPicker: ColorPicker

    @FXML
    private lateinit var numberOfBoidsSlider: Slider

    @FXML
    private lateinit var perceptionRadiusSlider: Slider

    @FXML
    private lateinit var frictionSlider: Slider

    @FXML
    private lateinit var attractionSlider: Slider

    @FXML
    private lateinit var repelFactorSlider: Slider

    @FXML
    private lateinit var accelerationSlider: Slider

    @FXML
    private lateinit var repelDistanceSlider: Slider

    @FXML
    private lateinit var maxVelocitySlider: Slider

    @FXML
    private lateinit var maxBoidSizeSlider: Slider

    @FXML
    private lateinit var boidTailLengthSlider: Slider

    @FXML
    private lateinit var boidVelocityVectorLengthSlider: Slider

    @FXML
    private lateinit var boidAccelerationVectorLengthSlider: Slider
    
    private var backgroundColor: Color = INIT_BG_COLOR
    private val subScene2D: SubScene
    private val flock: Flock
    private val animationService: AnimationService
    private val engine: Engine
    private val statisticsController = StatisticsController()

    override fun setup() {
        bindFullScreenButtonToFullScreen(fullScreenButton, sceneManager.stage)
        configureAnimationPane(animationPane)
        configureSubScene(subScene2D, animationPane)
        configureComboBoxes()
        configureControls()
        configureColorPickers()
        addListenersToSliders()
        setupAppearancePane()
        bindFlockPropertiesToControlsProperties(flock)
        configureFlock(flock)
        engine.pullFactorProperty().bind(attractionSlider.valueProperty())
        engine.repelFactorProperty().bind(repelFactorSlider.valueProperty())
        animationService.addAnimationLoopToTimeline { animationLoop() }
        uniformFlockColorPicker.isDisable = flock.flockType.random
    }

    private fun setupAppearancePane() {
        try {
            val appearanceController = AppearanceController(this)
            appearanceTab.content = appearanceController.root
        } catch (e: IOException) {
            LOGGER.error("Appearance pane was not correctly loaded...", e)
        }
    }

    private fun animationLoop() {
        val maxSpeed = maxVelocitySlider.value
        val friction = frictionSlider.value
        val accelerationMultiplier = accelerationSlider.value
        val bounce = bounceWallsButton.isSelected
        val runTimeSim = Duration.millis((LocalTime.now().toNanoOfDay() - startTimeSim.toNanoOfDay()) / 1e6)
        statisticsController.showStatists(flock.selectedBoid, friction, flock.children.size, runTimeSim)
        animationService.run(flock, animationWindowDimension, accelerationMultiplier, friction, bounce, maxSpeed)
    }

    private fun configureSubScene(subScene: SubScene, animationPane: Pane?) {
        subScene.widthProperty().bind(animationPane?.widthProperty())
        subScene.heightProperty().bind(animationPane?.heightProperty())
        subScene.camera = configuredCamera
        animationPane?.onMouseDragged = EventHandler {
            flock.addBoidToFlockAtMouseTip(it, animationWindowDimension, numberOfBoidsSlider)
        }
    }

   private fun configureAnimationPane(animationPane: AnchorPane?) {
        animationPane?.setPrefSize(640.0, 400.0)
        animationPane?.background = Background(
            BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)
        )
    }

    private fun configureComboBoxes() {
        flockSettingsComboBox.items.addAll(
            flock.randomCircleFlock, flock.uniformCircleFlock, flock.randomRectangleFlock, flock.CircleFlock()
        )
        flockSettingsComboBox.value = flock.randomCircleFlock
        physicsEngineComboBox.items.addAll(engine.type1, engine.type2, engine.type3)
        physicsEngineComboBox.value = engine.type1
    }

    private fun configureColorPickers() {
        backgroundColorPicker.value = INIT_BG_COLOR
        uniformFlockColorPicker.value = INIT_UNIFORM_BALL_COLOR
        selectedBallColorPicker.value = INIT_SELECTED_BALL_COLOR
    }

    private val animationWindowDimension: Dimension2D
        get() = if (subScene2D.width < 1 && subScene2D.height < 1) {
            Dimension2D(animationPane.prefWidth, animationPane.prefHeight)
        } else {
            Dimension2D(subScene2D.width, subScene2D.height)
        }

    private fun configureFlock(flock: Flock) {
        flock.flockType = flockSettingsComboBox.value
        flock.controlFlockSize(numberOfBoidsSlider.valueProperty().intValue(), animationWindowDimension)
        flock.flockingSim = physicsEngineComboBox.value
        flock.selectedBoid = flock.randomSelectedBoid
    }

    private fun addListenersToSliders() {
        numberOfBoidsSlider.valueProperty().addListener { _, _, n -> numberOfBoidsSliderChanged(n) }
        perceptionRadiusSlider.valueProperty().addListener { _, _, n -> perceptionRadiusSliderChanged(n) }
        repelDistanceSlider.valueProperty().addListener { _, _, n -> repelDistanceSliderChanged(n) }
    }

    private fun numberOfBoidsSliderChanged(n: Number?) =
        flock.controlFlockSize(n?.toInt() ?: 0, animationWindowDimension)

    private fun perceptionRadiusSliderChanged(n: Number?) =
        flock.forEach { it.setPerceptionRadius(it.distanceFromCenterToOuterEdge * (n?.toDouble() ?: 0.0)) }

    private fun repelDistanceSliderChanged(n: Number?) =
        flock.forEach { it.setRepelRadius(it.distanceFromCenterToOuterEdge * (n?.toDouble() ?: 0.0)) }

    private fun bindFlockPropertiesToControlsProperties(flock: Flock) {
        val flockProperties = flock.flockProperties
        bindFlockPropertiesToButtonProperties(flockProperties)
        bindFlockPropertiesToSliderProperties(flockProperties)
    }

    private fun bindFlockPropertiesToButtonProperties(flockProperties: FlockProperties) {
        flockProperties.velocityVectorVisibleProperty().bind(showVelocityVectorButton.selectedProperty())
        flockProperties.accelerationVectorVisibleProperty().bind(showAccelerationVectorButton.selectedProperty())
        flockProperties.perceptionCircleVisibleProperty().bind(showPerceptionButton.selectedProperty())
        flockProperties.repelCircleVisibleProperty().bind(showRepelCircleButton.selectedProperty())
        flockProperties.allPathsVisibleProperty().bind(showAllPathsButton.selectedProperty())
        flockProperties.showConnectionsProperty().bind(showConnectionsButton.selectedProperty())
        flockProperties.selectedPathVisibleProperty().bind(showPathSelectedButton.selectedProperty())
        flockProperties.selectedPerceptionCircleVisibleProperty().bind(showPerceptionSelectedBallButton.selectedProperty())
    }

    private fun bindFlockPropertiesToSliderProperties(flockProperties: FlockProperties) {
        flockProperties.maxVelocityProperty().bind(maxVelocitySlider.valueProperty())
        flockProperties.maxAccelerationProperty().bind(accelerationSlider.valueProperty())
        flockProperties.maxBoidSizeProperty().bind(maxBoidSizeSlider.valueProperty())
        flockProperties.perceptionRadiusRatioProperty().bind(perceptionRadiusSlider.valueProperty())
        flockProperties.repelRadiusRatioProperty().bind(repelDistanceSlider.valueProperty())
        flockProperties.velocityVectorLengthProperty().bind(boidVelocityVectorLengthSlider.valueProperty())
        flockProperties.accelerationVectorLengthProperty().bind(boidAccelerationVectorLengthSlider.valueProperty())
        flockProperties.tailLengthProperty().bind(boidTailLengthSlider.valueProperty())
    }

    private fun configureControls() {
        configureSliders()
        setToggleButtons()
    }

    private fun configureSliders() {
        maxBoidSizeSlider.value = INIT_MAX_BALL_SIZE.toDouble()
        boidTailLengthSlider.value = MAX_PATH_SIZE_ALL.toDouble()
        boidVelocityVectorLengthSlider.value = MAX_VECTOR_LENGTH.toDouble()
        boidAccelerationVectorLengthSlider.value = MAX_VECTOR_LENGTH.toDouble()
        numberOfBoidsSlider.value = INIT_NUMBER_OF_BOIDS.toDouble()
        numberOfBoidsSlider.max = flock.maxNrOfBoids.toDouble()
        accelerationSlider.value = INIT_ACCELERATION_USER_SELECTED_BALL.toDouble()
        attractionSlider.value = INIT_ATTRACTION.toDouble()
        repelDistanceSlider.value = INIT_REPEL_DISTANCE_FACTOR.toDouble()
        repelFactorSlider.value = INIT_REPEL_FACTOR.toDouble()
        frictionSlider.value = INIT_FRICTION
        perceptionRadiusSlider.value = INIT_PERCEPTION_RADIUS.toDouble()
        maxVelocitySlider.value = INIT_MAX_SPEED.toDouble()
    }

    private fun setToggleButtons() {
        showConnectionsButton.isSelected = false
        showPathSelectedButton.isSelected = false
        showAllPathsButton.isSelected = false
        showVelocityVectorButton.isSelected = false
        showAccelerationVectorButton.isSelected = false
        showPerceptionButton.isSelected = false
        showRepelCircleButton.isSelected = false
        showPerceptionSelectedBallButton.isSelected = false
        bounceWallsButton.isSelected = true
    }

    @FXML
    private fun backgroundColorPickerAction(event: ActionEvent) {
        backgroundColor = (event.source as ColorPicker).value
        animationPane.background = Background(BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY))
    }

    @FXML
    private fun uniformBoidColorPickerAction(event: ActionEvent) {
        val color = (event.source as ColorPicker).value
        flock.setUniformBallColor(color)
        flock.children.stream()
            .map { Boid::class.java.cast(it) }
            .filter { it != flock.selectedBoid }
            .forEach { it.updatePaint(color) }
    }

    @FXML
    private fun selectedBoidColorPickerAction(event: ActionEvent) {
        val color = (event.source as ColorPicker).value
        flock.selectedBallColor = color
        flock.selectedBoid?.updatePaint(color)
    }

    @FXML
    private fun pauseSimButtonAction(actionEvent: ActionEvent) {
        if ((actionEvent.source as ToggleButton).isSelected) {
            animationService.pauseTimeline()
        } else {
            animationService.startTimeline()
        }
    }

    @FXML
    private fun resetButtonAction() {
        configureControls()
        flock.controlFlockSize(numberOfBoidsSlider.valueProperty().intValue(), animationWindowDimension)
        flock.forEach { it.setVisibilityBoidComponents(flock.flockProperties) }
        val selectedBoid = flock.selectedBoid
        if (selectedBoid != null) {
            flock.updateSelectedBoidComponentsVisibility(selectedBoid)
        }
    }

    @FXML
    private fun transparentButtonAction(actionEvent: ActionEvent) {
        val transparent = (actionEvent.source as ToggleButton).isSelected
        sceneManager.stage.opacity = if (transparent) STAGE_OPACITY else 1.0
    }

    @FXML
    private fun showPathSelectedBallButtonAction(event: ActionEvent) {
        val showPath = (event.source as ToggleButton).isSelected
        flock.selectedBoid?.path?.isVisible = showPath
    }

    @FXML
    private fun showPathsAllBoidsButtonAction(event: ActionEvent) {
        val showPaths = (event.source as ToggleButton).isSelected
        flock.forEach { it.path.isVisible = showPaths }
        showPathSelectedButton.isSelected = showPaths
    }

    @FXML
    private fun fullScreenButtonAction(actionEvent: ActionEvent) {
        sceneManager.stage.isFullScreen = (actionEvent.source as ToggleButton).isSelected
    }

    @FXML
    private fun showPerceptionRadiusButtonAction(event: ActionEvent) {
        val visible = (event.source as ToggleButton).isSelected
        flock.forEach { it.perceptionCircle.isVisible = visible }
        showPerceptionSelectedBallButton.isSelected = visible
    }

    @FXML
    private fun showPerceptionSelectedBallButtonAction(event: ActionEvent) {
        val visible = (event.source as ToggleButton).isSelected
        flock.selectedBoid?.perceptionCircle?.isVisible = visible
    }

    @FXML
    fun showRepelCircleButtonAction(event: ActionEvent) {
        val visible = (event.source as ToggleButton).isSelected
        flock.forEach { it.repelCircle.isVisible = visible }
    }

    @FXML
    fun showVelocitiesButtonAction(event: ActionEvent) {
        val visible = (event.source as ToggleButton).isSelected
        flock.children.stream()
            .map { Boid::class.java.cast(it) }
            .forEach { it.visibleVelocityVector.isVisible = visible }
    }

    @FXML
    fun showAccelerationsButtonAction(event: ActionEvent) {
        val visible = (event.source as ToggleButton).isSelected
        flock.forEach { it.visibleAccelerationVector.isVisible = visible }
    }

    @FXML
    private fun physicsEngineComboBoxAction(event: ActionEvent) {
        flock.flockingSim = (event.source as ComboBox<*>).value as FlockingSim
    }

    @FXML
    private fun flockTypeDropdownAction() {
        flock.controlFlockSize(0, animationWindowDimension)
        configureFlock(flock)
        uniformFlockColorPicker.isDisable = flock.flockType.random
    }

    override fun getController(): SceneController = this

    companion object {
        private val LOGGER = LogManager.getLogger(MainSceneController::class.java)
        private const val INIT_ACCELERATION_USER_SELECTED_BALL = 50
        private const val INIT_ATTRACTION = 3
        private const val INIT_REPEL_FACTOR = 10
        private const val INIT_REPEL_DISTANCE_FACTOR = 3
        private const val INIT_MAX_BALL_SIZE = 5
        private const val INIT_PERCEPTION_RADIUS = 25
        private const val INIT_MAX_SPEED = 150
        private const val INIT_FRICTION = 1.0
        private const val MAX_PATH_SIZE_ALL = 200
        private const val MAX_VECTOR_LENGTH = 80
        private val INIT_NUMBER_OF_BOIDS = parsedIntAppProp("init_number_of_boids", 120)
        private val configuredCamera: Camera
            get() {
                val camera: Camera = ParallelCamera()
                camera.farClip = 1000.0
                camera.nearClip = .01
                return camera
            }

        private fun bindFullScreenButtonToFullScreen(fullScreenButton: ToggleButton?, stage: Stage) {
            stage.fullScreenProperty().addListener { _, _, isFullScreen -> fullScreenButton?.isSelected = isFullScreen!! }
            stage.addEventFilter(KeyEvent.KEY_TYPED) { switchFullScreenIfF11Typed(stage, it) }
        }

        private fun switchFullScreenIfF11Typed(stage: Stage, key: KeyEvent) {
            if (key.code == KeyCode.F11) {
                stage.isFullScreen = !stage.isFullScreen
            }
        }
    }

    init {
        val subSceneRoot = Group()
        subScene2D = SubScene(subSceneRoot, 0.0, 0.0, true, SceneAntialiasing.BALANCED)
        flock = Flock(scene)
        engine = Engine()
        animationService = AnimationService()
        subSceneRoot.children.addAll(flock)
        statisticsTab.content = statisticsController.root
        animationPane.children.add(subScene2D)
    }
}
