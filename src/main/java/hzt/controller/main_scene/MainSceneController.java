package hzt.controller.main_scene;

import hzt.controller.AbstractSceneController;
import hzt.controller.AppManager;
import hzt.controller.services.AnimationService;
import hzt.controller.services.PhysicsEngine;
import hzt.model.entity.Ball2D;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import static hzt.controller.AppConstants.Scene.ABOUT_SCENE;
import static hzt.controller.AppConstants.Scene.MAIN_SCENE;
import static hzt.controller.services.AnimationService.INIT_FRAME_DURATION;

public class MainSceneController extends AbstractSceneController {

    private final MainSceneService ms;
    private final AnimationService as;
    private final PhysicsEngine pe;

    public MainSceneController(AppManager appManager) {
        super(MAIN_SCENE.getEnglishDescription(), MAIN_SCENE.getFxmlFileName(), appManager);
        as = new AnimationService();
        ms = new MainSceneService(as);
        pe = new PhysicsEngine();
    }

    @FXML
    private AnchorPane root;
    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private AnchorPane controlsPane;
    @FXML
    private AnchorPane sliderPane;
    @FXML
    private ComboBox<?> dropDown1;
    @FXML
    private ComboBox<?> dropDown2;
    @FXML
    private ComboBox<?> dropDown3;
    @FXML
    private ToggleButton toggleButton1;
    @FXML
    private ToggleButton toggleButton3;
    @FXML
    private ToggleButton toggleButton2;
    @FXML
    private ToggleButton toggleButton4;
    @FXML
    private Slider numberOfBallsSlider;
    @FXML
    private Slider zoomSlider;
    @FXML
    private Slider FrictionSlider;
    @FXML
    private Slider accelerationSlider;
    @FXML
    private AnchorPane animationPane;
    @FXML
    private Group ballGroup;
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

    @Override
    public void setup() {
        animationPane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        ms.setupBallsAndAddToBallGroup(ballGroup, mainSplitPane, appManager.getStage(), 30);
        EventHandler<ActionEvent> eventHandler = e -> pe.run(as.getTimeline().getCycleDuration(), ballGroup);
        KeyFrame physicsEngineKeyframe = new KeyFrame(INIT_FRAME_DURATION, "Physics engine keyframe", eventHandler);
        as.addKeyframeToTimeline(physicsEngineKeyframe, true);
        setupSliders();
    }

    private void setupSliders() {
        numberOfBallsSlider.valueProperty().addListener((oldVal, curVal, newVal) -> {
           ms.setupBallsAndAddToBallGroup(ballGroup, mainSplitPane, appManager.getStage(), newVal.intValue());
        });
    }

    @FXML
    void exitProgram() {
        appManager.getStage().close();
    }

    @FXML
    void showAbout(ActionEvent event) {
        appManager.setupScene(ABOUT_SCENE);
    }

    @FXML
    void showPreferences(ActionEvent event) {

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

    public void newInstance() {
    }

    public void button4Action(ActionEvent actionEvent) {
    }

    public void button6Action(ActionEvent actionEvent) {
    }

    public void reset(ActionEvent actionEvent) {
        System.out.println("Button 1 pressed");
    }

    public void button3Action(ActionEvent actionEvent) {
    }

    public void button5Action(ActionEvent actionEvent) {
    }

    public void pauseSim(ActionEvent actionEvent) {
        if (((ToggleButton) actionEvent.getSource()).isSelected()) as.pauseTimeline();
        else as.startTimeline();
    }

    public void toggleButton2Action(ActionEvent actionEvent) {
    }

    public void toggleButton3Action(ActionEvent actionEvent) {
    }

    public void toggleButton4Action(ActionEvent actionEvent) {
    }

    public void dropDownMenu1Action(ActionEvent actionEvent) {
    }

    public void dropDownMenu2Action(ActionEvent actionEvent) {
    }

    public void dropDownMenu3Action(ActionEvent actionEvent) {
    }

    protected AbstractSceneController getBean() {
        return this;
    }

    public AnimationService getAs() {
        return as;
    }
}
