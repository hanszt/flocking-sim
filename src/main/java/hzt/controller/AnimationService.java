package hzt.controller;

import hzt.controller.main_scene.MainSceneController;
import hzt.controller.main_scene.StatisticsService;
import hzt.model.entity.Ball2D;
import hzt.model.entity.Flock;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.time.LocalTime;

public class AnimationService {

    public static final int INIT_FRAME_RATE = 30; // f/s
    public static final Duration INIT_FRAME_DURATION = Duration.seconds(1. / INIT_FRAME_RATE); // s/f

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
        Ball2D selected = mainSceneController.getFlock().getSelectedBall();
        statisticsService.showStatisticsAboutSelectedBall(selected);
        LocalTime startTimeSim = mainSceneController.getAppManager().startTimeSim, stopTimeSim = LocalTime.now();
        Duration runTimeSim = Duration.millis((stopTimeSim.toNanoOfDay() - startTimeSim.toNanoOfDay()) / 1e6);
        statisticsService.showGlobalStatistics(frictionFactor, timeline.getCycleDuration(),
                flock.getChildren().size(), runTimeSim);
        flock.getChildren().stream().map(ball2D -> (Ball2D) ball2D).forEach(ball -> {
            ball.addFriction(frictionFactor);
            if (bounce) ball.bounceOfEdges(mainSceneController.getAnimationWindowDimension());
            else ball.floatThroughEdges(mainSceneController.getAnimationWindowDimension());
            ball.update(timeline.getCycleDuration(), accelerationMultiplier, maxSpeed);
        });
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
