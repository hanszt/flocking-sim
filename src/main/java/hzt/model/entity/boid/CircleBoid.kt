package hzt.model.entity.boid

import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import kotlin.math.pow

class CircleBoid(radius: Double, paint: Paint) : Boid("Circle Boid ", Circle(radius), paint) {
    override val distanceFromCenterToOuterEdge: Double
        get() = (body as Circle).radius
    override val mass: Double
        get() = massByDensityAndRadius
    private val massByDensityAndRadius: Double
        get() {
            val volume = 4 * Math.PI * distanceFromCenterToOuterEdge.pow(3.0) / 3
            return getDensityMaterial() * volume
        }
}
