package hzt.model.entity.boid;

import hzt.model.FlockProperties;
import hzt.model.controls.TranslationKeyFilter;
import hzt.model.entity.Connection;
import hzt.model.entity.Flock;
import hzt.model.entity.Path;
import hzt.model.entity.VisibleVector;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static hzt.model.utils.Engine.DENSITY;
import static hzt.service.AnimationService.LINE_STROKE_WIDTH;
import static java.util.function.Predicate.not;
import static javafx.scene.paint.Color.TRANSPARENT;

@ToString
@Getter
public abstract class Boid extends Group {

    private static int next = 0;

    private final String name;
    private final Shape body;
    private final Circle perceptionCircle;
    private final Circle repelCircle;
    private final VisibleVector visibleAccelerationVector;
    private final VisibleVector visibleVelocityVector;
    private final Path path;
    private final Map<Boid, Connection> perceptionRadiusMap;
    private final Paint initPaint;
    private final DoubleProperty densityMaterial = new SimpleDoubleProperty(); // kg/m^3

    private Point2D velocity; // pixel/s
    private Point2D acceleration; // pixel/s^2
    private Point2D prevCenterPosition = Point2D.ZERO;
    private double maxAcceleration;

    private final TranslationKeyFilter translationKeyFilter = new TranslationKeyFilter();

    Boid(String name, Shape body, Paint initPaint) {
        this.name = name + " " + ++next;
        this.initPaint = initPaint;
        this.body = body;
        this.perceptionCircle = new Circle();
        this.repelCircle = new Circle();
        this.visibleVelocityVector = new VisibleVector();
        this.visibleAccelerationVector = new VisibleVector();
        this.path = new Path();
        this.densityMaterial.set(DENSITY);
        this.perceptionRadiusMap = new HashMap<>();

        this.velocity = Point2D.ZERO;
        this.acceleration = Point2D.ZERO;
        configureComponents();
        super.getChildren().addAll(this.body, perceptionCircle, repelCircle, visibleVelocityVector, visibleAccelerationVector, path);
    }

    private void configureComponents() {
        body.setCursor(Cursor.HAND);
        configureCircle(perceptionCircle);
        configureCircle(repelCircle);
        configureVisibleVector(visibleVelocityVector);
        configureVisibleVector(visibleAccelerationVector);
        updatePaint(initPaint);
        path.setVisible(false);
        path.setLineWidth(getDistanceFromCenterToOuterEdge() / 4);
        visibleAccelerationVector.getStrokeDashArray().addAll(4., 4.);
    }

    public abstract double getDistanceFromCenterToOuterEdge();

    private void configureCircle(Circle circle) {
        circle.setStrokeType(StrokeType.OUTSIDE);
        circle.setDisable(true); //ignores user input
        circle.setFill(TRANSPARENT);
        circle.centerXProperty().bind(body.translateXProperty());
        circle.centerYProperty().bind(body.translateYProperty());
    }

    private void configureVisibleVector(Line line) {
        line.setStrokeWidth(LINE_STROKE_WIDTH);
        line.startXProperty().bind(body.translateXProperty());
        line.startYProperty().bind(body.translateYProperty());
    }

    public void update(Duration deltaT, double accelerationMultiplier, double frictionFactor, double maxVelocity) {
        acceleration = Point2D.ZERO;
        Flock flock = (Flock) this.getParent();
        maxAcceleration = accelerationMultiplier / deltaT.toSeconds();
        translationKeyFilter.setUserInputSize(maxAcceleration);
        Set<Boid> ballsSet = perceptionRadiusMap.keySet();
        Point2D physicsEngineAcceleration = flock.getFlockingSim().getTotalAcceleration(this, ballsSet);
        acceleration = acceleration.add(physicsEngineAcceleration);
        acceleration = acceleration.add(addFriction(frictionFactor));
        acceleration = acceleration.add(translationKeyFilter.getUserInputAcceleration());
        updatePositionAndVelocityBasedOnAcceleration(deltaT, maxVelocity, maxAcceleration);
        updateVisibleComponents(maxVelocity);
        updateBallsInPerceptionRadiusMap();
    }

