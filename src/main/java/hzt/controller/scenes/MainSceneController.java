package hzt.controller.scenes;

import hzt.controller.SceneManager;
import hzt.controller.sub_pane.AppearanceController;
import hzt.controller.sub_pane.StatisticsController;
import hzt.model.FlockProperties;
import hzt.model.entity.Boid;
import hzt.model.entity.Flock;
import hzt.model.utils.Engine;
import hzt.service.AnimationService;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalTime;

import static hzt.model.AppConstants.*;
import static hzt.model.AppConstants.Scene.MAIN_SCENE;
import static java.util.function.Predicate.not;

public class MainSceneController extends SceneController {

    @FXML
    private Tab appearanceTab;
    @FXML
    private Tab statisticsTab;
    @FXML
    private AnchorPane animationPane;
    @FXML
    private ComboBox<Engine.FlockingSim> physicsEngineComboBox;
    @FXML
    private ComboBox<Flock.FlockType> flockSettingsComboBox;

    @FXML
    private ToggleButton showVelocityVectorButton;
    @FXML
    private ToggleButton showAccelerationVectorButton;
    @FXML
    private ToggleButton bounceWallsButton;
    @FXML
    private ToggleButton showPathSelectedButton;
    @FXML
    private ToggleButton showPerceptionButton;
    @FXML
    private ToggleButton showPerceptionSelectedBallButton;
    @FXML
    private ToggleButton showRepelCircleButton;
    @FXML
    private ToggleButton showConnectionsButton;
    @FXML
    private ToggleButton fullScreenButton;
    @FXML
    private ToggleButton showAllPathsButton;

    @FXML
    private ColorPicker uniformBallColorPicker;
    @FXML
    private ColorPicker backgroundColorPicker;
    @FXML
    private ColorPicker selectedBallColorPicker;

    @FXML
    private Slider numberOfBoidsSlider;
    @FXML
    private Slider perceptionRadiusSlider;
    @FXML
    private Slider frictionSlider;
    @FXML
    private Slider attractionSlider;
    @FXML
    private Slider repelFactorSlider;
    @FXML
    private Slider accelerationSlider;
    @FXML
    private Slider repelDistanceSlider;
    @FXML
    private Slider maxVelocitySlider;
    @FXML
    private Slider maxBoidSizeSlider;

    @FXML
    private Slider boidTailLengthSlider;
    @FXML
    private Slider boidVelocityVectorLengthSlider;
    @FXML
    private Slider boidAccelerationVectorLengthSlider;

    private Color backgroundColor = INIT_BG_COLOR;

    private final SubScene subScene2D;
    private final Flock flock;
    private final AnimationService animationService;
    private final Engine engine;

    private final StatisticsController statisticsController = new StatisticsController();

    public MainSceneController(SceneManager sceneManager) throws IOException {
        super(MAIN_SCENE.getFxmlFileName(), sceneManager);
        Group subSceneRoot = new Group();
        this.subScene2D = new SubScene(subSceneRoot, 0, 0, true, SceneAntialiasing.BALANCED);
        this.flock = new Flock(scene);
        this.engine = new Engine();
        this.animationService = new AnimationService();
        subSceneRoot.getChildren().addAll(flock);
        statisticsTab.setContent(statisticsController.getRoot());
        this.animationPane.getChildren().add(subScene2D);
    }

    @Override
    public void setup() {
        bindFullScreenButtonToFullScreen(fullScreenButton, sceneManager.getStage());
        configureAnimationPane(animationPane);
        configureSubScene(subScene2D, animationPane);
        configureComboBoxes();
        configureControls();
        configureColorPickers();
        addListenersToSliders();
        setupAppearancePane();
        bindFlockPropertiesToControlsProperties(flock);
        configureFlock(flock);
        engine.pullFactorProperty().bind(attractionSlider.valueProperty());
        engine.repelFactorProperty().bind(repelFactorSlider.valueProperty());
        animationService.addAnimationLoopToTimeline(this::animationLoop, true);
        uniformBallColorPicker.setDisable(flock.getFlockType().equals(flock.getRandom()));
    }

