package hzt.controller.main_scene;

import hzt.controller.AbstractSceneController;
import hzt.controller.AnimationService;
import hzt.controller.AppManager;
import hzt.controller.utils.PhysicsEngine;
import hzt.model.entity.Ball2D;
import hzt.model.entity.Flock;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import static hzt.controller.AppConstants.STAGE_OPACITY;
import static hzt.controller.AppConstants.Scene.MAIN_SCENE;
import static hzt.model.entity.Flock.INIT_SELECTED_BALL_COLOR;
import static hzt.model.entity.Flock.INIT_UNIFORM_BALL_COLOR;
import static javafx.scene.paint.Color.DARKBLUE;

public class MainSceneController extends AbstractSceneController {

    private static final Color INIT_BG_COLOR = DARKBLUE.darker().darker().darker();

    private final Flock flock;
    private final AnimationService as;

    public MainSceneController(AppManager appManager) {
        super(MAIN_SCENE.getEnglishDescription(), MAIN_SCENE.getFxmlFileName(), appManager);
        as = new AnimationService(this);
        flock = new Flock(as);
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
    private AnchorPane animationPane;
    @FXML
    private VBox mainControlPanel;
    @FXML
    private HBox slidersPane;

    @FXML
    private ComboBox<String> physicsEngineComboBox;
    @FXML
    private ComboBox<String> flockSettingsComboBox;

    @FXML
    private ToggleButton velocityButton;
    @FXML
    private ToggleButton bounceWallsButton;
    @FXML
    private ToggleButton showPathButton;
    @FXML
    private ToggleButton showPerceptionButton;
    @FXML
    private ToggleButton showAccelerationVectorButton;
    @FXML
    private ToggleButton showConnectionsButton;

    @FXML
    private ColorPicker ballColorPicker;
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
        resetControls();
        addListenersToSliders();
        configureColorPickers();
        configureComboBoxes();
        setupFlock();
        PhysicsEngine.setPullFactor(attractionSlider.getValue());
        PhysicsEngine.setRepelFactor(repelFactorSlider.getValue());
        PhysicsEngine.setRepelDistanceFactor(repelDistanceSlider.getValue());
        animationPane.getChildren().add(flock);
        animationPane.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
        EventHandler<ActionEvent> animationLoop = initializeAnimationLoop();
        as.addAnimationLoopToTimeline(animationLoop, true);
    }

    public static final String RANDOM_FLOCK = "Random flock", UNIFORM_FLOCK = "Uniform Flock";
    public static final String ENGINE_TYPE_1 = "Engine type 1", ENGINE_TYPE_2 = "Engine type 2";

    private void configureComboBoxes() {
        flockSettingsComboBox.getItems().addAll(RANDOM_FLOCK, UNIFORM_FLOCK);
        flockSettingsComboBox.setValue(RANDOM_FLOCK);
        physicsEngineComboBox.getItems().addAll(ENGINE_TYPE_1, ENGINE_TYPE_2);
        physicsEngineComboBox.setValue(ENGINE_TYPE_1);
    }

    private EventHandler<ActionEvent> initializeAnimationLoop() {
        return loop -> {
            double maxSpeed = maxSpeedSlider.getValue();
            double friction = frictionSlider.getValue();
            double accelerationMultiplier = accelerationSlider.getValue();
            boolean bounce = bounceWallsButton.isSelected();
            as.run(flock, accelerationMultiplier, friction, bounce, maxSpeed);
        };
    }

    private void configureColorPickers() {
        backgroundColorPicker.setValue(backgroundColor);
        ballColorPicker.setValue(INIT_UNIFORM_BALL_COLOR);
        selectedBallColorPicker.setValue(INIT_SELECTED_BALL_COLOR);
    }

    public Dimension2D getAnimationWindowDimension() {
        Dimension2D sceneDimension;
        if (scene.getWidth() == 0 && scene.getHeight() == 0) {
            sceneDimension = new Dimension2D(root.getPrefWidth(), root.getPrefHeight());
        } else {
            sceneDimension = new Dimension2D(scene.getWidth(), scene.getHeight());
        }
        return new Dimension2D(sceneDimension.getWidth() - mainControlPanel.getWidth(),
                sceneDimension.getHeight() - slidersPane.getHeight());
    }

    private void setupFlock() {
        flock.setShowVelocityVector(velocityButton.isSelected());
        flock.setShowAccelerationVector(showAccelerationVectorButton.isSelected());
        flock.setShowPerceptionCircle(showPerceptionButton.isSelected());
        flock.setShowConnections(showConnectionsButton.isSelected());
        flock.setPerceptionRadiusRatio(perceptionRadiusSlider.getValue());
        flock.setMaxBallSize(maxBallSizeSlider.getValue());
        flock.setFlockType(flockSettingsComboBox.getValue());
        flock.setPhysicsEngine(physicsEngineComboBox.getValue());
        var parentDimension = getAnimationWindowDimension();
        var numberOfBalls = (int) numberOfBallsSlider.getValue();
        flock.controlFlockSize(numberOfBalls, parentDimension);
    }

