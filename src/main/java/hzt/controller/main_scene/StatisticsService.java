package hzt.controller.main_scene;

import hzt.model.entity.Ball2D;
import javafx.util.Duration;

public class StatisticsService {

    private final MainSceneController mc;

    public StatisticsService(MainSceneController mc) {
        this.mc = mc;
    }

    public void showStatisticsAboutSelectedBall(Ball2D selected) {
        double positionX, positionY, velocity, acceleration, ballSize;
        int ballsInPerceptionRadius;
        if (selected != null) {
            positionX = selected.getCenterPosition().getX();
            positionY = selected.getCenterPosition().getY();
            velocity = selected.getVelocity().magnitude();
            acceleration = selected.getAcceleration().magnitude();
            ballsInPerceptionRadius = selected.getPerceptionRadiusMap().size();
            ballSize = selected.getBody().getRadius() * 2;
        } else {
            positionX = positionY = velocity = acceleration = ballSize = Double.NaN;
            ballsInPerceptionRadius = 0;
        }
        //selected ball statistics
        mc.getBallNameLabel().setText(String.format("%s", selected != null ? selected.getName() : "No ball selected"));
        mc.getPositionStatsLabel().setText(String.format("x = %-4.2f p, y = %-4.2f p", positionX, positionY));
        mc.getVelocityStatsLabel().setText(String.format("%-4.2f p/s", velocity));
        mc.getAccelerationStatsLabel().setText(String.format("%-4.2f p/s^2", acceleration));
        mc.getNrOfBallsInPerceptionRadiusLabel().setText(String.format("%-3d", ballsInPerceptionRadius));
        mc.getBallSizeLabel().setText(String.format("%-4.2f p", ballSize));
    }

    public void showGlobalStatistics(double friction, Duration cycleDuration, int size, Duration runTimeSim) {
        mc.getFrictionStatsLabel().setText(String.format("%-1.3f", friction));
        mc.getFrameRateStatsLabel().setText(String.format("%-4.2f f/s", 1 / cycleDuration.toSeconds()));
        mc.getNumberOfBallsLabel().setText(String.format("%-3d", size));
        mc.getRunTimeLabel().setText(String.format("%-4.3f seconds", runTimeSim.toSeconds()));
    }
}
