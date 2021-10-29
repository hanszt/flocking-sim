package hzt.model.entity

import hzt.model.AppConstants
import hzt.model.AppConstants.parsedIntAppProp
import hzt.model.FlockProperties
import hzt.model.entity.boid.Boid
import hzt.model.entity.boid.CircleBoid
import hzt.model.entity.boid.RectangleBoid
import hzt.model.utils.Engine.FlockingSim
import hzt.model.utils.RandomGenerator.getRandomDouble
import hzt.model.utils.RandomGenerator.getRandomPositionOnParent
import hzt.model.utils.RandomGenerator.randomColor
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Slider
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Flock(val mainScene: Scene) : Group(), Iterable<Boid?> {

    val randomRectangleFlock: FlockType = object : FlockType() {
        override fun createBoid(maxBoidSize: Double): Boid {
            return RectangleBoid(
                getRandomDouble(AppConstants.MIN_SIZE.toDouble(), maxBoidSize),
                getRandomDouble(AppConstants.MIN_SIZE.toDouble(), maxBoidSize), randomColor
            )
        }

        override fun setCenterPosition(boid: Boid, dimension: Dimension2D) {
            boid.setBodyTranslate(getRandomPositionOnParent(dimension.width, dimension.height))
        }

        override fun toString(): String {
            return "Random rectangle flock"
        }
    }

    val uniformCircleFlock: FlockType = object : FlockType() {
        override fun createBoid(maxBoidSize: Double): Boid {
            return CircleBoid(maxBoidSize, uniformBallColor)
        }

        override fun setCenterPosition(boid: Boid, dimension: Dimension2D) {
            boid.setBodyTranslate(getRandomPositionOnParent(dimension.width, dimension.height))
        }

        override fun toString(): String {
            return "Uniform circle flock"
        }
    }

    val randomCircleFlock: FlockType = object : FlockType() {
        override fun createBoid(maxBoidSize: Double): Boid {
            return CircleBoid(getRandomDouble(AppConstants.MIN_SIZE.toDouble(), maxBoidSize), randomColor)
        }

        override fun setCenterPosition(boid: Boid, dimension: Dimension2D) {
            boid.setBodyTranslate(getRandomPositionOnParent(dimension.width, dimension.height))
        }

        override fun toString(): String {
            return "Random circle flock"
        }
    }
    val flockProperties = FlockProperties()
    var selectedBoid: Boid? = null
    private var uniformBallColor = AppConstants.INIT_UNIFORM_BALL_COLOR
    var selectedBallColor: Color = AppConstants.INIT_SELECTED_BALL_COLOR
    var flockType: FlockType? = null
    var flockingSim: FlockingSim? = null

    fun controlFlockSize(numberOfBalls: Int, parentDimension: Dimension2D) {
        while (children.size != numberOfBalls) {
            if (children.size < numberOfBalls) {
                addBoidToFlock(parentDimension)
            } else {
                removeBoidFromFLock()
            }
        }
    }

    private fun addBoidToFlock(parentDimension: Dimension2D): Boid {
        val boid = flockType!!.createBoid(flockProperties.getMaxBoidSize())
        flockType!!.setCenterPosition(boid, parentDimension)
        boid.setPerceptionRadius(boid.distanceFromCenterToOuterEdge * flockProperties.getPerceptionRadiusRatio())
        boid.setRepelRadius(boid.distanceFromCenterToOuterEdge * flockProperties.getRepelRadiusRatio())
        boid.addMouseFunctionality()
        children.add(boid)
        boid.setVisibilityBoidComponents(flockProperties)
        return boid
    }

    private fun removeBoidFromFLock() {
        val list = children
        val boid = list[0] as Boid
        children.remove(boid)
        for (node in children) {
            val other = node as Boid
            other.perceptionRadiusMap.remove(boid)
            other.children.removeIf { obj: Node? -> Connection::class.java.isInstance(obj) }
        }
        if (boid == selectedBoid) {
            selectedBoid = if (!list.isEmpty()) randomSelectedBoid else null
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
                removeBoidFromFLock()
                val boid = addBoidToFlock(dimension2D)
                boid.setBodyTranslate(Point2D(mouseEvent.x, mouseEvent.y))
            }
        }
    }

    val maxNrOfBoids: Int
    get() {
        return parsedIntAppProp("max_number_of_boids", 200)
    }

    val randomSelectedBoid: Boid
        get() {
            val boid = children[Random().nextInt(children.size)] as Boid
            boid.updatePaint(selectedBallColor)
            boid.addKeyControlForAcceleration()
            boid.toFront()
            updateSelectedBoidComponentsVisibility(boid)
            return boid
        }

    override fun iterator(): MutableIterator<Boid> {
        return children.stream()
            .filter { obj: Node? -> Boid::class.java.isInstance(obj) }
            .map { obj: Node? -> Boid::class.java.cast(obj) }
            .iterator()
    }

    fun updateSelectedBoidComponentsVisibility(selectedBoid: Boid) {
        selectedBoid.perceptionCircle.isVisible = flockProperties.isSelectedPerceptionCircleVisible()
        selectedBoid.path.isVisible = flockProperties.isSelectedPathVisible()
    }

    fun updateBoidComponentsVisibility(boid: Boid) {
        boid.perceptionCircle.isVisible = flockProperties.isPerceptionCircleVisible()
        boid.path.isVisible = flockProperties.isAllPathsVisible()
    }

    abstract class FlockType {
        abstract fun createBoid(maxBoidSize: Double): Boid
        abstract fun setCenterPosition(boid: Boid, dimension: Dimension2D)
        override fun toString(): String {
            return "FlockType"
        }
    }

    inner class CircleFlock : FlockType() {
        override fun createBoid(maxBoidSize: Double): Boid {
            return CircleBoid(maxBoidSize, uniformBallColor)
        }

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

        override fun toString(): String {
            return "Uniform ordered circle flock"
        }
    }

    fun setUniformBallColor(uniformBallColor: Color) {
        this.uniformBallColor = uniformBallColor
    }
}
