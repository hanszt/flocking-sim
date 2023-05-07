package hzt.model.entity.boid;

import javafx.geometry.Point2D;
import org.apache.commons.math3.util.Precision;
import org.junit.jupiter.api.Test;

import static javafx.scene.paint.Color.BLACK;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class Ball2DTest {

    @Test
    void limitSpeedWhenMaxSpeedExceeded() {
        //arrange
        final int nrOftestCases = 10000, maxValue = 100, decimalPlaces = 10;
        double[] expectedSpeeds = new double[nrOftestCases], actualSpeeds = new double[nrOftestCases];
        for (int caseNr = 0; caseNr < nrOftestCases; caseNr++) {
            Boid ball2D = new CircleBoid(getRandomNumber(0, maxValue), BLACK);
            Point2D point2D = new Point2D(Math.random(), Math.random());
            double maxSpeed = getRandomNumber(0, maxValue);
            Point2D velocity = point2D.normalize().multiply(maxSpeed + getRandomNumber(0, maxValue));
            //act
            ball2D.limit(maxSpeed, velocity);
            actualSpeeds[caseNr] = Precision.round(ball2D.getVelocity().magnitude(), decimalPlaces);
            expectedSpeeds[caseNr] = Precision.round(maxSpeed, decimalPlaces);
        }
        assertArrayEquals(expectedSpeeds, actualSpeeds);
    }

    @SuppressWarnings("SameParameterValue")
    private static double getRandomNumber(int min, int max) {
        return Math.random() * (max - min) + min;
    }
}
