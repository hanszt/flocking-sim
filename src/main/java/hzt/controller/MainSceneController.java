package hzt.controller;

import hzt.model.AppConstants;
import hzt.service.AnimationService;
import hzt.model.utils.Engine;
import hzt.model.entity.Boid;
import hzt.model.entity.Flock;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;

import static hzt.model.AppConstants.STAGE_OPACITY;
import static hzt.model.AppConstants.Scene.MAIN_SCENE;
import static hzt.model.entity.Flock.INIT_SELECTED_BALL_COLOR;
import static hzt.model.entity.Flock.INIT_UNIFORM_BALL_COLOR;

@Getter
public class MainSceneController extends AbstractSceneController {

    @FXML
    private VBox root;
    @FXML
    private MenuBar menuBar;
    @FXML
    private AnchorPane animationPane;
    @FXML
    private VBox mainControlPanel;
    @FXML
    private HBox slidersPane;

    @FXML
    private ComboBox<Engine.FlockingSim> physicsEngineComboBox;
    @FXML
    private ComboBox<Flock.FlockType> flockSettingsComboBox;

    @FXML
    private ToggleButton showVelocityVectorButton;
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
    private ToggleButton showAccelerationVectorButton;
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
    private Slider numberOfBallsSlider;
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
    private Slider maxSpeedSlider;
    @FXML
    private Slider maxBallSizeSlider;

    @FXML
    private Label ballNameLabel;
    @FXML
    private Label positionStatsLabel;
    @FXML
    private Label velocityStatsLabel;
    @FXML
    private Label accelerationStatsLabel;
    @FXML
    private Label frictionStatsLabel;
    @FXML
    private Label frameRateStatsLabel;
    @FXML
    private Label nrOfBallsInPerceptionRadiusLabel;
    @FXML
    private Label ballSizeLabel;
    @FXML
    private Label numberOfBallsLabel;
    @FXML
    private Label runTimeLabel;

    private Color backgroundColor = AppConstants.INIT_BG_COLOR;

    private final SubScene subScene2D;
    private final Group subSceneRoot;
    private final Flock flock;
    private final AnimationService animationService;
    private final Engine engine;

    public MainSceneController(SceneManager sceneManager) throws IOException {
        super(MAIN_SCENE.getFxmlFileName(), sceneManager);
        this.subSceneRoot = new Group();
        this.subScene2D = new SubScene(subSceneRoot, 0, 0, true, SceneAntialiasing.BALANCED);
        this.flock = new Flock(this);
        this.engine = new Engine();
        this.animationService = new AnimationService(this);
        this.subSceneRoot.getChildren().addAll(flock);
        this.animationPane.getChildren().add(subScene2D);
    }

    @Override
    public void setup() {
        bindFullScreenButtonToFullScreen(fullScreenButton, sceneManager.getStage());
        configureAnimationPane(animationPane);
        configureSubScene(subScene2D, animationPane);
        configureComboBoxes();
        reset();
        configureFlock(flock);
        configureColorPickers();
        addListenersToSliders();
        engine.setPullFactor(attractionSlider.getValue());
        engine.setRepelFactor(repelFactorSlider.getValue());
       animationService.addAnimationLoopToTimeline(initializeAnimationLoop(), true);
        uniformBallColorPicker.setDisable(flock.getFlockType().equals(flock.getRandom()));
    }

