package hzt.controller.main_scene;

import hzt.controller.AbstractSceneController;
import hzt.controller.AnimationService;
import hzt.controller.AppManager;
import hzt.controller.utils.Engine;
import hzt.model.entity.Boid;
import hzt.model.entity.Flock;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;

import static hzt.controller.AppConstants.STAGE_OPACITY;
import static hzt.controller.AppConstants.Screen.MAIN_SCENE;
import static hzt.model.entity.Flock.INIT_SELECTED_BALL_COLOR;
import static hzt.model.entity.Flock.INIT_UNIFORM_BALL_COLOR;
import static javafx.scene.paint.Color.NAVY;

@Getter
public class MainSceneController extends AbstractSceneController {

    private static final Color INIT_BG_COLOR = NAVY;

    private final Flock flock;
    private final AnimationService animationService;
    private final Engine engine;

    public MainSceneController(AppManager appManager) {
        super(MAIN_SCENE.getFxmlFileName(), appManager);
        engine = new Engine();
        animationService = new AnimationService(this);
        flock = new Flock(this);
    }

    //constants declared in FXML file
    @FXML
    private Integer initNumberOfBalls;
    @FXML
    private Integer initAccelerationUserSelectedBall;
    @FXML
    private Integer initAttraction;
    @FXML
    private Integer initRepelFactor;
    @FXML
    private Integer initRepelDistanceFactor;
    @FXML
    private Integer initMaxBallSize;
    @FXML
    private Integer initPerceptionRadius;
    @FXML
    private Integer initMaxSpeed;
    @FXML
    private Double initFriction;
    @FXML
    private Boolean initBounceWallsButtonValue;
    @FXML
    private Boolean initShowConnections;
    @FXML
    private Boolean initShowPath;
    @FXML
    private Boolean initShowVelocity;
    @FXML
    private Boolean initShowAcceleration;
    @FXML
    private Boolean initShowPerception;

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

    private Color backgroundColor = INIT_BG_COLOR;

    @Override
    public void setup() {
        addListenersToSliders();
        configureColorPickers();
        configureComboBoxes();
        configureFlock();
        reset();
        flock.setFlockingSim(engine.getType1());
        bindFullScreenButtonToFullScreen();
        engine.setPullFactor(attractionSlider.getValue());
        engine.setRepelFactor(repelFactorSlider.getValue());
        animationPane.getChildren().add(flock);
        animationPane.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
        animationService.addAnimationLoopToTimeline(initializeAnimationLoop(), true);
        flock.setSelectedBall(flock.getRandomNewSelectedBall());
        uniformBallColorPicker.setDisable(flock.getFlockType().equals(flock.getRandom()));
    }

    private void bindFullScreenButtonToFullScreen() {
        Stage stage = appManager.getStage();
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
        backgroundColorPicker.setValue(backgroundColor);
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

    private void configureFlock() {
        flock.setFlockType(flockSettingsComboBox.getValue());
        flock.controlFlockSize((int) numberOfBallsSlider.getValue(), getAnimationWindowDimension());
    }

    private void addListenersToSliders() {
        numberOfBallsSlider.valueProperty().addListener((oldVal, curVal, newVal) -> configureFlock());
        perceptionRadiusSlider.valueProperty().addListener((oldVal, curVal, newVal) -> flock.getChildren().stream()
                .map(n -> (Boid) n).forEach(ball -> ball.setPerceptionRadius(ball.getBody().getRadius() * newVal.doubleValue())));
        repelDistanceSlider.valueProperty().addListener((oldVal, curVal, newVal) -> flock.getChildren().stream()
                .map(n -> (Boid) n).forEach(ball -> ball.setRepelRadius(ball.getBody().getRadius() * newVal.doubleValue())));
        attractionSlider.valueProperty().addListener((oldVal, curVal, newVal) -> engine.setPullFactor(newVal.doubleValue()));
        repelFactorSlider.valueProperty().addListener((oldVal, curVal, newVal) -> engine.setRepelFactor(newVal.doubleValue()));
    }

    public void reset() {
        resetControls();
        flock.getChildren().stream().map(n -> (Boid) n).forEach(this::setBallParams);
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
        maxBallSizeSlider.setValue(initMaxBallSize);
        numberOfBallsSlider.setValue(initNumberOfBalls);
        accelerationSlider.setValue(initAccelerationUserSelectedBall);
        attractionSlider.setValue(initAttraction);
        repelDistanceSlider.setValue(initRepelDistanceFactor);
        repelFactorSlider.setValue(initRepelFactor);
        frictionSlider.setValue(initFriction);
        perceptionRadiusSlider.setValue(initPerceptionRadius);
        maxSpeedSlider.setValue(initMaxSpeed);
        showConnectionsButton.setSelected(initShowConnections);
        showPathSelectedButton.setSelected(initShowPath);
        showAllPathsButton.setSelected(initShowPath);
        showVelocityVectorButton.setSelected(initShowVelocity);
        showAccelerationVectorButton.setSelected(initShowAcceleration);
        showPerceptionButton.setSelected(initShowPerception);
        showPerceptionSelectedBallButton.setSelected(initShowPerception);
        bounceWallsButton.setSelected(initBounceWallsButtonValue);
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
        if (!((ToggleButton) actionEvent.getSource()).isSelected()) getAppManager().getStage().setOpacity(1);
        else getAppManager().getStage().setOpacity(STAGE_OPACITY);
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
        flock.getChildren().stream().map(n -> (Boid) n).forEach(ball2D -> ball2D.getPath().setVisible(showPaths));
        showPathSelectedButton.setSelected(showPaths);
    }

    @FXML
    private void fullScreenButtonAction(ActionEvent actionEvent) {
        appManager.getStage().setFullScreen(((ToggleButton) actionEvent.getSource()).isSelected());
    }

    @FXML
    private void showPerceptionRadiusButtonAction(ActionEvent event) {
        boolean visible = ((ToggleButton) event.getSource()).isSelected();
        flock.getChildren().stream().map(n -> (Boid) n).forEach(ball2D -> ball2D.getPerceptionCircle().setVisible(visible));
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
        flock.getChildren().stream().map(n -> (Boid) n).forEach(ball2D -> ball2D.getRepelCircle().setVisible(visible));
    }

    @FXML
    public void showVelocitiesButtonAction(ActionEvent event) {
        boolean visible = ((ToggleButton) event.getSource()).isSelected();
        flock.getChildren().stream().map(n -> (Boid) n).forEach(ball2D -> ball2D.getVisibleVelocityVector().setVisible(visible));
    }

    @FXML
    public void showAccelerationsButtonAction(ActionEvent event) {
        boolean visible = ((ToggleButton) event.getSource()).isSelected();
        flock.getChildren().stream().map(n -> (Boid) n).forEach(ball2D -> ball2D.getVisibleAccelerationVector().setVisible(visible));
    }

    @FXML
    private void physicsEngineComboBoxAction(ActionEvent event) {
        flock.setFlockingSim((Engine.FlockingSim) ((ComboBox<?>) event.getSource()).getValue());
    }

    @FXML
    private void flockTypeDropdownAction() {
        flock.controlFlockSize(0, getAnimationWindowDimension());
        configureFlock();
        uniformBallColorPicker.setDisable(flock.getFlockType().equals(flock.getRandom()));
        flock.setSelectedBall(flock.getRandomNewSelectedBall());
    }

    protected AbstractSceneController getBean() {
        return this;
    }


}
