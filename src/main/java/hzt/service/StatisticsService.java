package hzt.service;

import hzt.model.entity.Boid;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.util.Duration;
import lombok.Getter;

public class StatisticsService {

   private final SelectedBoidLabelDto selectedDto;
   private final GeneralStatsLabelDto generalStatsDto;

    public StatisticsService(SelectedBoidLabelDto selectedDto, GeneralStatsLabelDto generalStatsDto) {
        this.selectedDto = selectedDto;
        this.generalStatsDto = generalStatsDto;
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

    public void showGlobalStatistics(double friction, Duration cycleDuration, int size, Duration runTimeSim) {
        generalStatsDto.getFrictionLabel().setText(String.format(TWO_DEC_DOUBLE, friction));
        generalStatsDto.getFrameRateLabel().setText(String.format(TWO_DEC_DOUBLE + " f/s", 1 / cycleDuration.toSeconds()));
        generalStatsDto.getNrOfBoidsLabel().setText(String.format("%-3d", size));
        generalStatsDto.getRunTimeLabel().setText(String.format("%-4.3f seconds", runTimeSim.toSeconds()));
    }

    @Getter
    public static class SelectedBoidLabelDto {

        private final Label boidNameLabel;
        private final Label positionXLabel;
        private final Label positionYLabel;
        private final Label velocityMagnitudeLabel;
        private final Label accelerationMagnitudeLabel;
        private final Label nrOfBallsInPerceptionRadiusLabel;
        private final Label boidSizeLabel;

        public SelectedBoidLabelDto(Label boidNameLabel,
                                    Label positionXLabel,
                                    Label positionYLabel,
                                    Label velocityMagnitudeLabel,
                                    Label accelerationMagnitudeLabel,
                                    Label nrOfBallsInPerceptionRadiusLabel,
                                    Label boidSizeLabel) {
            this.boidNameLabel = boidNameLabel;
            this.positionXLabel = positionXLabel;
            this.positionYLabel = positionYLabel;
            this.velocityMagnitudeLabel = velocityMagnitudeLabel;
            this.accelerationMagnitudeLabel = accelerationMagnitudeLabel;
            this.nrOfBallsInPerceptionRadiusLabel = nrOfBallsInPerceptionRadiusLabel;
            this.boidSizeLabel = boidSizeLabel;
        }
    }

    @Getter
    public static class GeneralStatsLabelDto {

        private final Label frictionLabel;
        private final Label frameRateLabel;
        private final Label nrOfBoidsLabel;
        private final Label runTimeLabel;


        public GeneralStatsLabelDto(Label frictionLabel,
                                    Label frameRateLabel,
                                    Label nrOfBoidsLabel,
                                    Label runTimeLabel) {
            this.frictionLabel = frictionLabel;
            this.frameRateLabel = frameRateLabel;
            this.nrOfBoidsLabel = nrOfBoidsLabel;
            this.runTimeLabel = runTimeLabel;
        }
    }
}
