package hzt.model.controls;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public abstract class AbstractKeyFilter {

    private static final Point2D X_POS_DIR = new Point2D(1, 0);
    private static final Point2D Y_POS_DIR = new Point2D(0, 1);

    private final KeyCode negXKeyCode;
    private final KeyCode posXKeycode;
    private final KeyCode negYKeyCode;
    private final KeyCode posYKeyCode;

    private boolean xNegPressed;
    private boolean xPosPressed;
    private boolean yNegPressed;
    private boolean yPosPressed;

    private double userInputSize = .1;

    private final EventHandler<KeyEvent> keyPressed;
    private final EventHandler<KeyEvent> keyReleased;


    AbstractKeyFilter(
            KeyCode negXKeyCode, KeyCode posXKeycode,
            KeyCode negYKeyCode, KeyCode posYKeyCode) {
        this.keyPressed = this::keyPressedAction;
        this.keyReleased = this::keyReleasedAction;
        this.negXKeyCode = negXKeyCode;
        this.posXKeycode = posXKeycode;
        this.negYKeyCode = negYKeyCode;
        this.posYKeyCode = posYKeyCode;
    }

    private static boolean keyPressed(KeyEvent enteredKey, KeyCode comparedKey, boolean isPressed) {
        return enteredKey.getCode() == comparedKey && !isPressed;
    }

    abstract boolean pressedAction(Point2D point2D);

    abstract boolean releasedAction(Point2D point2D);

    abstract void allReleasedAction(boolean allReleased);

    public void resetKeyPressed() {
        xNegPressed = xPosPressed = yNegPressed = yPosPressed = false;
    }

    public double getUserInputSize() {
        return userInputSize;
    }

    public void setUserInputSize(double userInputSize) {
        this.userInputSize = userInputSize;
    }

    public EventHandler<KeyEvent> getKeyPressed() {
        return keyPressed;
    }

    public EventHandler<KeyEvent> getKeyReleased() {
        return keyReleased;
    }

    private void keyReleasedAction(KeyEvent e) {
        if (e.getCode() == posXKeycode) {
            xPosPressed = releasedAction(X_POS_DIR.multiply(userInputSize));
        }
        if (e.getCode() == negXKeyCode) {
            xNegPressed = releasedAction(X_POS_DIR.multiply(-userInputSize));
        }
        if (e.getCode() == posYKeyCode) {
            yPosPressed = releasedAction(Y_POS_DIR.multiply(userInputSize));
        }
        if (e.getCode() == negYKeyCode) {
            yNegPressed = releasedAction(Y_POS_DIR.multiply(-userInputSize));
        }
        boolean allReleased = !xNegPressed && !xPosPressed && !yNegPressed && !yPosPressed;
        allReleasedAction(allReleased);
    }

    private void keyPressedAction(KeyEvent e) {
        if (keyPressed(e, posXKeycode, xPosPressed)) {
            xPosPressed = pressedAction(X_POS_DIR.multiply(userInputSize));
        }
        if (keyPressed(e, negXKeyCode, xNegPressed)) {
            xNegPressed = pressedAction(X_POS_DIR.multiply(-userInputSize));
        }
        if (keyPressed(e, posYKeyCode, yPosPressed)) {
            yPosPressed = pressedAction(Y_POS_DIR.multiply(userInputSize));
        }
        if (keyPressed(e, negYKeyCode, yNegPressed)) {
            yNegPressed = pressedAction(Y_POS_DIR.multiply(-userInputSize));
        }
    }
}
