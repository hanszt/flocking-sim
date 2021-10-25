package hzt.model.entity.boid

import hzt.model.FlockProperties
import hzt.model.controls.TranslationKeyFilter
import hzt.model.entity.Connection
import hzt.model.entity.Flock
import hzt.model.entity.Path
import hzt.model.entity.VisibleVector
import hzt.model.utils.Engine
import hzt.service.AnimationService
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Shape
import javafx.scene.shape.StrokeType
import javafx.util.Duration
import java.util.function.Predicate

abstract class Boid internal constructor(name: String, body: Shape, initPaint: Paint) : Group() {
    val name: String
    val body: Shape
    val perceptionCircle: Circle
    val repelCircle: Circle
    val visibleAccelerationVector: VisibleVector
    val visibleVelocityVector: VisibleVector
    val path: Path
    val perceptionRadiusMap: MutableMap<Boid, Connection>
    private val initPaint: Paint
    private val densityMaterial: DoubleProperty = SimpleDoubleProperty() // kg/m^3
    var velocity // pixel/s
            : Point2D
    var acceleration // pixel/s^2
            : Point2D
        private set
    private var prevCenterPosition = Point2D.ZERO
    private var maxAcceleration = 0.0
    private val translationKeyFilter = TranslationKeyFilter()
    private fun configureComponents() {
        body.cursor = Cursor.HAND
        configureCircle(perceptionCircle)
        configureCircle(repelCircle)
        configureVisibleVector(visibleVelocityVector)
        configureVisibleVector(visibleAccelerationVector)
        updatePaint(initPaint)
        path.isVisible = false
        path.setLineWidth(distanceFromCenterToOuterEdge / 4)
        visibleAccelerationVector.strokeDashArray.addAll(4.0, 4.0)
    }

    abstract val distanceFromCenterToOuterEdge: Double
    private fun configureCircle(circle: Circle) {
        circle.strokeType = StrokeType.OUTSIDE
        circle.isDisable = true //ignores user input
        circle.fill = Color.TRANSPARENT
        circle.centerXProperty().bind(body.translateXProperty())
        circle.centerYProperty().bind(body.translateYProperty())
    }

    private fun configureVisibleVector(line: Line) {
        line.strokeWidth = AnimationService.LINE_STROKE_WIDTH.toDouble()
        line.startXProperty().bind(body.translateXProperty())
        line.startYProperty().bind(body.translateYProperty())
    }

    fun update(deltaT: Duration, accelerationMultiplier: Double, frictionFactor: Double, maxVelocity: Double) {
        acceleration = Point2D.ZERO
        val flock = parent as Flock
        maxAcceleration = accelerationMultiplier / deltaT.toSeconds()
        translationKeyFilter.userInputSize = maxAcceleration
        val ballsSet: Set<Boid> = perceptionRadiusMap.keys
        val physicsEngineAcceleration = flock.flockingSim?.getTotalAcceleration(this, ballsSet)
        acceleration = acceleration.add(physicsEngineAcceleration)
        acceleration = acceleration.add(addFriction(frictionFactor))
        acceleration = acceleration.add(translationKeyFilter.userInputAcceleration)
        updatePositionAndVelocityBasedOnAcceleration(deltaT, maxVelocity, maxAcceleration)
        updateVisibleComponents(maxVelocity)
        updateBallsInPerceptionRadiusMap()
    }

    private fun updateVisibleComponents(maxVelocity: Double) {
        val flock = parent as Flock
        val flockProperties = flock.flockProperties
        val minVelVectorLength = 300
        val velocityCorrection =
            if (maxVelocity >= minVelVectorLength) maxVelocity else minVelVectorLength.toDouble()
        updateVisibleVector(visibleVelocityVector, velocity, velocityCorrection, flockProperties.getVelocityVectorLength())
        updateVisibleVector(visibleAccelerationVector, acceleration, 2000.0, flockProperties.getAccelerationVectorLength())
        updatePath(flockProperties.getTailLength())
        if (flockProperties.isAllPathsVisible()) {
            path.fadeOut()
        }
        if (flockProperties.isShowConnections()) {
            perceptionRadiusMap.forEach { (otherBall: Boid?, lineToOther: Connection?) ->
                strokeConnection(
                    otherBall,
                    lineToOther
                )
            }
        } else {
            children.removeIf { obj: Node? -> Connection::class.java.isInstance(obj) }
        }
    }

