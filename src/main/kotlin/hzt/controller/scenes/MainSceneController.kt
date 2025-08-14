package hzt.controller.scenes

import hzt.controller.SceneManager
import hzt.controller.sub_pane.AppearanceController
import hzt.controller.sub_pane.AppearanceController.Companion.INIT_BG_COLOR
import hzt.controller.sub_pane.StatisticsController
import hzt.model.FlockProperties
import hzt.model.PropertyLoader
import hzt.model.PropertyLoader.parsedIntAppProp
import hzt.model.entity.Flock
import hzt.model.entity.Flock.FlockType
import hzt.model.entity.boid.Boid
import hzt.model.utils.Engine
import hzt.model.utils.Engine.FlockingSim
import hzt.service.AnimationService
import hzt.utils.inverseFullScreen
import hzt.utils.onChange
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Dimension2D
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.ParallelCamera
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import org.slf4j.LoggerFactory
import java.io.IOException
import java.time.Duration
import java.time.LocalTime

class MainSceneController(sceneManager: SceneManager) : SceneController(SceneType.MAIN_SCENE.fxmlFileName, sceneManager) {
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
    lateinit var fullScreenButton: ToggleButton
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

    init {
        val subSceneRoot = Group()
        subScene2D = SubScene(subSceneRoot, 0.0, 0.0, true, SceneAntialiasing.BALANCED)
        flock = Flock(scene, sceneManager.random)
        engine = Engine()
        animationService = AnimationService()
        subSceneRoot.children.addAll(flock)
        statisticsTab.content = statisticsController.root
        animationPane.children.add(subScene2D)
    }

    override fun setup() {
        animationPane.configure()
        subScene2D.configureBy(animationPane)
        configureComboBoxes()
        configureControls()
        configureColorPickers()
        addListenersToSliders()
        setupAppearancePane()
        bindFlockPropertiesToControlsProperties(flock)
        flock.configure()
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
        val runTimeSim = Duration.between(startTimeSim, sceneManager.clock.instant())
        statisticsController.showStatists(flock.selectedBoid, friction, flock.children.size, runTimeSim)
        animationService.run(flock, accelerationMultiplier, friction, maxSpeed) {
            if (bounce) it.bounceOfEdges(animationWindowDimension) else it.floatThroughEdges(animationWindowDimension)
        }
    }

    private fun SubScene.configureBy(animationPane: Pane) {
        widthProperty().bind(animationPane.widthProperty())
        heightProperty().bind(animationPane.heightProperty())
        camera = ParallelCamera().apply {
            farClip = 1000.0
            nearClip = .01
        }
        animationPane.onMouseDragged = EventHandler {
            flock.addBoidToFlockAtMouseTip(it, animationWindowDimension, numberOfBoidsSlider)
        }
    }

