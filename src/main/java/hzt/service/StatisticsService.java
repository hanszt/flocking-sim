package hzt.service;

import hzt.model.AppConstants;
import hzt.model.entity.Boid;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.util.Duration;
import lombok.Data;

public class StatisticsService {

   private final SelectedBoidLabelDto selectedDto;
   private final GeneralStatsLabelDto generalStatsDto;
    private final SimpleFrameRateMeter frameRateMeter = new SimpleFrameRateMeter();

    public StatisticsService(SelectedBoidLabelDto selectedDto, GeneralStatsLabelDto generalStatsDto) {
        this.selectedDto = selectedDto;
        this.generalStatsDto = generalStatsDto;
        frameRateMeter.initialize();
    }

    private static final String TWO_DEC_DOUBLE = "%-4.2f";

    public void showStatisticsAboutSelectedBall(Boid selected) {
        if (selected != null) {
            Point2D centerPos = selected.getCenterPosition();
            selectedDto.getBoidNameLabel().setText(String.format("%s", selected.getName()));
            selectedDto.getPositionXLabel().setText(String.format(TWO_DEC_DOUBLE, centerPos.getX()));
            selectedDto.getPositionYLabel().setText(String.format(TWO_DEC_DOUBLE, centerPos.getY()));
            selectedDto.getVelocityMagnitudeLabel().setText(String.format(TWO_DEC_DOUBLE + " p/s", selected.getVelocity().magnitude()));
            selectedDto.getAccelerationMagnitudeLabel().setText(String.format(TWO_DEC_DOUBLE + " p/s^2", selected.getAcceleration().magnitude()));
            selectedDto.getNrOfBallsInPerceptionRadiusLabel().setText(String.format("%-3d", selected.getPerceptionRadiusMap().size()));
            selectedDto.getBoidSizeLabel().setText(String.format(TWO_DEC_DOUBLE + " p", selected.getBody().getRadius() * 2));
        }
    }

    public void showGlobalStatistics(double friction, int size, Duration runTimeSim) {
        generalStatsDto.getFrictionLabel().setText(String.format(TWO_DEC_DOUBLE, friction));
        generalStatsDto.getFrameRateLabel().setText(String.format(TWO_DEC_DOUBLE + " f/s", frameRateMeter.frameRate));
        generalStatsDto.getNrOfBoidsLabel().setText(String.format("%-3d", size));
        generalStatsDto.getRunTimeLabel().setText(String.format("%-4.3f seconds", runTimeSim.toSeconds()));
    }

    private static class SimpleFrameRateMeter {

        private final long[] frameTimes = new long[100];
        private int frameTimeIndex = 0;
        private boolean arrayFilled = false;
        private double frameRate = AppConstants.INIT_FRAME_RATE;

        private void initialize() {
            AnimationTimer frameRateTimer = new AnimationTimer() {

                @Override
                public void handle(long now) {
                    long oldFrameTime = frameTimes[frameTimeIndex];
                    frameTimes[frameTimeIndex] = now;
                    frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
                    if (frameTimeIndex == 0) arrayFilled = true;
                    if (arrayFilled) {
                        long elapsedNanos = now - oldFrameTime;
                        long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
                        frameRate = 1e9 / elapsedNanosPerFrame;
                    }
                }
            };
            frameRateTimer.start();
        }

    }

    @Data
    public static class SelectedBoidLabelDto {

        private final Label boidNameLabel;
        private final Label positionXLabel;
        private final Label positionYLabel;
        private final Label velocityMagnitudeLabel;
        private final Label accelerationMagnitudeLabel;
        private final Label nrOfBallsInPerceptionRadiusLabel;
        private final Label boidSizeLabel;

    }

    @Data
    public static class GeneralStatsLabelDto {

        private final Label frictionLabel;
        private final Label frameRateLabel;
        private final Label nrOfBoidsLabel;
        private final Label runTimeLabel;

    }
}
