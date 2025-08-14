package hzt.model.entity

import hzt.model.PropertyLoader.parsedIntAppProp
import hzt.model.FlockProperties
import hzt.model.entity.boid.Boid
import hzt.model.entity.boid.CircleBoid
import hzt.model.entity.boid.RectangleBoid
import hzt.model.utils.Engine.FlockingSim
import hzt.model.utils.RandomGenerator
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Slider
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Flock(val mainScene: Scene, random: kotlin.random.Random) : Group(), Iterable<Boid> {

    private val randomGenerator = RandomGenerator(random)
    val randomRectangleFlock: FlockType = object : FlockType() {
        override val random: Boolean
            get() = true

        override fun createBoid(maxBoidSize: Double): Boid {
            return RectangleBoid(
                random.nextDouble(MIN_SIZE.toDouble(), maxBoidSize),
                random.nextDouble(MIN_SIZE.toDouble(), maxBoidSize), randomGenerator.randomColor
            )
        }

        override fun setCenterPosition(boid: Boid, dimension: Dimension2D) {
            boid.setBodyTranslate(randomGenerator.randomPosition(dimension.width, dimension.height))
        }

        override fun toString(): String = "Random rectangle flock"
    }

    val uniformCircleFlock: FlockType = object : FlockType() {
        override val random: Boolean
            get() = false

        override fun createBoid(maxBoidSize: Double): Boid = CircleBoid(maxBoidSize, uniformBallColor)

        override fun setCenterPosition(boid: Boid, dimension: Dimension2D) {
            boid.setBodyTranslate(randomGenerator.randomPosition(dimension.width, dimension.height))
        }

        override fun toString(): String = "Uniform circle flock"
    }

    val randomCircleFlock: FlockType = object : FlockType() {
        override val random: Boolean
            get() = true

        override fun createBoid(maxBoidSize: Double): Boid =
            CircleBoid(random.nextDouble(MIN_SIZE.toDouble(), maxBoidSize), randomGenerator.randomColor)

        override fun setCenterPosition(boid: Boid, dimension: Dimension2D) =
            boid.setBodyTranslate(randomGenerator.randomPosition(dimension.width, dimension.height))

        override fun toString(): String = "Random circle flock"
    }

    val flockProperties = FlockProperties()
    var selectedBoid: Boid? = null
    private var uniformBallColor = INIT_UNIFORM_BALL_COLOR
    var selectedBallColor: Color = INIT_SELECTED_BALL_COLOR
    var flockType: FlockType = randomCircleFlock
    var flockingSim: FlockingSim? = null

    fun controlFlockSize(numberOfBalls: Int, parentDimension: Dimension2D) {
        while (children.size != numberOfBalls) {
            if (children.size < numberOfBalls) addBoidToFlock(parentDimension) else removeBoidFromFlock()
        }
    }

    private fun addBoidToFlock(parentDimension: Dimension2D): Boid = flockType
        .createBoid(flockProperties.getMaxBoidSize()).also {
            flockType.setCenterPosition(it, parentDimension)
            it.setPerceptionRadius(it.distanceFromCenterToOuterEdge * flockProperties.getPerceptionRadiusRatio())
            it.setRepelRadius(it.distanceFromCenterToOuterEdge * flockProperties.getRepelRadiusRatio())
            it.addMouseFunctionality()
            children.add(it)
            it.setVisibilityBoidComponents(flockProperties)
        }

    private fun removeBoidFromFlock() {
        val boid = children[0] as Boid
        children.remove(boid)
        for (node in children) {
            val other = node as Boid
            other.perceptionRadiusMap.remove(boid)
            other.children.removeIf(Connection::class.java::isInstance)
        }
        if (boid == selectedBoid) {
            selectedBoid = if (children.isNotEmpty()) randomSelectedBoid else null
        }
    }

    fun addBoidToFlockAtMouseTip(mouseEvent: MouseEvent, dimension2D: Dimension2D, numberOfBoidsSlider: Slider) {
        val numberOfBoids = children.size
        val isMiddleOrSecondary = mouseEvent.isMiddleButtonDown || mouseEvent.isSecondaryButtonDown
        if (isMiddleOrSecondary) {
            if (numberOfBoids < maxNrOfBoids) {
                numberOfBoidsSlider.value = numberOfBoids.toDouble()
                val boid = addBoidToFlock(dimension2D)
                boid.setBodyTranslate(Point2D(mouseEvent.x, mouseEvent.y))
            } else {
                removeBoidFromFlock()
                val boid = addBoidToFlock(dimension2D)
                boid.setBodyTranslate(Point2D(mouseEvent.x, mouseEvent.y))
            }
        }
    }

    val maxNrOfBoids: Int
        get() = parsedIntAppProp("max_number_of_boids", 200)

    val randomSelectedBoid: Boid
        get() = (children[Random().nextInt(children.size)] as Boid).apply {
            updatePaint(selectedBallColor)
            addKeyControlForAcceleration()
            toFront()
            updateSelectedBoidComponentsVisibility(this)
        }

    override fun iterator(): Iterator<Boid> = children.asSequence()
        .filterIsInstance<Boid>()
        .iterator()

    fun updateSelectedBoidComponentsVisibility(selectedBoid: Boid) {
        selectedBoid.perceptionCircle.isVisible = flockProperties.isSelectedPerceptionCircleVisible()
        selectedBoid.path.isVisible = flockProperties.isSelectedPathVisible()
    }

    fun updateBoidComponentsVisibility(boid: Boid) {
        boid.perceptionCircle.isVisible = flockProperties.isPerceptionCircleVisible()
        boid.path.isVisible = flockProperties.isAllPathsVisible()
    }

    abstract class FlockType {
        abstract val random: Boolean
        abstract fun createBoid(maxBoidSize: Double): Boid
        abstract fun setCenterPosition(boid: Boid, dimension: Dimension2D)
        override fun toString(): String = "FlockType"
    }

    inner class CircleFlock : FlockType() {
        override val random: Boolean = false
        override fun createBoid(maxBoidSize: Double): Boid = CircleBoid(maxBoidSize, uniformBallColor)

        override fun setCenterPosition(boid: Boid, dimension: Dimension2D) {
            val index: Int = Boid.next % maxNrOfBoids
            boid.setBodyTranslate(getCirclePositionOnParent(dimension.width, dimension.height, index))
        }

        private fun getCirclePositionOnParent(width: Double, height: Double, index: Int): Point2D {
            val centerPosition = Point2D(width / 2, height / 2)
            val positionMultiplier = (width + height) / 8
            val circularPosition = Point2D(
                positionMultiplier * cos(2 * index * PI / maxNrOfBoids),
                positionMultiplier * sin(2 * index * PI / maxNrOfBoids)
            )
            return circularPosition.add(centerPosition)
        }

        override fun toString(): String = "Uniform ordered circle flock"
    }

    fun setUniformBallColor(uniformBallColor: Color) {
        this.uniformBallColor = uniformBallColor
    }

    companion object {
        val INIT_UNIFORM_BALL_COLOR: Color = Color.ORANGE
        val INIT_SELECTED_BALL_COLOR: Color = Color.RED
        const val MIN_SIZE = 3
    }
}
