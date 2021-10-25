package hzt.model.entity.boid;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class CircleBoid extends Boid {

    public CircleBoid(double radius, Paint paint) {
        super("Circle Boid ", new Circle(radius), paint);
    }

    @Override
    public double getDistanceFromCenterToOuterEdge() {
        return ((Circle) getBody()).getRadius();
    }

    @Override
    public double getMass() {
        return getMassByDensityAndRadius();
    }

    private double getMassByDensityAndRadius() {
        double volume = 4 * Math.PI * Math.pow(getDistanceFromCenterToOuterEdge(), 3) / 3;
        return getDensityMaterial() * volume;
    }
}
