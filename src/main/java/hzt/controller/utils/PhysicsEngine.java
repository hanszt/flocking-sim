package hzt.controller.utils;

import hzt.model.entity.Ball2D;
import javafx.geometry.Point2D;

import java.util.Set;

public class PhysicsEngine {

    private static final double G_EARTH = 9.81; // m/s^2
    private static final double GRAVITATION_CONSTANT = 6.67e-11; //m^3/(kg * s^2)

    public static final double DENSITY_IRON = 7.870; // kg/m^3

    private static double gravity = GRAVITATION_CONSTANT;

    /*
     * Fg = (G * m_self * m_other) / r^2
     * F_res = m_self * a ->
     * <p>
     * Fg = F_res
     * <p>
     * a = (G * m_other) / r^2
     */

    private static Point2D getAccelerationCausedByBallInPerceptionRadius(Ball2D self, Ball2D other) {
        Point2D dirAcceleration = other.getCenterPosition().subtract(self.getCenterPosition()).normalize();
        double distance = self.getCenterPosition().subtract(other.getCenterPosition()).magnitude();
        double accelerationMagnitude = gravity * other.getMass() / (distance * distance);
        return dirAcceleration.multiply(accelerationMagnitude);

    }

    public static Point2D getTotalAccelerationByMassAndOtherBallsInPerceptionRadius(Ball2D self, Set<Ball2D> ballsInPerceptionRadius) {
        Point2D totalAcceleration = Point2D.ZERO;
        for (Ball2D other : ballsInPerceptionRadius) {
            Point2D acceleration = getAccelerationCausedByBallInPerceptionRadius(self, other);
            totalAcceleration = totalAcceleration.add(acceleration);
        }
        return totalAcceleration;
    }

    /*
     *
     *
     *
     */

    public static void setGravity(double gravity) {
        PhysicsEngine.gravity = gravity;
    }
}
