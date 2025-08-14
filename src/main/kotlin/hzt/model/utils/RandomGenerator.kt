package hzt.model.utils

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import kotlin.random.Random

class RandomGenerator(private val random: Random) {

    fun randomPosition(width: Double, height: Double): Point2D {
        val xPos = random.nextDouble(0.0, width)
        val yPos = random.nextDouble(0.0, height)
        return Point2D(xPos, yPos)
    }

    val randomColor: Color
        get() = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble())
}