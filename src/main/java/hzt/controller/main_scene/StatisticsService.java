package hzt.controller.main_scene;

import hzt.model.entity.Ball2D;
import javafx.util.Duration;


public class StatisticsService {

    private final MainSceneController mc;

    public StatisticsService(MainSceneController mc) {
        this.mc = mc;
    }

    public void addStatisticsAboutSelectedBall(Ball2D selected) {
        double positionX, positionY, velocityX, velocityY, accelerationX, accelerationY;
        int ballsInPerceptionRadius;
        if (selected != null) {
            positionX = selected.getCenterPosition().getX();
            velocityX = selected.getVelocity().getX();
            accelerationX = selected.getAcceleration().getX();
            positionY = selected.getCenterPosition().getY();
            velocityY = selected.getVelocity().getY();
            accelerationY = selected.getAcceleration().getY();
            ballsInPerceptionRadius = selected.getBallsInPerceptionRadiusMap().size();
        } else {
            positionX = positionY = velocityX = velocityY = accelerationX = accelerationY = Double.NaN;
            ballsInPerceptionRadius = 0;
        }
        //selected ball statistics
        mc.getBallNameLabel().setText(String.format("%s", selected != null ? selected.getName() : "No ball selected"));
        mc.getPositionStatsLabel().setText(String.format("x = %-4.2f p, x = %-4.2f p",
                positionX, positionY));
        mc.getVelocityStatsLabel().setText(String.format("x = %-4.2f p/s, x = %-4.2f p/s",
                velocityX, velocityY));
        mc.getAccelerationStatsLabel().setText(String.format("x = %-4.2f p/s^2, x = %-4.2f p/s^2",
                accelerationX, accelerationY));
        mc.getNrOfBallsInPerceptionRadiusLabel().setText(String.format("%-3d", ballsInPerceptionRadius));
    }

    public void addGlobalStatistics(double friction, Duration cycleDuration, int size) {
        mc.getFrictionStatsLabel().setText(String.format("%-1.2f", friction));
        mc.getFrameRateStatsLabel().setText(String.format("%-4.2f f/s", 1 / cycleDuration.toSeconds()));
        mc.getNumberOfBallsLabel().setText(String.format("%-3d", size));
    }
}
