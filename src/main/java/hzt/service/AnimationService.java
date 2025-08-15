package hzt.service;

import hzt.model.entity.Flock;
import hzt.model.entity.boid.Boid;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.function.Consumer;

import static hzt.model.PropertyLoader.parsedIntAppProp;
import static javafx.animation.Animation.INDEFINITE;

public class AnimationService {

    private static final int INIT_FRAME_RATE = parsedIntAppProp("framerate", 30);// f/s
    private static final Duration INIT_FRAME_DURATION = Duration.seconds(1.0 / INIT_FRAME_RATE); // s/f

    private final Timeline timeline;

    public AnimationService() {
        this.timeline = setupTimeLine();
    }

    private static Timeline setupTimeLine() {
        final var t = new Timeline();
        t.setCycleCount(INDEFINITE);
        return t;
    }

    public void addAnimationLoopToTimeline(final EventHandler<ActionEvent> animationLoop) {
        final var animationLoopKeyFrame = new KeyFrame(INIT_FRAME_DURATION, "Ball sim", animationLoop);
        timeline.getKeyFrames().add(animationLoopKeyFrame);
        timeline.play();
    }

    public void run(final Flock flock, final double accelerationMultiplier,
                    final double frictionFactor, final double maxSpeed, final Consumer<Boid> boidUpdater) {
        for (final var boid : flock) {
            boidUpdater.accept(boid);
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