    private void configureSubScene(SubScene subScene, Pane animationPane) {
        subScene.widthProperty().bind(animationPane.widthProperty());
        subScene.heightProperty().bind(animationPane.heightProperty());
        subScene.setCamera(getConfiguredCamera());
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
        stage.fullScreenProperty().addListener((observableValue, curVal, newVal) -> fullScreenButton.setSelected(newVal));
        stage.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (key.getCode() == KeyCode.F11) stage.setFullScreen(!stage.isFullScreen());
        });
    }

    private void configureComboBoxes() {
        flockSettingsComboBox.getItems().addAll(flock.getRandom(), flock.getUniform());
        flockSettingsComboBox.setValue(flock.getRandom());

        physicsEngineComboBox.getItems().addAll(engine.getType1(), engine.getType2(), engine.getType3());
        physicsEngineComboBox.setValue(engine.getType1());
    }

    private EventHandler<ActionEvent> initializeAnimationLoop() {
        return loop -> {
            double maxSpeed = maxSpeedSlider.getValue();
            double friction = frictionSlider.getValue();
            double accelerationMultiplier = accelerationSlider.getValue();
            boolean bounce = bounceWallsButton.isSelected();
            animationService.run(flock, accelerationMultiplier, friction, bounce, maxSpeed);
        };
    }

    private void configureColorPickers() {
        backgroundColorPicker.setValue(AppConstants.INIT_BG_COLOR);
        uniformBallColorPicker.setValue(INIT_UNIFORM_BALL_COLOR);
        selectedBallColorPicker.setValue(INIT_SELECTED_BALL_COLOR);
    }

    public Dimension2D getAnimationWindowDimension() {
        Dimension2D sceneDimension;
        if (scene.getWidth() == 0 && scene.getHeight() == 0) {
            sceneDimension = new Dimension2D(root.getPrefWidth(), root.getPrefHeight());
        } else sceneDimension = new Dimension2D(scene.getWidth(), scene.getHeight());
        return new Dimension2D(sceneDimension.getWidth() - mainControlPanel.getWidth(),
                sceneDimension.getHeight() - slidersPane.getHeight() - menuBar.getHeight());
    }

    private void configureFlock(Flock flock) {
        flock.setFlockType(flockSettingsComboBox.getValue());
        flock.controlFlockSize((int) numberOfBallsSlider.getValue(), getAnimationWindowDimension());
        flock.setFlockingSim(engine.getType1());
        flock.setSelectedBall(flock.getRandomSelectedBall());
    }

    private void addListenersToSliders() {
        numberOfBallsSlider.valueProperty().addListener((oldVal, curVal, newVal) ->
                flock.controlFlockSize(newVal.intValue(), getAnimationWindowDimension()));
        perceptionRadiusSlider.valueProperty().addListener((oldVal, curVal, newVal) ->
                flock.forEach(ball -> ball.setPerceptionRadius(ball.getBody().getRadius() * newVal.doubleValue())));
        repelDistanceSlider.valueProperty().addListener((oldVal, curVal, newVal) ->
                flock.forEach(ball -> ball.setRepelRadius(ball.getBody().getRadius() * newVal.doubleValue())));
        attractionSlider.valueProperty().addListener((oldVal, curVal, newVal) -> engine.setPullFactor(newVal.doubleValue()));
        repelFactorSlider.valueProperty().addListener((oldVal, curVal, newVal) -> engine.setRepelFactor(newVal.doubleValue()));
    }

    public void reset() {
        resetControls();
        flock.forEach(this::setBallParams);
    }

    public void setBallParams(Boid boid) {
        boid.getVisibleVelocityVector().setVisible(showVelocityVectorButton.isSelected());
        boid.getVisibleAccelerationVector().setVisible(showAccelerationVectorButton.isSelected());
        boid.getRepelCircle().setVisible(showRepelCircleButton.isSelected());
        boid.getPerceptionCircle().setVisible(showPerceptionButton.isSelected());
        boolean showPathSelectedBall = flock.getSelectedBall() != null && flock.getSelectedBall().equals(boid);
        if (showPathSelectedBall) boid.getPath().setVisible(showPathSelectedButton.isSelected());
        else boid.getPath().setVisible(showAllPathsButton.isSelected());
    }

    private void resetControls() {
        maxBallSizeSlider.setValue(AppConstants.INIT_MAX_BALL_SIZE);
        numberOfBallsSlider.setValue(AppConstants.INIT_NUMBER_OF_BALLS);
        accelerationSlider.setValue(AppConstants.INIT_ACCELERATION_USER_SELECTED_BALL);
        attractionSlider.setValue(AppConstants.INIT_ATTRACTION);
        repelDistanceSlider.setValue(AppConstants.INIT_REPEL_DISTANCE_FACTOR);
        repelFactorSlider.setValue(AppConstants.INIT_REPEL_FACTOR);
        frictionSlider.setValue(AppConstants.INIT_FRICTION);
        perceptionRadiusSlider.setValue(AppConstants.INIT_PERCEPTION_RADIUS);
        maxSpeedSlider.setValue(AppConstants.INIT_MAX_SPEED);
        showConnectionsButton.setSelected(AppConstants.INIT_SHOW_CONNECTIONS);
        showPathSelectedButton.setSelected(AppConstants.INIT_SHOW_PATH);
        showAllPathsButton.setSelected(AppConstants.INIT_SHOW_PATH);
        showVelocityVectorButton.setSelected(AppConstants.INIT_SHOW_VELOCITY);
        showAccelerationVectorButton.setSelected(AppConstants.INIT_SHOW_ACCELERATION);
        showPerceptionButton.setSelected(AppConstants.INIT_SHOW_PERCEPTION);
        showPerceptionSelectedBallButton.setSelected(AppConstants.INIT_SHOW_PERCEPTION);
        bounceWallsButton.setSelected(AppConstants.INIT_BOUNCE_WALLS_BUTTON_VALUE);
    }

    @FXML
    private void backgroundColorPickerAction(ActionEvent event) {
        backgroundColor = ((ColorPicker) event.getSource()).getValue();
        animationPane.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void uniformBallColorPickerAction(ActionEvent event) {
        Color color = ((ColorPicker) event.getSource()).getValue();
        flock.setUniformBallColor(color);
        flock.getChildren().stream().map(n -> (Boid) n)
                .filter(ball -> !ball.equals(flock.getSelectedBall())).forEach(ball2D -> ball2D.updatePaint(color));
    }

    @FXML
    private void selectedBallColorPickerAction(ActionEvent event) {
        Color color = ((ColorPicker) event.getSource()).getValue();
        flock.setSelectedBallColor(color);
        flock.getSelectedBall().updatePaint(color);
    }

    @FXML
    private void pauseSimButtonAction(ActionEvent actionEvent) {
        if (((ToggleButton) actionEvent.getSource()).isSelected()) animationService.pauseTimeline();
        else animationService.startTimeline();
    }

    @FXML
    private void transparentButtonAction(ActionEvent actionEvent) {
        boolean transparent = ((ToggleButton) actionEvent.getSource()).isSelected();
        getSceneManager().getStage().setOpacity(transparent ? STAGE_OPACITY : 1);
    }

    @FXML
    private void showPathSelectedBallButtonAction(ActionEvent event) {
        boolean showPath = ((ToggleButton) event.getSource()).isSelected();
        Boid ball = flock.getSelectedBall();
        if (ball != null) ball.getPath().setVisible(showPath);
    }

    @FXML
    private void showPathsAllBallsButtonAction(ActionEvent event) {
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
        flock.getSelectedBall().getPerceptionCircle().setVisible(visible);
    }

    @FXML
    public void showRepelCircleButtonAction(ActionEvent event) {
        boolean visible = ((ToggleButton) event.getSource()).isSelected();
        flock.forEach(ball2D -> ball2D.getRepelCircle().setVisible(visible));
    }

    @FXML
    public void showVelocitiesButtonAction(ActionEvent event) {
        boolean visible = ((ToggleButton) event.getSource()).isSelected();
        flock.getChildren().stream().map(n -> (Boid) n).forEach(ball2D -> ball2D.getVisibleVelocityVector().setVisible(visible));
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
        flock.setSelectedBall(flock.getRandomSelectedBall());
    }

    protected AbstractSceneController getBean() {
        return this;
    }


}
