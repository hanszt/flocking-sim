package hzt.controller.utils;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class RandomGenerator {

    public static double getRandomDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public static Point2D getRandomPositionOnParent(double parentWidth, double parentHeight) {
        double xPos = getRandomDouble(0, parentWidth);
        double yPos = getRandomDouble(0, parentHeight);
        return new Point2D(xPos, yPos);
    }

    public static Color getRandomColor() {
        return javafx.scene.paint.Color.color(Math.random(), Math.random(), Math.random());
    }

}
