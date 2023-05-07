package hzt.model.utils

import javafx.geometry.Point2D
import javafx.scene.paint.Color

object RandomGenerator {
    @JvmStatic
    fun getRandomDouble(min: Double, max: Double): Double {
        return Math.random() * (max - min) + min
    }

    @JvmStatic
    fun getRandomPositionOnParent(parentWidth: Double, parentHeight: Double): Point2D {
        val xPos = getRandomDouble(0.0, parentWidth)
        val yPos = getRandomDouble(0.0, parentHeight)
        return Point2D(xPos, yPos)
    }

    @JvmStatic
    val randomColor: Color
        get() = Color.color(Math.random(), Math.random(), Math.random())
}
