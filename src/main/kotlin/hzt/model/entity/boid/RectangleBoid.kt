package hzt.model.entity.boid

import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Translate
import kotlin.math.sqrt

class RectangleBoid(width: Double, height: Double, paint: Paint) :
    Boid("Rectangle Boid ", Rectangle(width, height), paint) {

    private val diagonal: Double
    private val width: Double
    private val height: Double

    override val distanceFromCenterToOuterEdge: Double
        get() = diagonal / 2

    override val mass: Double
        get() = massByDensityAndDimensions

    private val massByDensityAndDimensions: Double
        get() = densityMaterial() * (width * height)

    init {
        super.body.transforms.add(Translate(-width / 2, -height / 2))
        this.width = width
        this.height = height
        diagonal = sqrt(width * width + height * height)
    }
}
