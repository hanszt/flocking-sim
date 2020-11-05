package hzt.controller.main_scene;

import hzt.model.entity.Boid;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.util.Duration;

public class StatisticsService {

    private final MainSceneController mc;

    public StatisticsService(MainSceneController mc) {
        this.mc = mc;
    }

    public void showStatisticsAboutSelectedBall(Boid selected) {
        if (selected != null) {
            Point2D centerPos = selected.getCenterPosition();
            mc.getBallNameLabel().setText(String.format("%s", selected.getName()));
            mc.getPositionStatsLabel().setText(String.format("x = %-4.2f p, y = %-4.2f p", centerPos.getX(), centerPos.getY()));
            mc.getVelocityStatsLabel().setText(String.format("%-4.2f p/s", selected.getVelocity().magnitude()));
            mc.getAccelerationStatsLabel().setText(String.format("%-4.2f p/s^2", selected.getAcceleration().magnitude()));
            mc.getNrOfBallsInPerceptionRadiusLabel().setText(String.format("%-3d", selected.getPerceptionRadiusMap().size()));
            mc.getBallSizeLabel().setText(String.format("%-4.2f p", selected.getBody().getRadius() * 2));
        }
    }

    public void showGlobalStatistics(double friction, Duration cycleDuration, int size, Duration runTimeSim) {
        mc.getFrictionStatsLabel().setText(String.format("%-1.3f", friction));
        mc.getFrameRateStatsLabel().setText(String.format("%-4.2f f/s", 1 / cycleDuration.toSeconds()));
        mc.getNumberOfBallsLabel().setText(String.format("%-3d", size));
        mc.getRunTimeLabel().setText(String.format("%-4.3f seconds", runTimeSim.toSeconds()));
    }
}
