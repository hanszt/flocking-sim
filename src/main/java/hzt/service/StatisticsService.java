package hzt.service;

import javafx.animation.AnimationTimer;

import static hzt.model.AppConstants.INIT_FRAME_RATE;

public class StatisticsService {

    private final SimpleFrameRateMeter simpleFrameRateMeter = new SimpleFrameRateMeter();

    public StatisticsService() {
        simpleFrameRateMeter.initialize();
    }

    public static class SimpleFrameRateMeter {

        private final long[] frameTimes = new long[100];
        private int frameTimeIndex = 0;
        private boolean arrayFilled = false;
        private double frameRate = INIT_FRAME_RATE;

        private void initialize() {
            AnimationTimer frameRateTimer = new AnimationTimer() {

                @Override
                public void handle(long now) {
                    frameRateTimer(now);
                }
            };
            frameRateTimer.start();
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

    public SimpleFrameRateMeter getSimpleFrameRateMeter() {
        return simpleFrameRateMeter;
    }
}
