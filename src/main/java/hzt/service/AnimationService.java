package hzt.service;

import hzt.model.entity.Boid;
import hzt.model.entity.Flock;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;

import static hzt.model.AppConstants.INIT_FRAME_DURATION;
import static javafx.animation.Animation.INDEFINITE;

public class AnimationService {

    public static final int LINE_STROKE_WIDTH = 2;

    private final Timeline timeline;

    public AnimationService() {
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

    public void run(Flock flock, Dimension2D animationWindowSize, double accelerationMultiplier, double frictionFactor, boolean bounce, double maxSpeed) {
        for (Node node : flock.getChildren()) {
            Boid boid = (Boid) node;
            if (bounce) boid.bounceOfEdges(animationWindowSize);
            else boid.floatThroughEdges(animationWindowSize);
            boid.update(timeline.getCycleDuration(), accelerationMultiplier, frictionFactor, maxSpeed);
        }
    }

    public void startTimeline() {
        timeline.play();
    }

    public void pauseTimeline() {
        timeline.pause();
    }

}