    private void addListenersToSliders() {
        numberOfBallsSlider.valueProperty().addListener((oldVal, curVal, newVal) -> setupFlock());
        perceptionRadiusSlider.valueProperty().addListener((oldVal, curVal, newVal) -> {
            for (Node n : flock.getChildren()) {
                Ball2D ball = (Ball2D) n;
                ball.setPerceptionRadius(ball.getBody().getRadius() * newVal.doubleValue());
            }
        });
        attractionSlider.valueProperty().addListener((oldVal, curVal, newVal) -> PhysicsEngine.setPullFactor(newVal.doubleValue()));
        repelFactorSlider.valueProperty().addListener((oldVal, curVal, newVal) -> PhysicsEngine.setRepelFactor(newVal.doubleValue()));
        repelDistanceSlider.valueProperty().addListener((oldVal, curVal, newVal) -> PhysicsEngine.setRepelDistanceFactor(newVal.doubleValue()));
    }

    public void reset() {
        resetControls();
        flock.setShowAccelerationVector(showAccelerationVectorButton.isSelected());
        flock.setShowConnections(showConnectionsButton.isSelected());
        flock.setShowVelocityVector(velocityButton.isSelected());
        flock.setShowPerceptionCircle(showPerceptionButton.isSelected());
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
        showPathButton.setSelected(initShowPath);
        velocityButton.setSelected(initShowVelocity);
        showAccelerationVectorButton.setSelected(initShowAcceleration);
        showPerceptionButton.setSelected(initShowPerception);
        bounceWallsButton.setSelected(initBounceWallsButtonValue);
    }

    @FXML
    private void setBackgroundColor(ActionEvent event) {
        backgroundColor = ((ColorPicker) event.getSource()).getValue();
        animationPane.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void setUniformBallColor(ActionEvent event) {
        Color color = ((ColorPicker) event.getSource()).getValue();
        flock.setUniformBallColor(color);
        for (Node n : flock.getChildren()) {
            Ball2D ball2D = (Ball2D) n;
            ball2D.updatePaint(color);
        }
    }

    @FXML
    private void setSelectedBallColor(ActionEvent event) {
        Color color = ((ColorPicker) event.getSource()).getValue();
        flock.setSelectedBallColor(color);
        flock.getSelectedBall().updatePaint(color);
    }

    @FXML
    private void pauseSim(ActionEvent actionEvent) {
        if (((ToggleButton) actionEvent.getSource()).isSelected()) as.pauseTimeline();
        else as.startTimeline();
    }

    @FXML
    private void setTransparent(ActionEvent actionEvent) {
        if (!((ToggleButton) actionEvent.getSource()).isSelected()) getAppManager().getStage().setOpacity(1);
        else getAppManager().getStage().setOpacity(STAGE_OPACITY);
    }

    @FXML
    private void showConnections(ActionEvent actionEvent) {
        flock.setShowConnections(((ToggleButton) actionEvent.getSource()).isSelected());
    }

    @FXML
    private void showPerception(ActionEvent actionEvent) {
        flock.setShowPerceptionCircle(((ToggleButton) actionEvent.getSource()).isSelected());
    }

    @FXML
    private void showVelocityVector(ActionEvent actionEvent) {
        flock.setShowVelocityVector(((ToggleButton) actionEvent.getSource()).isSelected());
    }

    @FXML
    private void showAccelerationVector(ActionEvent actionEvent) {
        flock.setShowAccelerationVector(((ToggleButton) actionEvent.getSource()).isSelected());
    }

    @FXML
    private void showPathSelectedBall(ActionEvent event) {
        boolean showPath = ((ToggleButton) event.getSource()).isSelected();
        Ball2D ball = flock.getSelectedBall();
        flock.setShowPath(showPath);
        if (ball != null) ball.getPath().setPathVisible(showPath);
    }

    @FXML
    private void physicsEngineComboBoxAction(ActionEvent event) {
        flock.setPhysicsEngine((String) ((ComboBox<?>) event.getSource()).getValue());
    }

    @FXML
    private void flockSettingsDropdownAction() {
        flock.controlFlockSize(0, getAnimationWindowDimension());
        setupFlock();
    }

    protected AbstractSceneController getBean() {
        return this;
    }

    public Flock getBallGroup() {
        return flock;
    }

    public Label getBallNameLabel() {
        return ballNameLabel;
    }

    public Label getPositionStatsLabel() {
        return positionStatsLabel;
    }

    public Label getVelocityStatsLabel() {
        return velocityStatsLabel;
    }

    public Label getAccelerationStatsLabel() {
        return accelerationStatsLabel;
    }

    public Label getFrictionStatsLabel() {
        return frictionStatsLabel;
    }

    public Label getFrameRateStatsLabel() {
        return frameRateStatsLabel;
    }

    public Label getNrOfBallsInPerceptionRadiusLabel() {
        return nrOfBallsInPerceptionRadiusLabel;
    }

    public Label getBallSizeLabel() {
        return ballSizeLabel;
    }

    public Label getNumberOfBallsLabel() {
        return numberOfBallsLabel;
    }

    public Label getRunTimeLabel() {
        return runTimeLabel;
    }

}
