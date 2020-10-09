package hzt.controller.main_scene;

import hzt.controller.utils.PhysicsEngine;
import hzt.model.entity.Ball2D;
import hzt.model.entity.BallGroup;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.Set;

public class AnimationService {

    public static final int INIT_FRAME_RATE = 60; // f/s
    public static final Duration INIT_FRAME_DURATION = Duration.seconds(1. / INIT_FRAME_RATE); // s/f

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

    public void run(BallGroup group, double accelerationMultiplier, double frictionFactor, boolean gravity) {
        group.getChildren().forEach(ball2D -> {
            Ball2D ball = (Ball2D) ball2D;
            ball.update(timeline.getCycleDuration(), accelerationMultiplier);
            ball.addFriction(frictionFactor);
            Set<Ball2D> ballsSet = ball.getBallsInPerceptionRadiusMap().keySet();
            if (gravity) ball.setAcceleration(PhysicsEngine.getTotalAccelerationByMassAndOtherBallsInPerceptionRadius(ball, ballsSet));
        });
        Ball2D selected = mainSceneController.getBallGroup().getSelectedBall();
        statisticsService.showStatisticsAboutSelectedBall(selected);
        statisticsService.showGlobalStatistics(frictionFactor, timeline.getCycleDuration(), group.getChildren().size(), mainSceneController.getAppManager().getRunTimeSim());
    }

    public void removeKeyFrameFromTimeline(KeyFrame keyFrame) {
        timeline.getKeyFrames().remove(keyFrame);
    }

    public void startTimeline() {
        timeline.play();
    }

    public void stopTimeline() {
        timeline.stop();
    }

    public void pauseTimeline() {
        timeline.pause();
    }

    public Timeline getTimeline() {
        return timeline;
    }

}
