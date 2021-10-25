package hzt.model.entity.boid;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

public class RectangleBoid extends Boid {

    private final double diagonal;

    public RectangleBoid(double width, double height, Paint paint) {
        super("Rectangle Boid ", new Rectangle(width, height), paint);
        super.getBody().getTransforms().add(new Translate(-width / 2,-height / 2));
        this.diagonal = Math.sqrt(width * width + height * height);
    }

    @Override
    public double getDistanceFromCenterToOuterEdge() {
        return diagonal / 2;
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
