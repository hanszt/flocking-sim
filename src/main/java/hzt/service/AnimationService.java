package hzt.service;

import hzt.model.entity.boid.Boid;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.Parent;

import static hzt.model.AppConstants.INIT_FRAME_DURATION;
import static javafx.animation.Animation.INDEFINITE;

public class AnimationService {

    private final Timeline timeline;

    public AnimationService() {
        this.timeline = setupTimeLine();
    }

    private static Timeline setupTimeLine() {
        Timeline t = new Timeline();
        t.setCycleCount(INDEFINITE);
        return t;
    }

    public void addAnimationLoopToTimeline(EventHandler<ActionEvent> animationLoop) {
        KeyFrame animationLoopKeyFrame = new KeyFrame(INIT_FRAME_DURATION, "Ball sim", animationLoop);
        timeline.getKeyFrames().add(animationLoopKeyFrame);
        timeline.play();
    }

    public void run(Parent flock, Dimension2D animationWindowSize, double accelerationMultiplier,
                    double frictionFactor, boolean bounce, double maxSpeed) {
        for (Node node : flock.getChildrenUnmodifiable()) {
            Boid boid = (Boid) node;
            if (bounce) {
                boid.bounceOfEdges(animationWindowSize);
            } else {
                boid.floatThroughEdges(animationWindowSize);
            }
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