    private fun updateVisibleVector(line: Line, vector: Point2D, correction: Double, maxVectorLength: Double) {
        var begin = translation
        var end = begin.add(vector)
        val unitVector = end.subtract(begin).normalize()
        val radiusInVectorDir = unitVector.multiply(distanceFromCenterToOuterEdge - line.strokeWidth)
        begin = begin.add(radiusInVectorDir)
        end = begin.add(unitVector.multiply(maxVectorLength * vector.magnitude() / correction))
        val visibleVectorMagnitude = end.subtract(begin).magnitude()
        if (visibleVectorMagnitude > maxVectorLength) {
            end = begin.add(unitVector.multiply(maxVectorLength))
        }
        line.endX = end.x
        line.endY = end.y
    }

    private fun strokeConnection(otherBall: Boid?, lineToOther: Connection?) {
        val distance = otherBall!!.translation.subtract(translation).magnitude()
        lineToOther!!.stroke = body.fill
        lineToOther.strokeWidth = AnimationService.LINE_STROKE_WIDTH.toDouble()
        lineToOther.isDisable = true // ignores user input
        lineToOther.opacity = 1 - distance / perceptionCircle.radius
        lineToOther.startX = body.translateX
        lineToOther.startY = body.translateY
        lineToOther.endX = otherBall.body.translateX
        lineToOther.endY = otherBall.body.translateY
        if (!children.contains(lineToOther)) {
            children.add(lineToOther)
        }
    }

    private fun updatePath(maxPathLength: Double) {
        path.addLine(translation, prevCenterPosition)
        while (maxPathLength > 0 && path.elements.size >= maxPathLength) {
            path.removeLine(0)
        }
    }

    private fun updateBallsInPerceptionRadiusMap() {
        val flock = parent as Flock
        flock.childrenUnmodifiable.stream()
            .filter(Predicate.not { obj: Node? -> this == obj })
            .map { obj: Node? -> Boid::class.java.cast(obj) }
            .forEach { other: Boid -> determineIfBoidInPerceptionRadius(other) }
    }

    private fun determineIfBoidInPerceptionRadius(other: Boid) {
        val distance = other.translation.subtract(translation).magnitude()
        if (distance >= perceptionCircle.radius) {
            val lineToOther: Line? = perceptionRadiusMap.remove(other)
            children.remove(lineToOther)
        } else {
            perceptionRadiusMap.getOrPut(other) { Connection() }
        }
    }

    private fun addFriction(frictionFactor: Double): Point2D {
        val decelerationDir = velocity.multiply(-1.0)
        return decelerationDir.multiply(frictionFactor)
    }

    private fun updatePositionAndVelocityBasedOnAcceleration(
        deltaT: Duration,
        maxSpeed: Double,
        maxAcceleration: Double
    ) {
        var position = translation
        val deltaTSeconds = deltaT.toSeconds()
        acceleration = limit(maxAcceleration, acceleration)
        velocity = velocity.add(acceleration.multiply(deltaTSeconds))
        velocity = limit(maxSpeed, velocity)
        prevCenterPosition = position
        position = position.add(velocity.multiply(deltaTSeconds))
        this.setBodyTranslate(position.x, position.y)
    }

    fun limit(maxValue: Double, limitedVector: Point2D): Point2D {
        if (limitedVector.magnitude() > maxValue) {
            return limitedVector.normalize().multiply(maxValue)
        }
        return limitedVector
    }

    fun floatThroughEdges(dimension: Dimension2D) {
        val width = dimension.width
        val height = dimension.height
        val centerPosition = translation
        if (body.translateX >= width) {
            this.setBodyTranslate(0.0, centerPosition.y)
        } else if (body.translateX <= 0) {
            this.setBodyTranslate(width, centerPosition.y)
        }
        if (body.translateY >= height) {
            this.setBodyTranslate(centerPosition.x, 0.0)
        } else if (body.translateY <= 0) {
            this.setBodyTranslate(centerPosition.x, height)
        }
    }

