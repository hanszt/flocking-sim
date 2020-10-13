package hzt.controller.utils;

import hzt.model.entity.Ball2D;
import javafx.geometry.Point2D;

import java.util.Set;

public class PhysicsEngine {

    public static final double DENSITY = 100; // kg/m^3

    private static float pullFactor;
    private static float repelFactor;
    private static float repelDistanceFactor;
    /*
     * Fg = (G * m_self * m_other) / r^2
     * F_res = m_self * a ->
     * <p>
     * Fg = F_res
     * <p>
     * a = (G * m_other) / r^2
     */
    public static Point2D getAccProfile1(Ball2D self, Set<Ball2D> ballsInPerceptionRadius) {
        Point2D totalAcceleration = Point2D.ZERO;
        for (Ball2D other : ballsInPerceptionRadius) {
            Point2D acceleration = getAccelerationProfile1(self, other);
            totalAcceleration = totalAcceleration.add(acceleration);
        }
        return totalAcceleration;
    }
    private static Point2D getAccelerationProfile1(Ball2D self, Ball2D other) {
        Point2D vectorSelfToOther = other.getCenterPosition().subtract(self.getCenterPosition());
        Point2D unitVectorInAccDir = vectorSelfToOther.normalize();
        float distance = (float) vectorSelfToOther.magnitude();
        float part2Formula = (float) other.getMass() / (distance * distance);
        float threshHold = (float) (self.getBody().getRadius() + other.getBody().getRadius()) * repelDistanceFactor;
        float attractionMagnitude = pullFactor * part2Formula;
        float repelMagnitude = repelFactor * part2Formula;

        Point2D acceleration;
        if (distance <= threshHold) acceleration = unitVectorInAccDir.multiply(-repelMagnitude);
        else acceleration = unitVectorInAccDir.multiply(attractionMagnitude);
        return acceleration;
    }

    public static Point2D getAccProfile2(Ball2D self, Set<Ball2D> ballsInPerceptionRadius) {
        Point2D totalAcceleration = Point2D.ZERO;
        for (Ball2D other : ballsInPerceptionRadius) {
            Point2D acceleration = getAccelerationProfile2(self, other);
            totalAcceleration = totalAcceleration.add(acceleration);
        }
        return totalAcceleration;
    }

    private static Point2D getAccelerationProfile2(Ball2D self, Ball2D other) {
        Point2D vectorSelfToOther = other.getCenterPosition().subtract(self.getCenterPosition());
        Point2D unitVectorInAccDir = vectorSelfToOther.normalize();
        float distance = (float) vectorSelfToOther.magnitude();
        float part2Formula = (float) other.getMass() / (distance * distance);
        float threshHold = (float) ((self.getBody().getRadius() + other.getBody().getRadius()) * repelDistanceFactor);
        float curveFitConstant = (float) (other.getMass() * (pullFactor + repelFactor) / (threshHold * threshHold));
        float attractionMagnitude = pullFactor * part2Formula;
        float repelMagnitude = -repelFactor * part2Formula + curveFitConstant;
        Point2D acceleration;
        if (distance <= threshHold) acceleration = unitVectorInAccDir.multiply(repelMagnitude);
        else acceleration = unitVectorInAccDir.multiply(attractionMagnitude);
        return acceleration;
    }

    public static void setPullFactor(double pullFactor) {
        PhysicsEngine.pullFactor = (float) pullFactor;
    }

    public static void setRepelFactor(double repelFactor) {
        PhysicsEngine.repelFactor = (float) repelFactor;
    }

    public static void setRepelDistanceFactor(double repelDistanceFactor) {
        PhysicsEngine.repelDistanceFactor = (float) repelDistanceFactor;
    }

}