    private void setupAppearancePane() {
        try {
            AppearanceController appearanceController = new AppearanceController(this);
            this.appearanceTab.setContent(appearanceController.getRoot());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void animationLoop(ActionEvent loop) {
        double maxSpeed = maxVelocitySlider.getValue();
        double friction = frictionSlider.getValue();
        double accelerationMultiplier = accelerationSlider.getValue();
        boolean bounce = bounceWallsButton.isSelected();
        Duration runTimeSim = Duration.millis((LocalTime.now().toNanoOfDay() - startTimeSim.toNanoOfDay()) / 1e6);
        statisticsController.showStatists(flock.getSelectedBoid(), friction, flock.getChildren().size(), runTimeSim);
        animationService.run(flock, getAnimationWindowDimension(), accelerationMultiplier, friction, bounce, maxSpeed);
    }

    private void configureSubScene(SubScene subScene, Pane animationPane) {
        subScene.widthProperty().bind(animationPane.widthProperty());
        subScene.heightProperty().bind(animationPane.heightProperty());
        subScene.setCamera(getConfiguredCamera());
        animationPane.setOnMouseDragged(e -> flock.addBoidToFlockAtMouseTip(e, getAnimationWindowDimension(), numberOfBoidsSlider));
    }

    private Camera getConfiguredCamera() {
        Camera camera = new ParallelCamera();
        camera.setFarClip(1000);
        camera.setNearClip(.01);
        return camera;
    }

    private void configureAnimationPane(AnchorPane animationPane) {
        animationPane.setPrefSize(640, 400);
        animationPane.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void bindFullScreenButtonToFullScreen(ToggleButton fullScreenButton, Stage stage) {
        stage.fullScreenProperty().addListener((observableValue, curVal, isFullScreen) -> fullScreenButton.setSelected(isFullScreen));
        stage.addEventFilter(KeyEvent.KEY_TYPED, key -> switchFullScreenIfF11Typed(stage, key));
    }

    private void switchFullScreenIfF11Typed(Stage stage, KeyEvent key) {
        if (key.getCode() == KeyCode.F11) {
            stage.setFullScreen(!stage.isFullScreen());
        }
    }

    private void configureComboBoxes() {
        flockSettingsComboBox.getItems().addAll(flock.getRandom(), flock.getUniform(), flock.getUniformOrdered());
        flockSettingsComboBox.setValue(flock.getRandom());

        physicsEngineComboBox.getItems().addAll(engine.getType1(), engine.getType2(), engine.getType3());
        physicsEngineComboBox.setValue(engine.getType1());
    }

    private void configureColorPickers() {
        backgroundColorPicker.setValue(INIT_BG_COLOR);
        uniformBallColorPicker.setValue(INIT_UNIFORM_BALL_COLOR);
        selectedBallColorPicker.setValue(INIT_SELECTED_BALL_COLOR);
    }

    public Dimension2D getAnimationWindowDimension() {
        Dimension2D animationPaneDimension;
        if (subScene2D.getWidth() == 0 && subScene2D.getHeight() == 0) {
            animationPaneDimension = new Dimension2D(animationPane.getPrefWidth(), animationPane.getPrefHeight());
        } else animationPaneDimension = new Dimension2D(subScene2D.getWidth(), subScene2D.getHeight());
        return animationPaneDimension;
    }

    private void configureFlock(Flock flock) {
        flock.setFlockType(flockSettingsComboBox.getValue());
        flock.controlFlockSize(numberOfBoidsSlider.valueProperty().intValue(), getAnimationWindowDimension());
        flock.setFlockingSim(physicsEngineComboBox.getValue());
        flock.setSelectedBoid(flock.getRandomSelectedBoid());
    }

    private void addListenersToSliders() {
        numberOfBoidsSlider.valueProperty().addListener(this::numberOfBoidsSliderChanged);
        perceptionRadiusSlider.valueProperty().addListener(this::perceptionRadiusSliderChanged);
        repelDistanceSlider.valueProperty().addListener(this::repelDistanceSliderChanged);
    }

    private void numberOfBoidsSliderChanged(ObservableValue<? extends Number> o, Number c, Number n) {
        flock.controlFlockSize(n.intValue(), getAnimationWindowDimension());
    }

    private void perceptionRadiusSliderChanged(ObservableValue<? extends Number> o, Number c, Number n) {
        flock.forEach(ball -> ball.setPerceptionRadius(ball.getBody().getRadius() * n.doubleValue()));
    }

    private void repelDistanceSliderChanged(ObservableValue<? extends Number> o, Number c, Number n) {
        flock.forEach(ball -> ball.setRepelRadius(ball.getBody().getRadius() * n.doubleValue()));
    }

    private void bindFlockPropertiesToControlsProperties(Flock flock) {
        FlockProperties flockProperties = flock.getFlockProperties();
        bindFlockPropertiesToButtonProperties(flockProperties);
        bindFlockPropertiesToSliderProperties(flockProperties);
    }

    private void bindFlockPropertiesToButtonProperties(FlockProperties flockProperties) {
        flockProperties.velocityVectorVisibleProperty().bind(showVelocityVectorButton.selectedProperty());
        flockProperties.accelerationVectorVisibleProperty().bind(showAccelerationVectorButton.selectedProperty());
        flockProperties.perceptionCircleVisibleProperty().bind(showPerceptionButton.selectedProperty());
        flockProperties.repelCircleVisibleProperty().bind(showRepelCircleButton.selectedProperty());
        flockProperties.allPathsVisibleProperty().bind(showAllPathsButton.selectedProperty());
        flockProperties.showConnectionsProperty().bind(showConnectionsButton.selectedProperty());
        flockProperties.selectedPathVisibleProperty().bind(showPathSelectedButton.selectedProperty());
        flockProperties.selectedPerceptionCircleVisibleProperty().bind(showPerceptionSelectedBallButton.selectedProperty());
    }

    private void bindFlockPropertiesToSliderProperties(FlockProperties flockProperties) {
        flockProperties.maxVelocityProperty().bind(maxVelocitySlider.valueProperty());
        flockProperties.maxAccelerationProperty().bind(accelerationSlider.valueProperty());
        flockProperties.maxBoidSizeProperty().bind(maxBoidSizeSlider.valueProperty());
        flockProperties.perceptionRadiusRatioProperty().bind(perceptionRadiusSlider.valueProperty());
        flockProperties.repelRadiusRatioProperty().bind(repelDistanceSlider.valueProperty());
        flockProperties.velocityVectorLengthProperty().bind(boidVelocityVectorLengthSlider.valueProperty());
        flockProperties.accelerationVectorLengthProperty().bind(boidAccelerationVectorLengthSlider.valueProperty());
        flockProperties.tailLengthProperty().bind(boidTailLengthSlider.valueProperty());
    }

    private void configureControls() {
        configureSliders();
        setToggleButtons();
    }

    private static final int INIT_ACCELERATION_USER_SELECTED_BALL = 50;
    private static final int INIT_ATTRACTION = 3;
    private static final int INIT_REPEL_FACTOR = 10;
    private static final int INIT_REPEL_DISTANCE_FACTOR = 3;
    private static final int INIT_MAX_BALL_SIZE = 5;
    private static final int INIT_PERCEPTION_RADIUS = 25;
    private static final int INIT_MAX_SPEED = 150;
    private static final double INIT_FRICTION = 1;
    private static final int MAX_PATH_SIZE_ALL = 200;

    private static final int MAX_VECTOR_LENGTH = 80;
    private static final int INIT_NUMBER_OF_BOIDS = parsedIntAppProp("init_number_of_boids", 120);

    public static final int MAX_NUMBER_OF_BOIDS = parsedIntAppProp("max_number_of_boids", 200);

    private void configureSliders() {
        maxBoidSizeSlider.setValue(INIT_MAX_BALL_SIZE);
        boidTailLengthSlider.setValue(MAX_PATH_SIZE_ALL);
        boidVelocityVectorLengthSlider.setValue(MAX_VECTOR_LENGTH);
        boidAccelerationVectorLengthSlider.setValue(MAX_VECTOR_LENGTH);
        numberOfBoidsSlider.setValue(INIT_NUMBER_OF_BOIDS);
        numberOfBoidsSlider.setMax(MAX_NUMBER_OF_BOIDS);
        accelerationSlider.setValue(INIT_ACCELERATION_USER_SELECTED_BALL);
        attractionSlider.setValue(INIT_ATTRACTION);
        repelDistanceSlider.setValue(INIT_REPEL_DISTANCE_FACTOR);
        repelFactorSlider.setValue(INIT_REPEL_FACTOR);
        frictionSlider.setValue(INIT_FRICTION);
        perceptionRadiusSlider.setValue(INIT_PERCEPTION_RADIUS);
        maxVelocitySlider.setValue(INIT_MAX_SPEED);
    }

    private void setToggleButtons() {
        showConnectionsButton.setSelected(false);
        showPathSelectedButton.setSelected(false);
        showAllPathsButton.setSelected(false);
        showVelocityVectorButton.setSelected(false);
        showAccelerationVectorButton.setSelected(false);
        showPerceptionButton.setSelected(false);
        showRepelCircleButton.setSelected(false);
        showPerceptionSelectedBallButton.setSelected(false);
        bounceWallsButton.setSelected(true);
    }

    @FXML
    private void backgroundColorPickerAction(ActionEvent event) {
        backgroundColor = ((ColorPicker) event.getSource()).getValue();
        animationPane.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void uniformBoidColorPickerAction(ActionEvent event) {
        Color color = ((ColorPicker) event.getSource()).getValue();
        flock.setUniformBallColor(color);
        flock.getChildren().stream().map(Boid.class::cast)
                .filter(not(ball -> ball.equals(flock.getSelectedBoid())))
                .forEach(ball2D -> ball2D.updatePaint(color));
    }

    @FXML
    private void selectedBoidColorPickerAction(ActionEvent event) {
        Color color = ((ColorPicker) event.getSource()).getValue();
        flock.setSelectedBallColor(color);
        flock.getSelectedBoid().updatePaint(color);
    }

    @FXML
    private void pauseSimButtonAction(ActionEvent actionEvent) {
        if (((ToggleButton) actionEvent.getSource()).isSelected()) animationService.pauseTimeline();
        else animationService.startTimeline();
    }

    @FXML
    private void resetButtonAction() {
        configureControls();
        flock.controlFlockSize(numberOfBoidsSlider.valueProperty().intValue(), getAnimationWindowDimension());
        flock.forEach(boid -> boid.setVisibilityBoidComponents(flock.getFlockProperties()));
        flock.updateSelectedBoidComponentsVisibility(flock.getSelectedBoid());
    }

    @FXML
    private void transparentButtonAction(ActionEvent actionEvent) {
        boolean transparent = ((ToggleButton) actionEvent.getSource()).isSelected();
        sceneManager.getStage().setOpacity(transparent ? STAGE_OPACITY : 1);
    }

    @FXML
    private void showPathSelectedBallButtonAction(ActionEvent event) {
        boolean showPath = ((ToggleButton) event.getSource()).isSelected();
        Boid ball = flock.getSelectedBoid();
        if (ball != null) ball.getPath().setVisible(showPath);
    }

    @FXML
    private void showPathsAllBoidsButtonAction(ActionEvent event) {
        boolean showPaths = ((ToggleButton) event.getSource()).isSelected();
        flock.forEach(ball2D -> ball2D.getPath().setVisible(showPaths));
        showPathSelectedButton.setSelected(showPaths);
    }

    @FXML
    private void fullScreenButtonAction(ActionEvent actionEvent) {
        sceneManager.getStage().setFullScreen(((ToggleButton) actionEvent.getSource()).isSelected());
    }

    @FXML
    private void showPerceptionRadiusButtonAction(ActionEvent event) {
        boolean visible = ((ToggleButton) event.getSource()).isSelected();
        flock.forEach(ball2D -> ball2D.getPerceptionCircle().setVisible(visible));
        showPerceptionSelectedBallButton.setSelected(visible);
    }

    @FXML
    private void showPerceptionSelectedBallButtonAction(ActionEvent event) {
        boolean visible = ((ToggleButton) event.getSource()).isSelected();
        flock.getSelectedBoid().getPerceptionCircle().setVisible(visible);
    }

    @FXML
    public void showRepelCircleButtonAction(ActionEvent event) {
        boolean visible = ((ToggleButton) event.getSource()).isSelected();
        flock.forEach(ball2D -> ball2D.getRepelCircle().setVisible(visible));
    }

    @FXML
    public void showVelocitiesButtonAction(ActionEvent event) {
        boolean visible = ((ToggleButton) event.getSource()).isSelected();
        flock.getChildren().stream().map(Boid.class::cast)
                .forEach(ball2D -> ball2D.getVisibleVelocityVector().setVisible(visible));
    }

    @FXML
    public void showAccelerationsButtonAction(ActionEvent event) {
        boolean visible = ((ToggleButton) event.getSource()).isSelected();
        flock.forEach(ball2D -> ball2D.getVisibleAccelerationVector().setVisible(visible));
    }

    @FXML
    private void physicsEngineComboBoxAction(ActionEvent event) {
        flock.setFlockingSim((Engine.FlockingSim) ((ComboBox<?>) event.getSource()).getValue());
    }

    @FXML
    private void flockTypeDropdownAction() {
        flock.controlFlockSize(0, getAnimationWindowDimension());
        configureFlock(flock);
        uniformBallColorPicker.setDisable(flock.getFlockType().equals(flock.getRandom()));
    }

    @Override
    protected SceneController getBean() {
        return this;
    }

    public AnchorPane getAnimationPane() {
        return animationPane;
    }

    public ColorPicker getBackgroundColorPicker() {
        return backgroundColorPicker;
    }
}
