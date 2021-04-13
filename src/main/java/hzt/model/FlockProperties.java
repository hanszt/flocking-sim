package hzt.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class FlockProperties {

    private final BooleanProperty velocityVectorVisible = new SimpleBooleanProperty();
    private final BooleanProperty accelerationVectorVisible = new SimpleBooleanProperty();
    private final BooleanProperty repelCircleVisible = new SimpleBooleanProperty();
    private final BooleanProperty perceptionCircleVisible = new SimpleBooleanProperty();
    private final BooleanProperty allPathsVisible = new SimpleBooleanProperty();
    private final BooleanProperty selectedPathVisible = new SimpleBooleanProperty();
    private final BooleanProperty selectedPerceptionCircleVisible = new SimpleBooleanProperty();
    private final BooleanProperty showConnections = new SimpleBooleanProperty();

    private final DoubleProperty maxVelocity = new SimpleDoubleProperty();
    private final DoubleProperty maxAcceleration  = new SimpleDoubleProperty();

    private final DoubleProperty maxBoidSize = new SimpleDoubleProperty();
    private final DoubleProperty perceptionRadiusRatio = new SimpleDoubleProperty();
    private final DoubleProperty repelRadiusRatio = new SimpleDoubleProperty();

    private final DoubleProperty velocityVectorLength = new SimpleDoubleProperty();
    private final DoubleProperty accelerationVectorLength = new SimpleDoubleProperty();
    private final DoubleProperty tailLength = new SimpleDoubleProperty();

    public boolean isVelocityVectorVisible() {
        return velocityVectorVisible.get();
    }

    public BooleanProperty velocityVectorVisibleProperty() {
        return velocityVectorVisible;
    }

    public boolean isAccelerationVectorVisible() {
        return accelerationVectorVisible.get();
    }

    public BooleanProperty accelerationVectorVisibleProperty() {
        return accelerationVectorVisible;
    }

    public boolean isRepelCircleVisible() {
        return repelCircleVisible.get();
    }

    public BooleanProperty repelCircleVisibleProperty() {
        return repelCircleVisible;
    }

    public boolean isPerceptionCircleVisible() {
        return perceptionCircleVisible.get();
    }

    public BooleanProperty perceptionCircleVisibleProperty() {
        return perceptionCircleVisible;
    }

    public boolean isAllPathsVisible() {
        return allPathsVisible.get();
    }

    public BooleanProperty allPathsVisibleProperty() {
        return allPathsVisible;
    }

    public boolean isSelectedPathVisible() {
        return selectedPathVisible.get();
    }

    public BooleanProperty selectedPathVisibleProperty() {
        return selectedPathVisible;
    }

    public boolean isSelectedPerceptionCircleVisible() {
        return selectedPerceptionCircleVisible.get();
    }

    public BooleanProperty selectedPerceptionCircleVisibleProperty() {
        return selectedPerceptionCircleVisible;
    }

    public boolean isShowConnections() {
        return showConnections.get();
    }

    public BooleanProperty showConnectionsProperty() {
        return showConnections;
    }

    public DoubleProperty maxVelocityProperty() {
        return maxVelocity;
    }

    public DoubleProperty maxAccelerationProperty() {
        return maxAcceleration;
    }

    public double getMaxBoidSize() {
        return maxBoidSize.get();
    }

    public DoubleProperty maxBoidSizeProperty() {
        return maxBoidSize;
    }

    public double getPerceptionRadiusRatio() {
        return perceptionRadiusRatio.get();
    }

    public DoubleProperty perceptionRadiusRatioProperty() {
        return perceptionRadiusRatio;
    }

    public double getRepelRadiusRatio() {
        return repelRadiusRatio.get();
    }

    public DoubleProperty repelRadiusRatioProperty() {
        return repelRadiusRatio;
    }

    public double getVelocityVectorLength() {
        return velocityVectorLength.get();
    }

    public DoubleProperty velocityVectorLengthProperty() {
        return velocityVectorLength;
    }

    public double getAccelerationVectorLength() {
        return accelerationVectorLength.get();
    }

    public DoubleProperty accelerationVectorLengthProperty() {
        return accelerationVectorLength;
    }

    public double getTailLength() {
        return tailLength.get();
    }

    public DoubleProperty tailLengthProperty() {
        return tailLength;
    }
}
