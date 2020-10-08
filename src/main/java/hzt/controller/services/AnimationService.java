package hzt.controller.services;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class AnimationService {

    public static final Duration INIT_FRAME_DURATION = Duration.millis(10); ///s
    private final Timeline timeline;

    public AnimationService() {
        this.timeline = setupTimeLine();
    }

    public Timeline setupTimeLine() {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        return timeline;
    }

    public void addKeyframeToTimeline(KeyFrame keyFrame, boolean start) {
        timeline.getKeyFrames().add(keyFrame);
        if (start) timeline.play();
    }

    public void removeKeyFrameFromTimeline(KeyFrame keyFrame)  {
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
