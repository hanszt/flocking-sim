package hzt.model

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty

class FlockProperties {

    private val velocityVectorVisible: BooleanProperty = SimpleBooleanProperty()
    private val accelerationVectorVisible: BooleanProperty = SimpleBooleanProperty()
    private val repelCircleVisible: BooleanProperty = SimpleBooleanProperty()
    private val perceptionCircleVisible: BooleanProperty = SimpleBooleanProperty()
    private val allPathsVisible: BooleanProperty = SimpleBooleanProperty()
    private val selectedPathVisible: BooleanProperty = SimpleBooleanProperty()
    private val selectedPerceptionCircleVisible: BooleanProperty = SimpleBooleanProperty()
    private val showConnections: BooleanProperty = SimpleBooleanProperty()
    private val maxVelocity: DoubleProperty = SimpleDoubleProperty()
    private val maxAcceleration: DoubleProperty = SimpleDoubleProperty()
    private val maxBoidSize: DoubleProperty = SimpleDoubleProperty()
    private val perceptionRadiusRatio: DoubleProperty = SimpleDoubleProperty()
    private val repelRadiusRatio: DoubleProperty = SimpleDoubleProperty()
    private val velocityVectorLength: DoubleProperty = SimpleDoubleProperty()
    private val accelerationVectorLength: DoubleProperty = SimpleDoubleProperty()
    private val tailLength: DoubleProperty = SimpleDoubleProperty()

    fun isVelocityVectorVisible(): Boolean {
        return velocityVectorVisible.get()
    }

    fun velocityVectorVisibleProperty(): BooleanProperty {
        return velocityVectorVisible
    }

    fun isAccelerationVectorVisible(): Boolean {
        return accelerationVectorVisible.get()
    }

    fun accelerationVectorVisibleProperty(): BooleanProperty {
        return accelerationVectorVisible
    }

    fun isRepelCircleVisible(): Boolean {
        return repelCircleVisible.get()
    }

    fun repelCircleVisibleProperty(): BooleanProperty {
        return repelCircleVisible
    }

    fun isPerceptionCircleVisible(): Boolean {
        return perceptionCircleVisible.get()
    }

    fun perceptionCircleVisibleProperty(): BooleanProperty {
        return perceptionCircleVisible
    }

    fun isAllPathsVisible(): Boolean {
        return allPathsVisible.get()
    }

    fun allPathsVisibleProperty(): BooleanProperty {
        return allPathsVisible
    }

    fun isSelectedPathVisible(): Boolean {
        return selectedPathVisible.get()
    }

    fun selectedPathVisibleProperty(): BooleanProperty {
        return selectedPathVisible
    }

    fun isSelectedPerceptionCircleVisible(): Boolean {
        return selectedPerceptionCircleVisible.get()
    }

    fun selectedPerceptionCircleVisibleProperty(): BooleanProperty {
        return selectedPerceptionCircleVisible
    }

    fun isShowConnections(): Boolean {
        return showConnections.get()
    }

    fun showConnectionsProperty(): BooleanProperty {
        return showConnections
    }

    fun maxVelocityProperty(): DoubleProperty {
        return maxVelocity
    }

    fun maxAccelerationProperty(): DoubleProperty {
        return maxAcceleration
    }

    fun getMaxBoidSize(): Double {
        return maxBoidSize.get()
    }

    fun maxBoidSizeProperty(): DoubleProperty {
        return maxBoidSize
    }

    fun getPerceptionRadiusRatio(): Double {
        return perceptionRadiusRatio.get()
    }

    fun perceptionRadiusRatioProperty(): DoubleProperty {
        return perceptionRadiusRatio
    }

    fun getRepelRadiusRatio(): Double {
        return repelRadiusRatio.get()
    }

    fun repelRadiusRatioProperty(): DoubleProperty {
        return repelRadiusRatio
    }

    fun getVelocityVectorLength(): Double {
        return velocityVectorLength.get()
    }

    fun velocityVectorLengthProperty(): DoubleProperty {
        return velocityVectorLength
    }

    fun getAccelerationVectorLength(): Double {
        return accelerationVectorLength.get()
    }

    fun accelerationVectorLengthProperty(): DoubleProperty {
        return accelerationVectorLength
    }

    fun getTailLength(): Double {
        return tailLength.get()
    }

    fun tailLengthProperty(): DoubleProperty {
        return tailLength
    }
}
