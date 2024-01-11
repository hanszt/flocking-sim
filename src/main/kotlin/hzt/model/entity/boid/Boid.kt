package hzt.model.entity.boid

import hzt.model.FlockProperties
import hzt.model.controls.TranslationKeyFilter
import hzt.model.entity.Connection
import hzt.model.entity.Flock
import hzt.model.entity.Path
import hzt.model.entity.VisibleVector
import hzt.model.utils.Engine
import hzt.utils.*
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Shape
import javafx.scene.shape.StrokeType
import javafx.util.Duration

abstract class Boid internal constructor(name: String, val body: Shape, private val initPaint: Paint) : Group() {

    val name: String = name + " " + ++next
    val perceptionCircle: Circle = Circle()
    val repelCircle: Circle = Circle()
    val visibleAccelerationVector: VisibleVector = VisibleVector()
    val visibleVelocityVector: VisibleVector = VisibleVector()
    val path: Path = Path()
    val perceptionRadiusMap: MutableMap<Boid, Connection>
    private val densityMaterial: DoubleProperty = SimpleDoubleProperty() // kg/m^3
    private val translationKeyFilter = TranslationKeyFilter()

    var velocity: Point2D
    var acceleration: Point2D
    private var prevCenterPosition = Point2D.ZERO
    private var maxAcceleration = 0.0

    init {
        densityMaterial.set(Engine.DENSITY)
        perceptionRadiusMap = HashMap()
        velocity = Point2D.ZERO
        acceleration = Point2D.ZERO
        configureComponents()
        children.addAll(body, perceptionCircle, repelCircle, visibleVelocityVector, visibleAccelerationVector, path)
    }

    private fun configureComponents() {
        body.cursor = Cursor.HAND
        perceptionCircle.configure()
        repelCircle.configure()
        visibleVelocityVector.configure()
        visibleAccelerationVector.configure()
        updatePaint(initPaint)
        path.isVisible = false
        path.setLineWidth(distanceFromCenterToOuterEdge / 4)
        visibleAccelerationVector.strokeDashArray.addAll(4.0, 4.0)
    }

    abstract val distanceFromCenterToOuterEdge: Double

    private fun Circle.configure() {
        centerXProperty().bind(body.translateXProperty())
        centerYProperty().bind(body.translateYProperty())
        withFill(Color.TRANSPARENT)
            .withStrokeType(StrokeType.OUTSIDE)
            .isDisabled(true) //ignores user input
    }

    private fun VisibleVector.configure() {
        strokeWidth = LINE_STROKE_WIDTH.toDouble()
        startXProperty().bind(body.translateXProperty())
        startYProperty().bind(body.translateYProperty())
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
        updateVisibleVector(
            visibleVelocityVector,
            velocity,
            velocityCorrection,
            flockProperties.getVelocityVectorLength()
        )
        updateVisibleVector(
            visibleAccelerationVector,
            acceleration,
            2000.0,
            flockProperties.getAccelerationVectorLength()
        )
        updatePath(flockProperties.getTailLength())
        if (flockProperties.isAllPathsVisible()) {
            path.fadeOut()
        }
        if (flockProperties.isShowConnections()) {
            perceptionRadiusMap.forEach(this::strokeConnection)
        } else {
            children.removeIf { it is Connection }
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
        val distance = otherBall?.translation?.subtract(translation)?.magnitude() ?: 0.0
        lineToOther?.apply {
            stroke = body.fill
            strokeWidth = LINE_STROKE_WIDTH.toDouble()
            isDisable = true // ignores user input
            opacity = 1 - distance / perceptionCircle.radius
            withStart(body.translateX, body.translateY)
            withEnd(otherBall?.body?.translateX ?: 0.0, otherBall?.body?.translateY ?: 0.0)
        }
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
        flock.childrenUnmodifiable.asSequence()
            .filter { this != it }
            .filterIsInstance<Boid>()
            .forEach(::determineIfBoidInPerceptionRadius)
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

    private fun addFriction(frictionFactor: Double): Point2D = velocity.multiply(-frictionFactor)

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

    private fun limit(maxValue: Double, limitedVector: Point2D): Point2D =
        if (limitedVector.magnitude() > maxValue) limitedVector.normalize().multiply(maxValue) else limitedVector

    fun floatThroughEdges(dimension: Dimension2D) {
        val centerPosition = translation
        if (body.translateX >= dimension.width) {
            this.setBodyTranslate(0.0, centerPosition.y)
        } else if (body.translateX <= 0) {
            this.setBodyTranslate(dimension.width, centerPosition.y)
        }
        if (body.translateY >= dimension.height) {
            this.setBodyTranslate(centerPosition.x, 0.0)
        } else if (body.translateY <= 0) {
            this.setBodyTranslate(centerPosition.x, dimension.height)
        }
    }

    fun bounceOfEdges(dimension: Dimension2D) {
        val width = dimension.width
        val height = dimension.height
        val bounds = body.boundsInParent
        val translation = translation
        when {
            bounds.minX <= 0 && translation.x < prevCenterPosition.x -> velocity = Point2D(-velocity.x, velocity.y)
            bounds.minY <= 0 && translation.y < prevCenterPosition.y -> velocity = Point2D(velocity.x, -velocity.y)
            bounds.maxX >= width && translation.x > prevCenterPosition.x -> velocity = Point2D(-velocity.x, velocity.y)
            bounds.maxY >= height && translation.y > prevCenterPosition.y -> velocity = Point2D(velocity.x, -velocity.y)
        }
    }

    abstract val mass: Double

    val translation: Point2D
        get() = Point2D(body.translateX, body.translateY)

    fun setBodyTranslate(point2D: Point2D) = setBodyTranslate(point2D.x, point2D.y)

    private fun setBodyTranslate(x: Double, y: Double) = body.run {
        translateX = x
        translateY = y
    }

    fun addKeyControlForAcceleration() = (parent as Flock).mainScene.apply {
        addEventFilter(KeyEvent.KEY_PRESSED, translationKeyFilter.keyPressed)
        addEventFilter(KeyEvent.KEY_RELEASED, translationKeyFilter.keyReleased)
    }

    private fun removeKeyControlsForAcceleration() = (parent as Flock).mainScene.apply {
        removeEventFilter(KeyEvent.KEY_PRESSED, translationKeyFilter.keyPressed)
        removeEventFilter(KeyEvent.KEY_RELEASED, translationKeyFilter.keyReleased)
        translationKeyFilter.resetKeyPressed()
    }

    fun updatePaint(paint: Paint) {
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

    fun densityMaterial(): Double = densityMaterial.get()

    fun setPerceptionRadius(radius: Double) {
        perceptionCircle.radius = radius
    }

    val repelRadius: Float
        get() = repelCircle.radius.toFloat()

    fun setRepelRadius(radius: Double) {
        repelCircle.radius = radius
    }

    companion object {
        const val LINE_STROKE_WIDTH = 2
        var next = 0
            private set
    }

}
