package hzt.service;

import hzt.controller.MainSceneController;
import hzt.model.entity.Boid;
import hzt.model.entity.Flock;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.util.Duration;

import java.time.LocalTime;

import static javafx.animation.Animation.INDEFINITE;

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
        Timeline t = new Timeline();
        t.setCycleCount(INDEFINITE);
        return t;
    }

    public void addAnimationLoopToTimeline(EventHandler<ActionEvent> animationLoop, boolean start) {
        KeyFrame animationLoopKeyFrame = new KeyFrame(INIT_FRAME_DURATION, "Ball sim", animationLoop);
        timeline.getKeyFrames().add(animationLoopKeyFrame);
        if (start) timeline.play();
    }

    public void run(Flock flock, double accelerationMultiplier, double frictionFactor, boolean bounce, double maxSpeed) {
        Boid selected = mainSceneController.getFlock().getSelectedBall();
        Dimension2D animationWindowSize = mainSceneController.getAnimationWindowDimension();
        LocalTime startTimeSim = mainSceneController.getStartTimeSim();
        Duration runTimeSim = Duration.millis((LocalTime.now().toNanoOfDay() - startTimeSim.toNanoOfDay()) / 1e6);
        statisticsService.showStatisticsAboutSelectedBall(selected);
        statisticsService.showGlobalStatistics(frictionFactor, timeline.getCycleDuration(), flock.getChildren().size(), runTimeSim);
        flock.getChildren().stream().map(ball2D -> (Boid) ball2D).forEach(ball -> {
            if (bounce) ball.bounceOfEdges(animationWindowSize);
            else ball.floatThroughEdges(animationWindowSize);
            ball.update(timeline.getCycleDuration(), accelerationMultiplier, frictionFactor, maxSpeed);
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
