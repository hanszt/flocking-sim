package hzt.model.entity.boid

import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Translate
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

class RectangleBoid(width: Double, height: Double, paint: Paint) :
    Boid("Rectangle Boid ", Rectangle(width, height), paint) {

    private val diagonal: Double

    override val distanceFromCenterToOuterEdge: Double
        get() = diagonal / 2

    override val mass: Double
        get() = massByDensityAndRadius

    private val massByDensityAndRadius: Double
        get() {
            val volume = (4 / 3) * PI * distanceFromCenterToOuterEdge.pow(3.0)
            return getDensityMaterial() * volume
        }

    init {
        super.body.transforms.add(Translate(-width / 2, -height / 2))
        diagonal = sqrt(width * width + height * height)
    }
}
