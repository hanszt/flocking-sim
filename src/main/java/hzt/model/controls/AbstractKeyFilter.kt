package hzt.model.controls

import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

abstract class AbstractKeyFilter internal constructor(
    negXKeyCode: KeyCode, posXKeycode: KeyCode,
    negYKeyCode: KeyCode, posYKeyCode: KeyCode
) {
    private val negXKeyCode: KeyCode
    private val posXKeycode: KeyCode
    private val negYKeyCode: KeyCode
    private val posYKeyCode: KeyCode
    private var xNegPressed = false
    private var xPosPressed = false
    private var yNegPressed = false
    private var yPosPressed = false

    var userInputSize = .1
    val keyPressed: EventHandler<KeyEvent>
    val keyReleased: EventHandler<KeyEvent>

    abstract fun pressedAction(point2D: Point2D?): Boolean
    abstract fun releasedAction(point2D: Point2D?): Boolean
    abstract fun allReleasedAction(allReleased: Boolean)

    fun resetKeyPressed() {
        yPosPressed = false
        yNegPressed = yPosPressed
        xPosPressed = yNegPressed
        xNegPressed = xPosPressed
    }

    private fun keyReleasedAction(e: KeyEvent) {
        if (e.code == posXKeycode) {
            xPosPressed = releasedAction(X_POS_DIR.multiply(userInputSize))
        }
        if (e.code == negXKeyCode) {
            xNegPressed = releasedAction(X_POS_DIR.multiply(-userInputSize))
        }
        if (e.code == posYKeyCode) {
            yPosPressed = releasedAction(Y_POS_DIR.multiply(userInputSize))
        }
        if (e.code == negYKeyCode) {
            yNegPressed = releasedAction(Y_POS_DIR.multiply(-userInputSize))
        }
        val allReleased = !xNegPressed && !xPosPressed && !yNegPressed && !yPosPressed
        allReleasedAction(allReleased)
    }

    private fun keyPressedAction(e: KeyEvent) {
        if (keyPressed(e, posXKeycode, xPosPressed)) {
            xPosPressed = pressedAction(X_POS_DIR.multiply(userInputSize))
        }
        if (keyPressed(e, negXKeyCode, xNegPressed)) {
            xNegPressed = pressedAction(X_POS_DIR.multiply(-userInputSize))
        }
        if (keyPressed(e, posYKeyCode, yPosPressed)) {
            yPosPressed = pressedAction(Y_POS_DIR.multiply(userInputSize))
        }
        if (keyPressed(e, negYKeyCode, yNegPressed)) {
            yNegPressed = pressedAction(Y_POS_DIR.multiply(-userInputSize))
        }
    }

    companion object {
        private val X_POS_DIR = Point2D(1.0, 0.0)
        private val Y_POS_DIR = Point2D(0.0, 1.0)
        private fun keyPressed(enteredKey: KeyEvent, comparedKey: KeyCode, isPressed: Boolean): Boolean {
            return enteredKey.code == comparedKey && !isPressed
        }
    }

    init {
        keyPressed = EventHandler { keyPressedAction(it) }
        keyReleased = EventHandler { keyReleasedAction(it) }
        this.negXKeyCode = negXKeyCode
        this.posXKeycode = posXKeycode
        this.negYKeyCode = negYKeyCode
        this.posYKeyCode = posYKeyCode
    }
}
