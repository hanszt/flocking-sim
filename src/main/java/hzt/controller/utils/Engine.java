package hzt.controller.utils;

import hzt.model.entity.Ball2D;
import javafx.geometry.Point2D;

import java.util.Set;

public class Engine {

    public static final double DENSITY = 100; // kg/m^3

    private float pullFactor;
    private float repelFactor;

    /*
     * Fg = (G * m_self * m_other) / r^2
     * F_res = m_self * a ->
     * <p>
     * Fg = F_res
     * <p>
     * a = (G * m_other) / r^2
     */
    public static abstract class FlockingSim {

        public Point2D getTotalAcceleration(Ball2D self, Set<Ball2D> ball2DSet) {
            Point2D totalAcceleration = Point2D.ZERO;
            for (Ball2D other : ball2DSet) {
                Point2D acceleration = getAccelerationBetweenTwoBalls(self, other);
                totalAcceleration = totalAcceleration.add(acceleration);
            }
            return totalAcceleration;
        }

        abstract Point2D getAccelerationBetweenTwoBalls(Ball2D self, Ball2D other);

        public abstract String getName();
    }

    private final FlockingSim type1 = new FlockingSim() {

        Point2D getAccelerationBetweenTwoBalls(Ball2D self, Ball2D other) {
            Point2D vectorSelfToOther = other.getCenterPosition().subtract(self.getCenterPosition());
            Point2D unitVectorInAccDir = vectorSelfToOther.normalize();
            float distance = (float) vectorSelfToOther.magnitude();
            float part2Formula = (float) other.getMass() / (distance * distance);
            float attractionMagnitude = pullFactor * part2Formula;
            float repelMagnitude = repelFactor * part2Formula;

            Point2D acceleration;
            float repelDistance = self.getRepelRadius() + other.getRepelRadius();
            if (distance <= repelDistance) acceleration = unitVectorInAccDir.multiply(-repelMagnitude);
            else acceleration = unitVectorInAccDir.multiply(attractionMagnitude);
            return acceleration;
        }

        @Override
        public String getName() {
            return "Engine type 1";
        }
    };

    private final FlockingSim type2 = new FlockingSim() {

        Point2D getAccelerationBetweenTwoBalls(Ball2D self, Ball2D other) {
            Point2D vectorSelfToOther = other.getCenterPosition().subtract(self.getCenterPosition());
            Point2D unitVectorInAccDir = vectorSelfToOther.normalize();
            float distance = (float) vectorSelfToOther.magnitude();
            float part2Formula = (float) other.getMass() / (distance * distance);
            float repelDistance = self.getRepelRadius() + other.getRepelRadius();
            float curveFitConstant = (float) (other.getMass() * (pullFactor + repelFactor) / (repelDistance * repelDistance));
            float attractionMagnitude = pullFactor * part2Formula;
            float repelMagnitude = -repelFactor * part2Formula + curveFitConstant;
            Point2D acceleration;
            if (distance <= repelDistance) acceleration = unitVectorInAccDir.multiply(repelMagnitude);
            else acceleration = unitVectorInAccDir.multiply(attractionMagnitude);
            return acceleration;
        }

        @Override
        public String getName() {
            return "Engine type 2";
        }
    };

    public FlockingSim getType1() {
        return type1;
    }

    public FlockingSim getType2() {
        return type2;
    }

    public void setPullFactor(double pullFactor) {
        this.pullFactor = (float) pullFactor;
    }

    public void setRepelFactor(double repelFactor) {
        this.repelFactor = (float) repelFactor;
    }

}
