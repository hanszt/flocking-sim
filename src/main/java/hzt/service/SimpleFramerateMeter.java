package hzt.service;

import javafx.animation.AnimationTimer;

public class SimpleFramerateMeter {

    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0;
    private boolean arrayFilled = false;
    private double frameRate;

    public SimpleFramerateMeter() {
        initialize();
    }

    private void initialize() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                frameRateTimer(now);
            }
        }.start();
    }

    private void frameRateTimer(long now) {
        long oldFrameTime = frameTimes[frameTimeIndex];
        frameTimes[frameTimeIndex] = now;
        frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
        if (frameTimeIndex == 0) {
            arrayFilled = true;
        }
        if (arrayFilled) {
            long elapsedNanos = now - oldFrameTime;
            long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
            frameRate = 1e9 / elapsedNanosPerFrame;
        }
    }

    public double getFrameRate() {
        return frameRate;
    }
}
