package hzt.controller.main_scene;

import hzt.controller.AbstractSceneController;
import hzt.controller.AppManager;
import hzt.controller.utils.PhysicsEngine;
import hzt.model.entity.Ball2D;
import hzt.model.entity.BallGroup;
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

public class MainSceneController extends AbstractSceneController {

    private final BallGroup ballGroup;
    private final AnimationService as;

    public MainSceneController(AppManager appManager) {
        super(MAIN_SCENE.getEnglishDescription(), MAIN_SCENE.getFxmlFileName(), appManager);
        as = new AnimationService(this);
        ballGroup = new BallGroup(as);
    }

    @FXML
    private AnchorPane root;
    @FXML
    private SplitPane mainSplitPane;
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
    private ComboBox<?> dropDown1;
    @FXML
    private ComboBox<?> dropDown2;
    @FXML
    private ToggleButton gravityButton;
    @FXML
    private Slider numberOfBallsSlider;
    @FXML
    private Slider perceptionRadiusSlider;
    @FXML
    private Slider frictionSlider;
    @FXML
    private Slider gravitySlider;
    @FXML
    private Slider speedSlider;
    @FXML
    private Slider accelerationSlider;

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
        setupBallGroup();
        animationPane.getChildren().add(ballGroup);
        animationPane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        EventHandler<ActionEvent> animationLoop = e -> {
            double friction = frictionSlider.getValue();
            double speedAdder = accelerationSlider.getValue();
            double accelerationMultiplier = accelerationSlider.getValue();
            as.run(ballGroup, accelerationMultiplier, friction, gravityButton.isSelected());
        };
        as.addAnimationLoopToTimeline(animationLoop, true);
        addListenersToSliders();
    }

    private void setupBallGroup() {
        ballGroup.controlBallAmount((int) numberOfBallsSlider.getValue(),
                perceptionRadiusSlider.getValue(), new Dimension2D(scene.getWidth(), scene.getHeight()));
    }


    private void addListenersToSliders() {
        numberOfBallsSlider.valueProperty().addListener((oldVal, curVal, newVal) -> setupBallGroup());
        perceptionRadiusSlider.valueProperty().addListener((oldVal, curVal, newVal) -> {
            for (Node n : ballGroup.getChildren()) {
                Ball2D ball = (Ball2D) n;
                ball.setPerceptionRadius(ball.getBody().getRadius() * newVal.doubleValue());
            }
        });
        gravitySlider.valueProperty().addListener((oldVal, curVal, newVal) -> PhysicsEngine.setGravity(newVal.doubleValue()));
    }


    @FXML
    void setColor1(ActionEvent event) {

    }

    @FXML
    void setColor2(ActionEvent event) {

    }

    @FXML
    void setColor3(ActionEvent event) {

    }

    @FXML
    void setColor4(ActionEvent event) {

    }

    public void button4Action(ActionEvent actionEvent) {
        Ball2D ball = ballGroup.getSelectedBall();
        System.out.println((ball.getChildren().size()));
    }

    public void reset(ActionEvent actionEvent) {
        System.out.println("Button 1 pressed");
    }

    public void button3Action(ActionEvent actionEvent) {
    }

    public void pauseSim(ActionEvent actionEvent) {
        if (((ToggleButton) actionEvent.getSource()).isSelected()) as.pauseTimeline();
        else as.startTimeline();
    }

    public void setTransparent(ActionEvent actionEvent) {
        if (!((ToggleButton) actionEvent.getSource()).isSelected()) getAppManager().getStage().setOpacity(1);
        else getAppManager().getStage().setOpacity(STAGE_OPACITY);
    }

    public void showConnections(ActionEvent actionEvent) {
        for (Node node : ballGroup.getChildren()) {
            Ball2D ball2D = (Ball2D) node;
            ball2D.setShowConnections(((ToggleButton) actionEvent.getSource()).isSelected());
        }

    }

    public void showBalls(ActionEvent actionEvent) {
    }

    public void bounceOfWalls(ActionEvent actionEvent) {
    }

    public void enableGravity(ActionEvent actionEvent) {
    }

    public void showPerception(ActionEvent actionEvent) {
    }

    public void showTrails(ActionEvent actionEvent) {
    }

    public void dropDownMenu1Action(ActionEvent actionEvent) {
    }

    public void dropDownMenu2Action(ActionEvent actionEvent) {
    }

    protected AbstractSceneController getBean() {
        return this;
    }

    public BallGroup getBallGroup() {
        return ballGroup;
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