    private void updateVisibleComponents(double maxVelocity) {
        Flock flock = (Flock) getParent();
        FlockProperties flockProperties = flock.getFlockProperties();
        final int MIN_VELOCITY_VECTOR_LENGTH = 300;
        double velocityCorrection = maxVelocity >= MIN_VELOCITY_VECTOR_LENGTH ? maxVelocity : MIN_VELOCITY_VECTOR_LENGTH;
        updateVisibleVector(visibleVelocityVector, velocity, velocityCorrection, flockProperties.getVelocityVectorLength());
        updateVisibleVector(visibleAccelerationVector, acceleration, 2000, flockProperties.getAccelerationVectorLength());
        updatePath(flockProperties.getTailLength());
        if (flockProperties.isAllPathsVisible()) {
            path.fadeOut();
        }
        if (flockProperties.isShowConnections()) {
            perceptionRadiusMap.forEach(this::strokeConnection);
        } else {
            getChildren().removeIf(Connection.class::isInstance);
        }
    }

    private void updateVisibleVector(Line line, Point2D vector, double correction, double maxVectorLength) {
        Point2D begin = getTranslation();
        Point2D end = begin.add(vector);
        Point2D unitVector = end.subtract(begin).normalize();
        Point2D radiusInVectorDir = unitVector.multiply(getDistanceFromCenterToOuterEdge() - line.getStrokeWidth());
        begin = begin.add(radiusInVectorDir);
        end = begin.add(unitVector.multiply(maxVectorLength * vector.magnitude() / correction));
        double visibleVectorMagnitude = end.subtract(begin).magnitude();
        if (visibleVectorMagnitude > maxVectorLength) {
            end = begin.add(unitVector.multiply(maxVectorLength));
        }
        line.setEndX(end.getX());
        line.setEndY(end.getY());
    }

    private void strokeConnection(Boid otherBall, Connection lineToOther) {
        double distance = otherBall.getTranslation().subtract(this.getTranslation()).magnitude();
        lineToOther.setStroke(this.body.getFill());
        lineToOther.setStrokeWidth(LINE_STROKE_WIDTH);
        lineToOther.setDisable(true); // ignores user input
        lineToOther.setOpacity(1 - distance / this.perceptionCircle.getRadius());
        lineToOther.setStartX(this.body.getTranslateX());
        lineToOther.setStartY(this.body.getTranslateY());
        lineToOther.setEndX(otherBall.getBody().getTranslateX());
        lineToOther.setEndY(otherBall.getBody().getTranslateY());
        if (!this.getChildren().contains(lineToOther)) {
            this.getChildren().add(lineToOther);
        }
    }

    private void updatePath(double maxPathLength) {
        path.addLine(getTranslation(), prevCenterPosition);
        while (maxPathLength > 0 && path.getElements().size() >= maxPathLength) {
            path.removeLine(0);
        }
    }

    private void updateBallsInPerceptionRadiusMap() {
        Flock flock = (Flock) getParent();
        flock.getChildrenUnmodifiable().stream()
                .filter(not(this::equals))
                .map(Boid.class::cast)
                .forEach(this::determineIfBoidInPerceptionRadius);
    }

    private void determineIfBoidInPerceptionRadius(Boid other) {
        double distance = other.getTranslation().subtract(this.getTranslation()).magnitude();
        if (distance >= perceptionCircle.getRadius()) {
            Line lineToOther = perceptionRadiusMap.remove(other);
            this.getChildren().remove(lineToOther);
        } else {
            perceptionRadiusMap.computeIfAbsent(other, e -> new Connection());
        }
    }

    public Point2D addFriction(double frictionFactor) {
        Point2D decelerationDir = velocity.multiply(-1);
        return decelerationDir.multiply(frictionFactor);
    }

    private void updatePositionAndVelocityBasedOnAcceleration(Duration deltaT, double maxSpeed, double maxAcceleration) {
        Point2D position = getTranslation();
        double deltaTSeconds = deltaT.toSeconds();
        acceleration = limit(maxAcceleration, acceleration);
        velocity = velocity.add(acceleration.multiply(deltaTSeconds));
        velocity = limit(maxSpeed, velocity);
        prevCenterPosition = position;
        position = position.add(velocity.multiply(deltaTSeconds));
        this.setBodyTranslate(position.getX(), position.getY());
    }

    Point2D limit(double maxValue, Point2D limitedVector) {
        if (limitedVector.magnitude() > maxValue) {
            limitedVector = limitedVector.normalize().multiply(maxValue);
        }
        return limitedVector;
    }

