package hzt.model.utils;

import hzt.model.entity.Boid;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.geometry.Point2D;

import java.util.Set;

public class Engine {

    public static final double DENSITY = 100; // kg/m^3

    private final FloatProperty pullFactor = new SimpleFloatProperty();
    private final FloatProperty repelFactor = new SimpleFloatProperty();

    /*
     * Fg = (G * m_self * m_other) / r^2
     * F_res = m_self * a ->
     * <p>
     * Fg = F_res
     * <p>
     * a = (G * m_other) / r^2
     */
    public abstract static class FlockingSim {

        public Point2D getTotalAcceleration(Boid self, Set<Boid> boidSet) {
            Point2D totalAcceleration = Point2D.ZERO;
            for (Boid other : boidSet) {
                Point2D acceleration = getAccelerationBetweenTwoBalls(self, other);
                totalAcceleration = totalAcceleration.add(acceleration);
            }
            return totalAcceleration;
        }

        abstract Point2D getAccelerationBetweenTwoBalls(Boid self, Boid other);

        @Override
        public abstract String toString();
    }

    private final FlockingSim type1 = new FlockingSim() {

        Point2D getAccelerationBetweenTwoBalls(Boid self, Boid other) {
            Point2D vectorSelfToOther = other.getCenterPosition().subtract(self.getCenterPosition());
            Point2D unitVectorInAccDir = vectorSelfToOther.normalize();
            float distance = (float) vectorSelfToOther.magnitude();
            float part2Formula = (float) other.getMass() / (distance * distance);
            float attractionMagnitude = pullFactor.get() * part2Formula;
            float repelMagnitude = repelFactor.get() * part2Formula;

            float repelDistance = self.getRepelRadius() + other.getRepelRadius();
            if (distance <= repelDistance) return unitVectorInAccDir.multiply(-repelMagnitude);
            else return unitVectorInAccDir.multiply(attractionMagnitude);
        }

        @Override
        public String toString() {
            return "Engine type 1";
        }
    };

    private final FlockingSim type2 = new FlockingSim() {

        Point2D getAccelerationBetweenTwoBalls(Boid self, Boid other) {
            Point2D vectorSelfToOther = other.getCenterPosition().subtract(self.getCenterPosition());
            Point2D unitVectorInAccDir = vectorSelfToOther.normalize();
            float distance = (float) vectorSelfToOther.magnitude();
            float part2Formula = (float) other.getMass() / (distance * distance);
            float repelDistance = self.getRepelRadius() + other.getRepelRadius();
            float curveFitConstant = (float) (other.getMass() * (pullFactor.get() + repelFactor.get()) / (repelDistance * repelDistance));
            float attractionMagnitude = pullFactor.get() * part2Formula;
            float repelMagnitude = -repelFactor.get() * part2Formula + curveFitConstant;

            if (distance <= repelDistance) return unitVectorInAccDir.multiply(repelMagnitude);
            else return unitVectorInAccDir.multiply(attractionMagnitude);
        }

        @Override
        public String toString() {
            return "Engine type 2";
        }
    };

    private final FlockingSim type3 = new FlockingSim() {

        Point2D getAccelerationBetweenTwoBalls(Boid self, Boid other) {
            final int multiplier = 10;
            Point2D vectorSelfToOther = other.getCenterPosition().subtract(self.getCenterPosition());
            Point2D unitVectorInAccDir = vectorSelfToOther.normalize();
            float distance = (float) vectorSelfToOther.magnitude();
            float repelDistance = self.getRepelRadius() + other.getRepelRadius();

            if (distance <= repelDistance) return unitVectorInAccDir.multiply(-repelFactor.get() * multiplier);
            else return unitVectorInAccDir.multiply(pullFactor.get() * multiplier);
        }

        @Override
        public String toString() {
            return "Engine type 3";
        }
    };

    public FloatProperty pullFactorProperty() {
        return pullFactor;
    }

    public FloatProperty repelFactorProperty() {
        return repelFactor;
    }

    public FlockingSim getType1() {
        return type1;
    }

    public FlockingSim getType2() {
        return type2;
    }

    public FlockingSim getType3() {
        return type3;
    }


}
