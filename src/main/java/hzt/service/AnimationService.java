package hzt.service;

import hzt.model.entity.Boid;
import hzt.model.entity.Flock;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.util.Duration;

import java.time.LocalTime;

import static hzt.model.AppConstants.INIT_FRAME_DURATION;
import static javafx.animation.Animation.INDEFINITE;

public class AnimationService {

    public static final int LINE_STROKE_WIDTH = 2;

    private final LocalTime startTimeSim;
    private final StatisticsService statisticsService;
    private final Timeline animationTimeline;
    private final Timeline statisticsTimeline;

    public AnimationService(LocalTime startTimeSim, StatisticsService statisticsService) {
        this.startTimeSim = startTimeSim;
        this.statisticsService = statisticsService;
        this.animationTimeline = setupTimeLine();
        this.statisticsTimeline = setupTimeLine();
    }

    public Timeline setupTimeLine() {
        Timeline t = new Timeline();
        t.setCycleCount(INDEFINITE);
        return t;
    }

    public void addAnimationLoopToTimeline(EventHandler<ActionEvent> animationLoop, EventHandler<ActionEvent> statisticsLoop, boolean start) {
        animationTimeline.getKeyFrames().add(new KeyFrame(INIT_FRAME_DURATION, "Animation keyframe", animationLoop));
        statisticsTimeline.getKeyFrames().add(new KeyFrame(INIT_FRAME_DURATION, "Statistics keyframe", statisticsLoop));
        if (start) {
            animationTimeline.play();
            statisticsTimeline.play();
        }
    }

    public void runStatistics(Flock flock, double frictionFactor) {
        Boid selected = flock.getSelectedBoid();
        Duration runTimeSim = Duration.millis((LocalTime.now().toNanoOfDay() - startTimeSim.toNanoOfDay()) / 1e6);
        statisticsService.showStatisticsAboutSelectedBall(selected);
        statisticsService.showGlobalStatistics(frictionFactor, flock.getChildren().size(), runTimeSim);
    }

    public void run(Flock flock, Dimension2D animationWindowSize, double accelerationMultiplier, double frictionFactor, boolean bounce, double maxSpeed) {
        Boid selected = flock.getSelectedBoid();
        Duration runTimeSim = Duration.millis((LocalTime.now().toNanoOfDay() - startTimeSim.toNanoOfDay()) / 1e6);
        statisticsService.showStatisticsAboutSelectedBall(selected);
        statisticsService.showGlobalStatistics(frictionFactor, flock.getChildren().size(), runTimeSim);
        flock.getChildren().stream().map(ball2D -> (Boid) ball2D).forEach(ball -> {
            if (bounce) ball.bounceOfEdges(animationWindowSize);
            else ball.floatThroughEdges(animationWindowSize);
            ball.update(animationTimeline.getCycleDuration(), accelerationMultiplier, frictionFactor, maxSpeed);
        });
    }

    public void startAnimationTimeline() {
        animationTimeline.play();
    }

    public void pauseAnimationTimeline() {
        animationTimeline.pause();
    }

}