    public void floatThroughEdges(Dimension2D dimension) {
        double width = dimension.getWidth();
        double height = dimension.getHeight();
        Point2D centerPosition = getTranslation();
        if (body.getTranslateX() >= width) {
            this.setBodyTranslate(0, centerPosition.getY());
        } else if (body.getTranslateX() <= 0) {
            this.setBodyTranslate(width, centerPosition.getY());
        }
        if (body.getTranslateY() >= height) {
            this.setBodyTranslate(centerPosition.getX(), 0);
        } else if (body.getTranslateY() <= 0) {
            this.setBodyTranslate(centerPosition.getX(), height);
        }
    }

    public void bounceOfEdges(Dimension2D dimension) {
        double width = dimension.getWidth();
        double height = dimension.getHeight();
        Bounds bounds = body.getBoundsInParent();
        Point2D translation = getTranslation();
        if (bounds.getMinX() <= 0 && translation.getX() < prevCenterPosition.getX()) {
            velocity = new Point2D(-velocity.getX(), velocity.getY());
        } else if (bounds.getMinY() <= 0 && translation.getY() < prevCenterPosition.getY()) {
            velocity = new Point2D(velocity.getX(), -velocity.getY());
        } else if (bounds.getMaxX() >= width && translation.getX() > prevCenterPosition.getX()) {
            velocity = new Point2D(-velocity.getX(), velocity.getY());
        } else if (bounds.getMaxY() >= height && translation.getY() > prevCenterPosition.getY()) {
            velocity = new Point2D(velocity.getX(), -velocity.getY());
        }
    }

    public String getName() {
        return name;
    }

    public abstract double getMass();

    public Point2D getTranslation() {
        return new Point2D(body.getTranslateX(), body.getTranslateY());
    }

    public void setBodyTranslate(Point2D point2D) {
        body.setTranslateX(point2D.getX());
        body.setTranslateY(point2D.getY());
    }

    public void setBodyTranslate(double x, double y) {
        body.setTranslateX(x);
        body.setTranslateY(y);
    }

    public void addKeyControlForAcceleration() {
        Scene scene = ((Flock) getParent()).getMainScene();
        scene.addEventFilter(KeyEvent.KEY_PRESSED, translationKeyFilter.getKeyPressed());
        scene.addEventFilter(KeyEvent.KEY_RELEASED, translationKeyFilter.getKeyReleased());
    }

    public void removeKeyControlsForAcceleration() {
        Scene scene = ((Flock) getParent()).getMainScene();
        translationKeyFilter.resetKeyPressed();
        scene.removeEventFilter(KeyEvent.KEY_PRESSED, translationKeyFilter.getKeyPressed());
        scene.removeEventFilter(KeyEvent.KEY_RELEASED, translationKeyFilter.getKeyReleased());
    }

    public void updatePaint(Paint paint) {
        body.setFill(paint);
        perceptionCircle.setStroke(paint);
        repelCircle.setStroke(paint);
        visibleVelocityVector.setStroke(paint);
        visibleAccelerationVector.setStroke(paint);
        path.setStroke(paint);
    }

    public static int getNext() {
        return next;
    }

    public void setVisibilityBoidComponents(FlockProperties flockProperties) {
        visibleVelocityVector.setVisible(flockProperties.isVelocityVectorVisible());
        visibleAccelerationVector.setVisible(flockProperties.isAccelerationVectorVisible());
        perceptionCircle.setVisible(flockProperties.isPerceptionCircleVisible());
        repelCircle.setVisible(flockProperties.isRepelCircleVisible());
        path.setVisible(flockProperties.isAllPathsVisible());
    }

    public void addMouseFunctionality() {
        body.setOnMousePressed(this::onMousePressed);
    }

    private void onMousePressed(MouseEvent mouseEvent) {
        Flock flock = (Flock) getParent();
        Boid prevSelected = flock.getSelectedBoid();
        updatePaint(flock.getSelectedBallColor());
        setVelocity(Point2D.ZERO);
        if (!this.equals(prevSelected)) {
            addKeyControlForAcceleration();
            toFront();
            if (prevSelected != null) {
                prevSelected.removeKeyControlsForAcceleration();
                prevSelected.updatePaint(prevSelected.getInitPaint());
                flock.updateBoidComponentsVisibility(prevSelected);
            }
            flock.setSelectedBoid(this);
            flock.updateSelectedBoidComponentsVisibility(this);
        }
    }

    public double getDensityMaterial() {
        return densityMaterial.get();
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    public void setPerceptionRadius(double radius) {
        this.perceptionCircle.setRadius(radius);
    }

    public float getRepelRadius() {
        return (float) repelCircle.getRadius();
    }

    public void setRepelRadius(double radius) {
        this.repelCircle.setRadius(radius);
    }
}
