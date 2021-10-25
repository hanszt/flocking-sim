package hzt.model.controls

import kotlin.jvm.JvmOverloads
import javafx.scene.input.KeyCode
import hzt.model.controls.AbstractKeyFilter
import javafx.geometry.Point2D

class TranslationKeyFilter @JvmOverloads constructor(
    left: KeyCode? = KeyCode.A, right: KeyCode? = KeyCode.D,
    up: KeyCode? = KeyCode.W, down: KeyCode? = KeyCode.S
) : AbstractKeyFilter(
    left!!, right!!, up!!, down!!
) {
    var userInputAcceleration = Point2D.ZERO
        private set

    override fun pressedAction(point2D: Point2D?): Boolean {
        userInputAcceleration = userInputAcceleration.add(point2D)
        return true
    }

    override fun releasedAction(point2D: Point2D?): Boolean {
        userInputAcceleration = userInputAcceleration.subtract(point2D)
        return false
    }

    override fun allReleasedAction(allReleased: Boolean) {
        if (allReleased) {
            userInputAcceleration = Point2D.ZERO
        }
    }
}
