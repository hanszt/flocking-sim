package hzt.model.controls;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

import static javafx.scene.input.KeyCode.*;

public class TranslationKeyFilter extends AbstractKeyFilter {

    private Point2D userInputAcceleration = Point2D.ZERO;

    public TranslationKeyFilter() {
        this(A, D, W, S);
    }

    public TranslationKeyFilter(KeyCode left, KeyCode right,
                                KeyCode up, KeyCode down) {
        super(left, right, up, down);
    }
    boolean pressedAction(Point2D point2D) {
        userInputAcceleration = userInputAcceleration.add(point2D);
        return true;
    }

    boolean releasedAction(Point2D point2D) {
        userInputAcceleration = userInputAcceleration.subtract(point2D);
        return false;
    }

    void allReleasedAction(boolean allReleased) {
        if (allReleased) {
            userInputAcceleration = Point2D.ZERO;
        }
    }

    public Point2D getUserInputAcceleration() {
        return userInputAcceleration;
    }

}
