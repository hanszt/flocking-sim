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
import static javafx.scene.paint.Color.DARKBLUE;

public class MainSceneController extends AbstractSceneController {

    private static final Color INIT_BG_COLOR = DARKBLUE.darker().darker().darker();

    private Color backgroundColor = INIT_BG_COLOR;
    private final Flock flock;
    private final AnimationService as;

    public MainSceneController(AppManager appManager) {
        super(MAIN_SCENE.getEnglishDescription(), MAIN_SCENE.getFxmlFileName(), appManager);
        as = new AnimationService(this);
        flock = new Flock(as);
    }

    @FXML
    public SplitPane sliderSplitPane;
    @FXML
    private AnchorPane animationPane;
    @FXML
    private VBox mainControlPanel;
    @FXML
    public VBox statisticsPanel;
    @FXML
    private HBox slidersPane;

    @FXML
    private ComboBox<String> ballSelectorDropdown;
    @FXML
    private ComboBox<String> flockSettingsComboBox;

    @FXML
    private ToggleButton velocityButton;
    @FXML
    private ToggleButton bounceWallsButton;
    @FXML
    private ToggleButton showPathButton;
    @FXML
    public ToggleButton showPerceptionButton;
    @FXML
    public ToggleButton showAccelerationVectorButton;
    @FXML
    public ToggleButton showConnectionsButton;

    @FXML
    public ColorPicker ballColorPicker;
    @FXML
    public ColorPicker backgroundColorPicker;

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
    public Slider repelDistanceSlider;
    @FXML
    public Slider maxSpeedSlider;
    @FXML
    public Slider maxBallSizeSlider;

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
    private Label numberOfBallsLabel;
    @FXML
    private Label runTimeLabel;

    @Override
    public void setup() {
        addListenersToSliders();
        configureColorPickers();
        configureComboBoxes();
        setupFlock();
        maxSpeedSlider.setValue(300);
        PhysicsEngine.setPullFactor(attractionSlider.getValue());
        PhysicsEngine.setRepelFactor(repelFactorSlider.getValue());
        PhysicsEngine.setRepelDistanceFactor(repelDistanceSlider.getValue());
        if (!animationPane.getChildren().contains(flock)) animationPane.getChildren().add(flock);
        animationPane.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
        animationPane.setOpacity(1);
        EventHandler<ActionEvent> animationLoop = initializeAnimationLoop();
        as.addAnimationLoopToTimeline(animationLoop, true);
    }

    public static final String RANDOM_FLOCK = "Random flock", UNIFORM_FLOCK = "Uniform Flock";

    private void configureComboBoxes() {
        flockSettingsComboBox.getItems().addAll(RANDOM_FLOCK, UNIFORM_FLOCK);
        flockSettingsComboBox.setValue(RANDOM_FLOCK);
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
        ballColorPicker.setValue(flock.getUniformBallColor());
    }

    public Dimension2D getAnimationWindowDimension() {
        return new Dimension2D(scene.getWidth() - mainControlPanel.getWidth(),
                scene.getHeight() - slidersPane.getHeight());
    }

    private void setupFlock() {
        flock.setShowVelocityVector(velocityButton.isSelected());
        flock.setShowAccelerationVector(showAccelerationVectorButton.isSelected());
        flock.setShowPerceptionCircle(showPerceptionButton.isSelected());
        flock.setShowConnections(showConnectionsButton.isSelected());
        flock.setPerceptionRadiusRatio(perceptionRadiusSlider.getValue());
        flock.setMaxBallSize(maxBallSizeSlider.getValue());
        flock.setFlockType(flockSettingsComboBox.getValue());
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
        flock.reset();
        flock.setShowAccelerationVector(showAccelerationVectorButton.isSelected());
        flock.setShowConnections(showConnectionsButton.isSelected());
        flock.setShowVelocityVector(velocityButton.isSelected());
        flock.setShowPerceptionCircle(showPerceptionButton.isSelected());
    }

    private void resetControls() {
        numberOfBallsSlider.setValue(150);
        accelerationSlider.setValue(5);
        attractionSlider.setValue(3);
        repelDistanceSlider.setValue(3);
        repelFactorSlider.setValue(10);
        frictionSlider.setValue(0);
        maxBallSizeSlider.setValue(5);
        perceptionRadiusSlider.setValue(25);
        maxSpeedSlider.setValue(300);
        showConnectionsButton.setSelected(false);
        showPathButton.setSelected(false);
        velocityButton.setSelected(false);
        showAccelerationVectorButton.setSelected(false);
        showPerceptionButton.setSelected(false);
    }

    @FXML
    void setBackgroundColor(ActionEvent event) {
        backgroundColor = ((ColorPicker) event.getSource()).getValue();
        animationPane.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    void setSelectedBallColor(ActionEvent event) {
        Color color = ((ColorPicker) event.getSource()).getValue();
        flock.setUniformBallColor(color);
        for(Node n : flock.getChildren()) {
            Ball2D ball2D = (Ball2D) n;
            ball2D.updatePaint(color);
        }
    }

    @FXML
    public void pauseSim(ActionEvent actionEvent) {
        if (((ToggleButton) actionEvent.getSource()).isSelected()) as.pauseTimeline();
        else as.startTimeline();
    }

    @FXML
    public void setTransparent(ActionEvent actionEvent) {
        if (!((ToggleButton) actionEvent.getSource()).isSelected()) getAppManager().getStage().setOpacity(1);
        else getAppManager().getStage().setOpacity(STAGE_OPACITY);
    }

    @FXML
    public void showConnections(ActionEvent actionEvent) {
        flock.setShowConnections(((ToggleButton) actionEvent.getSource()).isSelected());
    }
    @FXML
    public void showPerception(ActionEvent actionEvent) {
        flock.setShowPerceptionCircle(((ToggleButton) actionEvent.getSource()).isSelected());
    }

    @FXML
    public void showVelocityVector(ActionEvent actionEvent) {
        flock.setShowVelocityVector(((ToggleButton) actionEvent.getSource()).isSelected());
    }

    @FXML
    public void showAccelerationVector(ActionEvent actionEvent) {
        flock.setShowAccelerationVector(((ToggleButton) actionEvent.getSource()).isSelected());
    }

    @FXML
    public void showPathSelectedBall(ActionEvent event) {
        Ball2D ball = flock.getSelectedBall();
        if (ball != null) ball.getPath().setVisible(((ToggleButton) event.getSource()).isSelected());
    }

    @FXML
    public void ballSelectorMenuAction(ActionEvent event) {
        ((ComboBox<?>) event.getSource()).getValue();
    }

    @FXML
    public void flockSettingsDropdownAction() {
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

    public Label getNumberOfBallsLabel() {
        return numberOfBallsLabel;
    }

    public Label getRunTimeLabel() {
        return runTimeLabel;
    }


}
