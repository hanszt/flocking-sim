package hzt.controller;

import hzt.controller.main_scene.MainSceneController;
import hzt.controller.main_scene.StatisticsService;
import hzt.model.entity.Ball2D;
import hzt.model.entity.Flock;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationService {

    public static final int INIT_FRAME_RATE = 30; // f/s
    public static final Duration INIT_FRAME_DURATION = Duration.seconds(1. / INIT_FRAME_RATE); // s/f

    public static final int INIT_NUMBER_OF_BALLS = 150;
    public static final int INIT_ACCELERATION_USER_SELECTED_BALL = 5;
    public static final int INIT_ATTRACTION = 3;
    public static final int INIT_REPEL_FACTOR = 10;
    public static final int INIT_REPEL_DISTANCE_FACTOR = 3;
    public static final int INIT_MAX_BALL_SIZE = 5;
    public static final int INIT_PERCEPTION_RADIUS = 25;
    public static final int INIT_MAX_SPEED = 300;
    public static final boolean INIT_SHOW_CONNECTIONS = false;
    public static final boolean INIT_SHOW_PATH = false;
    public static final boolean INIT_SHOW_VELOCITY = false;
    public static final boolean INIT_SHOW_ACCELERATION = false;


//      numberOfBallsSlider.setValue(150);
//        accelerationSlider.setValue(5);
//        attractionSlider.setValue(3);
//        repelDistanceSlider.setValue(3);
//        repelFactorSlider.setValue(10);
//        frictionSlider.setValue(0);
//        maxBallSizeSlider.setValue(5);
//        perceptionRadiusSlider.setValue(25);
//        maxSpeedSlider.setValue(300);
//        showConnectionsButton.setSelected(false);
//        showPathButton.setSelected(false);
//        velocityButton.setSelected(false);
//        showAccelerationVectorButton.setSelected(false);
//        showPerceptionButton.setSelected(false);

    public static final int LINE_STROKE_WIDTH = 2;

    private final MainSceneController mainSceneController;
    private final StatisticsService statisticsService;
    private final Timeline timeline;

    public AnimationService(MainSceneController mainSceneController) {
        this.mainSceneController = mainSceneController;
        this.statisticsService = new StatisticsService(mainSceneController);
        this.timeline = setupTimeLine();
    }

    public Timeline setupTimeLine() {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        return timeline;
    }

    public void addAnimationLoopToTimeline(EventHandler<ActionEvent> animationLoop, boolean start) {
        KeyFrame animationLoopKeyFrame = new KeyFrame(INIT_FRAME_DURATION, "Ball sim", animationLoop);
        timeline.getKeyFrames().add(animationLoopKeyFrame);
        if (start) timeline.play();
    }

    public void run(Flock flock, double accelerationMultiplier, double frictionFactor, boolean bounce, double maxSpeed) {
        Ball2D selected = mainSceneController.getBallGroup().getSelectedBall();
        statisticsService.showStatisticsAboutSelectedBall(selected);
        statisticsService.showGlobalStatistics(frictionFactor, timeline.getCycleDuration(),
                flock.getChildren().size(), mainSceneController.getAppManager().getRunTimeSim());
        for (Node ball2D : flock.getChildren()) {
            Ball2D ball = (Ball2D) ball2D;
            ball.addFriction(frictionFactor);
            if (bounce) ball.bounceOfEdges(mainSceneController.getAnimationWindowDimension());
            else ball.floatThroughEdges(mainSceneController.getAnimationWindowDimension());
            ball.update(timeline.getCycleDuration(), accelerationMultiplier, maxSpeed);
        }
    }

    public void startTimeline() {
        timeline.play();
    }

    public void pauseTimeline() {
        timeline.pause();
    }

    public Timeline getTimeline() {
        return timeline;
    }

}