   private fun AnchorPane.configure() {
        setPrefSize(640.0, 400.0)
        background = Background(BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY))
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
        uniformFlockColorPicker.value = Flock.INIT_UNIFORM_BALL_COLOR
        selectedBallColorPicker.value = Flock.INIT_SELECTED_BALL_COLOR
    }

    private val animationWindowDimension: Dimension2D
        get() = if (subScene2D.width < 1 && subScene2D.height < 1) {
            Dimension2D(animationPane.prefWidth, animationPane.prefHeight)
        } else {
            Dimension2D(subScene2D.width, subScene2D.height)
        }

    private fun Flock.configure() {
        flockType = flockSettingsComboBox.value
        controlFlockSize(numberOfBoidsSlider.valueProperty().intValue(), animationWindowDimension)
        flockingSim = physicsEngineComboBox.value
        selectedBoid = randomSelectedBoid
    }

    private fun addListenersToSliders() {
        numberOfBoidsSlider.onChange(::numberOfBoidsSliderChanged)
        perceptionRadiusSlider.onChange(::perceptionRadiusSliderChanged)
        repelDistanceSlider.onChange(::repelDistanceSliderChanged)
    }

    private fun numberOfBoidsSliderChanged(new: Number?) =
        flock.controlFlockSize(new?.toInt() ?: 0, animationWindowDimension)

    private fun perceptionRadiusSliderChanged(new: Number?) =
        flock.forEach { it.setPerceptionRadius(it.distanceFromCenterToOuterEdge * (new?.toDouble() ?: 0.0)) }

    private fun repelDistanceSliderChanged(new: Number?) =
        flock.forEach { it.setRepelRadius(it.distanceFromCenterToOuterEdge * (new?.toDouble() ?: 0.0)) }

    private fun bindFlockPropertiesToControlsProperties(flock: Flock) {
        flock.flockProperties.bindToButtonProperties()
        flock.flockProperties.bindToSliderProperties()
    }

    private fun FlockProperties.bindToButtonProperties() {
        velocityVectorVisibleProperty().bind(showVelocityVectorButton.selectedProperty())
        accelerationVectorVisibleProperty().bind(showAccelerationVectorButton.selectedProperty())
        perceptionCircleVisibleProperty().bind(showPerceptionButton.selectedProperty())
        repelCircleVisibleProperty().bind(showRepelCircleButton.selectedProperty())
        allPathsVisibleProperty().bind(showAllPathsButton.selectedProperty())
        showConnectionsProperty().bind(showConnectionsButton.selectedProperty())
        selectedPathVisibleProperty().bind(showPathSelectedButton.selectedProperty())
        selectedPerceptionCircleVisibleProperty().bind(showPerceptionSelectedBallButton.selectedProperty())
    }

    private fun FlockProperties.bindToSliderProperties() {
        maxVelocityProperty().bind(maxVelocitySlider.valueProperty())
        maxAccelerationProperty().bind(accelerationSlider.valueProperty())
        maxBoidSizeProperty().bind(maxBoidSizeSlider.valueProperty())
        perceptionRadiusRatioProperty().bind(perceptionRadiusSlider.valueProperty())
        repelRadiusRatioProperty().bind(repelDistanceSlider.valueProperty())
        velocityVectorLengthProperty().bind(boidVelocityVectorLengthSlider.valueProperty())
        accelerationVectorLengthProperty().bind(boidAccelerationVectorLengthSlider.valueProperty())
        tailLengthProperty().bind(boidTailLengthSlider.valueProperty())
    }

    private fun configureControls() {
        configureSliders()
        setToggleButtons()
        fullScreenButton.onAction = EventHandler { sceneManager.stage.inverseFullScreen() }
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
    private fun pauseSimButtonAction(actionEvent: ActionEvent) =
        if ((actionEvent.source as ToggleButton).isSelected) {
            animationService.pauseTimeline()
        } else {
            animationService.startTimeline()
        }

    @FXML
    private fun resetButtonAction() {
        configureControls()
        flock.controlFlockSize(numberOfBoidsSlider.valueProperty().intValue(), animationWindowDimension)
        flock.forEach { it.setVisibilityBoidComponents(flock.flockProperties) }
        flock.selectedBoid?.let(flock::updateSelectedBoidComponentsVisibility)
    }

    @FXML
    private fun transparentButtonAction(actionEvent: ActionEvent) {
        sceneManager.stage.opacity = if ((actionEvent.source as ToggleButton).isSelected) STAGE_OPACITY else 1.0
    }

    @FXML
    private fun showPathSelectedBallButtonAction(event: ActionEvent) {
        flock.selectedBoid?.path?.isVisible = (event.source as ToggleButton).isSelected
    }

    @FXML
    private fun showPathsAllBoidsButtonAction(event: ActionEvent) {
        val showPaths = (event.source as ToggleButton).isSelected
        flock.forEach { it.path.isVisible = showPaths }
        showPathSelectedButton.isSelected = showPaths
    }

    @FXML
    private fun showPerceptionRadiusButtonAction(event: ActionEvent) {
        val visible = (event.source as ToggleButton).isSelected
        flock.forEach { it.perceptionCircle.isVisible = visible }
        showPerceptionSelectedBallButton.isSelected = visible
    }

    @FXML
    private fun showPerceptionSelectedBallButtonAction(event: ActionEvent) {
        flock.selectedBoid?.perceptionCircle?.isVisible = (event.source as ToggleButton).isSelected
    }

    @FXML
    fun showRepelCircleButtonAction(event: ActionEvent) {
        flock.forEach { it.repelCircle.isVisible = (event.source as ToggleButton).isSelected }
    }

    @FXML
    fun showVelocitiesButtonAction(event: ActionEvent) {
        flock.forEach { it.visibleVelocityVector.isVisible = (event.source as ToggleButton).isSelected }
    }

    @FXML
    fun showAccelerationsButtonAction(event: ActionEvent) {
        flock.forEach { it.visibleAccelerationVector.isVisible = (event.source as ToggleButton).isSelected }
    }

    @FXML
    private fun physicsEngineComboBoxAction(event: ActionEvent) {
        flock.flockingSim = (event.source as ComboBox<*>).value as FlockingSim
    }

    @FXML
    private fun flockTypeDropdownAction() {
        flock.controlFlockSize(0, animationWindowDimension)
        flock.configure()
        uniformFlockColorPicker.isDisable = flock.flockType.random
    }

    companion object {

        private val LOGGER = LoggerFactory.getLogger(MainSceneController::class.java)
        private val STAGE_OPACITY = PropertyLoader.parsedDoubleAppProp("stage_opacity", .8)
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
    }
}