    fun bounceOfEdges(dimension: Dimension2D) {
        val width = dimension.width
        val height = dimension.height
        val bounds = body.boundsInParent
        val translation = translation
        if (bounds.minX <= 0 && translation.x < prevCenterPosition.x) {
            velocity = Point2D(-velocity.x, velocity.y)
        } else if (bounds.minY <= 0 && translation.y < prevCenterPosition.y) {
            velocity = Point2D(velocity.x, -velocity.y)
        } else if (bounds.maxX >= width && translation.x > prevCenterPosition.x) {
            velocity = Point2D(-velocity.x, velocity.y)
        } else if (bounds.maxY >= height && translation.y > prevCenterPosition.y) {
            velocity = Point2D(velocity.x, -velocity.y)
        }
    }

    abstract val mass: Double
    val translation: Point2D
        get() = Point2D(body.translateX, body.translateY)

    fun setBodyTranslate(point2D: Point2D) {
        body.translateX = point2D.x
        body.translateY = point2D.y
    }

    private fun setBodyTranslate(x: Double, y: Double) {
        body.translateX = x
        body.translateY = y
    }

    fun addKeyControlForAcceleration() {
        val scene = (parent as Flock).mainScene
        scene.addEventFilter(KeyEvent.KEY_PRESSED, translationKeyFilter.keyPressed)
        scene.addEventFilter(KeyEvent.KEY_RELEASED, translationKeyFilter.keyReleased)
    }

    private fun removeKeyControlsForAcceleration() {
        val scene = (parent as Flock).mainScene
        translationKeyFilter.resetKeyPressed()
        scene.removeEventFilter(KeyEvent.KEY_PRESSED, translationKeyFilter.keyPressed)
        scene.removeEventFilter(KeyEvent.KEY_RELEASED, translationKeyFilter.keyReleased)
    }

    fun updatePaint(paint: Paint?) {
        body.fill = paint
        perceptionCircle.stroke = paint
        repelCircle.stroke = paint
        visibleVelocityVector.stroke = paint
        visibleAccelerationVector.stroke = paint
        path.setStroke(paint)
    }

    fun setVisibilityBoidComponents(flockProperties: FlockProperties) {
        visibleVelocityVector.isVisible = flockProperties.isVelocityVectorVisible()
        visibleAccelerationVector.isVisible = flockProperties.isAccelerationVectorVisible()
        perceptionCircle.isVisible = flockProperties.isPerceptionCircleVisible()
        repelCircle.isVisible = flockProperties.isRepelCircleVisible()
        path.isVisible = flockProperties.isAllPathsVisible()
    }

    fun addMouseFunctionality() {
        body.onMousePressed = EventHandler { onMousePressed() }
    }

    private fun onMousePressed() {
        val flock = parent as Flock
        val prevSelected = flock.selectedBoid
        updatePaint(flock.selectedBallColor)
        velocity = Point2D.ZERO
        if (this != prevSelected) {
            addKeyControlForAcceleration()
            toFront()
            if (prevSelected != null) {
                prevSelected.removeKeyControlsForAcceleration()
                prevSelected.updatePaint(prevSelected.initPaint)
                flock.updateBoidComponentsVisibility(prevSelected)
            }
            flock.selectedBoid = this
            flock.updateSelectedBoidComponentsVisibility(this)
        }
    }

    fun getDensityMaterial(): Double {
        return densityMaterial.get()
    }

    fun setPerceptionRadius(radius: Double) {
        perceptionCircle.radius = radius
    }

    val repelRadius: Float
        get() = repelCircle.radius.toFloat()

    fun setRepelRadius(radius: Double) {
        repelCircle.radius = radius
    }

    companion object {
        var next = 0
            private set
    }

    init {
        this.name = name + " " + ++next
        this.initPaint = initPaint
        this.body = body
        perceptionCircle = Circle()
        repelCircle = Circle()
        visibleVelocityVector = VisibleVector()
        visibleAccelerationVector = VisibleVector()
        path = Path()
        densityMaterial.set(Engine.DENSITY)
        perceptionRadiusMap = HashMap()
        velocity = Point2D.ZERO
        acceleration = Point2D.ZERO
        configureComponents()
        super.getChildren()
            .addAll(this.body, perceptionCircle, repelCircle, visibleVelocityVector, visibleAccelerationVector, path)
    }
}
