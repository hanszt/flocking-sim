package hzt.model.entity;

import javafx.geometry.Point2D;
import org.apache.commons.math3.util.Precision;
import org.junit.jupiter.api.Test;

import static javafx.scene.paint.Color.BLACK;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class Ball2DTest {

    @Test
    void update() {
        //arrange

        //act

        //assert
        assertNull(null);
    }

    @Test
    void addComponentToAcceleration() {
        //arrange

        //act

        //assert
    }

    @Test
    void setSpeedBasedOnMouseDrag() {
        //arrange

        //act

        //assert
    }

    @Test
    void bounceOfEdges() {
        //arrange

        //act

        //assert
    }

    @Test
    void limitSpeedWhenMaxSpeedExceeded() {
        //arrange
        final int nrOftestCases = 10000, maxValue = 100, decimalPlaces = 10;
        double[] expectedSpeeds = new double[nrOftestCases], actualSpeeds = new double[nrOftestCases];
        for (int i = 0; i < nrOftestCases; i++) {
            Ball2D ball2D = new Ball2D(getRandomNumber(0, maxValue), BLACK);
            Point2D point2D = new Point2D(Math.random(), Math.random());
            double maxSpeed = getRandomNumber(0, maxValue);
            Point2D velocity = point2D.normalize().multiply(maxSpeed + getRandomNumber(0, maxValue));
            ball2D.setVelocity(velocity);
            //act
            ball2D.limit(maxSpeed, velocity);
            actualSpeeds[i] = Precision.round(ball2D.getVelocity().magnitude(), decimalPlaces);
            expectedSpeeds[i] = Precision.round(maxSpeed, decimalPlaces);
        }
        //assert: expected, actual
        assertArrayEquals(expectedSpeeds, actualSpeeds);
    }

    public double getRandomNumber(int min, int max) {
        return Math.random() * (max - min) + min;
    }
}
