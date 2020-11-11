package hzt.model.entity;

import hzt.controller.MainSceneController;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import lombok.Getter;
import lombok.ToString;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static hzt.service.AnimationService.LINE_STROKE_WIDTH;
import static hzt.model.utils.Engine.DENSITY;
import static hzt.model.entity.Flock.MAX_PATH_SIZE;
import static hzt.model.entity.Flock.MAX_VECTOR_LENGTH;
import static javafx.scene.paint.Color.TRANSPARENT;

@ToString
@Getter
public class Boid extends Group {

    private static int next = 0;

    private final String name;
    private final Circle body;
    private final Circle perceptionCircle;
    private final Circle repelCircle;
    private final VisibleVector visibleAccelerationVector;
    private final VisibleVector visibleVelocityVector;
    private final Path path;
    private final Map<Boid, Connection> perceptionRadiusMap;
    private final Paint initPaint;
    private final double densityMaterial; // kg/m^3

    private Point2D velocity; // pixel/s
    private Point2D acceleration; // pixel/s^2

    public Boid(double radius, Paint paint) {
        this("Ball" + ++next, radius, paint);
    }

    public Boid(String name, double radius, Paint initPaint) {
        this.name = name;
        this.initPaint = initPaint;
        this.body = new Circle(radius);
        this.perceptionCircle = new Circle();
        this.repelCircle = new Circle();
        this.visibleVelocityVector = new VisibleVector();
        this.visibleAccelerationVector = new VisibleVector();
        this.path = new Path();
        this.densityMaterial = DENSITY;
        this.perceptionRadiusMap = new HashMap<>();

        this.velocity = Point2D.ZERO;
        this.acceleration = Point2D.ZERO;
        configureComponents();
        super.getChildren().addAll(body, perceptionCircle, repelCircle, visibleVelocityVector, visibleAccelerationVector, path);
    }

    private void configureComponents() {
        configureCircle(perceptionCircle);
        configureCircle(repelCircle);
        configureVisibleVector(visibleVelocityVector);
        configureVisibleVector(visibleAccelerationVector);
        updatePaint(initPaint);
        path.setVisible(false);
        path.setLineWidth(body.getRadius() / 4);
        visibleAccelerationVector.getStrokeDashArray().addAll(4., 4.);
    }

    private void configureCircle(Circle circle) {
        circle.setStrokeType(StrokeType.OUTSIDE);
        circle.setDisable(true); //ignores user input
        circle.setFill(TRANSPARENT);
        circle.centerXProperty().bind(body.centerXProperty());
        circle.centerYProperty().bind(body.centerYProperty());
    }

    private void configureVisibleVector(Line line) {
        line.setStrokeWidth(LINE_STROKE_WIDTH);
        line.startXProperty().bind(body.centerXProperty());
        line.startYProperty().bind(body.centerYProperty());
    }

    public void update(Duration deltaT, double accelerationMultiplier, double frictionFactor, double maxSpeed) {
        acceleration = Point2D.ZERO;
        Flock flock = (Flock) this.getParent();
        maxAcceleration = accelerationMultiplier / deltaT.toSeconds();
        Set<Boid> ballsSet = perceptionRadiusMap.keySet();
        Point2D physicsEngineAcceleration = flock.getFlockingSim().getTotalAcceleration(this, ballsSet);
        acceleration = acceleration.add(physicsEngineAcceleration);
        acceleration = acceleration.add(addFriction(frictionFactor));
        acceleration = acceleration.add(userInputAcceleration);
        updatePositionAndVelocityBasedOnAcceleration(deltaT, maxSpeed, maxAcceleration);
        manageComponentsVisibility(flock.getSceneController());
        updateBallsInPerceptionRadiusMap();
    }

    private void manageComponentsVisibility(MainSceneController ms) {
        double maxSpeed = ms.getMaxSpeedSlider().getValue();
        final int minSpeedLength = 300;
        updateVisibleVector(visibleVelocityVector, velocity, maxSpeed >= minSpeedLength ? maxSpeed : minSpeedLength);
        updateVisibleVector(visibleAccelerationVector, acceleration, 2000);
        updatePath();
        if (ms.getShowAllPathsButton().isSelected()) path.fadeOut();
        if (ms.getShowConnectionsButton().isSelected()) strokeConnections();
        else getChildren().removeIf(n -> n instanceof Connection);
    }

    private void updateVisibleVector(Line line, Point2D vector, double correction) {
        Point2D begin = getCenterPosition();
        Point2D end = begin.add(vector);
        Point2D unitVector = end.subtract(begin).normalize();
        Point2D radiusInVectorDir = unitVector.multiply(body.getRadius() - line.getStrokeWidth());
        begin = begin.add(radiusInVectorDir);
        end = begin.add(unitVector.multiply(MAX_VECTOR_LENGTH * vector.magnitude() / correction));
        double visibleVectorMagnitude = end.subtract(begin).magnitude();
        if (visibleVectorMagnitude > MAX_VECTOR_LENGTH) end = begin.add(unitVector.multiply(MAX_VECTOR_LENGTH));
        line.setEndX(end.getX());
        line.setEndY(end.getY());
    }

    private void strokeConnections() {
        perceptionRadiusMap.forEach((otherBall, lineToOther) -> {
            double distance = otherBall.getCenterPosition().subtract(this.getCenterPosition()).magnitude();
            lineToOther.setStroke(this.body.getFill());
            lineToOther.setStrokeWidth(LINE_STROKE_WIDTH);
            lineToOther.setDisable(true); // ignores user input
            lineToOther.setOpacity(1 - distance / this.perceptionCircle.getRadius());
            lineToOther.setStartX(this.body.getCenterX());
            lineToOther.setStartY(this.body.getCenterY());
            lineToOther.setEndX(otherBall.getBody().getCenterX());
            lineToOther.setEndY(otherBall.getBody().getCenterY());
            if (!this.getChildren().contains(lineToOther)) this.getChildren().add(lineToOther);
        });
    }

    private void updatePath() {
        path.addLine(getCenterPosition(), prevCenterPosition);
        if (path.getElements().size() >= MAX_PATH_SIZE) path.removeLine(0);
    }

    private void updateBallsInPerceptionRadiusMap() {
        Flock flock = (Flock) getParent();
        flock.getChildrenUnmodifiable().stream().filter(node -> !node.equals(this)).map(node -> (Boid) node).forEach(boid -> {
            double distance = boid.getCenterPosition().subtract(this.getCenterPosition()).magnitude();
            if (distance < perceptionCircle.getRadius()) {
                if (!perceptionRadiusMap.containsKey(boid)) perceptionRadiusMap.put(boid, new Connection());
            } else {
                Line lineToOther = perceptionRadiusMap.get(boid);
                this.getChildren().remove(lineToOther);
                perceptionRadiusMap.remove(boid);
            }
        });
    }

    public Point2D addFriction(double frictionFactor) {
        Point2D decelerationDir = velocity.multiply(-1);
        return decelerationDir.multiply(frictionFactor);
    }

    private void updatePositionAndVelocityBasedOnAcceleration(Duration deltaT, double maxSpeed, double maxAcceleration) {
        Point2D position = getCenterPosition();
        double deltaTSeconds = deltaT.toSeconds();
        acceleration = limit(maxAcceleration, acceleration);
        velocity = velocity.add(acceleration.multiply(deltaTSeconds));
        velocity = limit(maxSpeed, velocity);
        prevCenterPosition = position;
        position = position.add(velocity.multiply(deltaTSeconds));
        this.setCenterPosition(position.getX(), position.getY());
    }

    Point2D limit(double maxValue, Point2D limitedVector) {
        if (limitedVector.magnitude() > maxValue) limitedVector = limitedVector.normalize().multiply(maxValue);
        return limitedVector;
    }

    public void setSpeedBasedOnMouseDrag(Deque<Point2D> dragPoints, Duration duration) {
        final int speedMultiplier = 3;
        if (!dragPoints.isEmpty()) {
            Point2D last = dragPoints.pop();
            if (!dragPoints.isEmpty()) {
                Point2D secondLast = dragPoints.pop();
                velocity = last.subtract(secondLast).multiply(duration.toMillis() * speedMultiplier);
            }
        }
    }

    public void floatThroughEdges(Dimension2D dimension) {
        double width = dimension.getWidth();
        double height = dimension.getHeight();
        Point2D centerPosition = getCenterPosition();
        if (body.getCenterX() >= width) this.setCenterPosition(0, centerPosition.getY());
        else if (body.getCenterX() <= 0) this.setCenterPosition(width, centerPosition.getY());
        if (body.getCenterY() >= height) this.setCenterPosition(centerPosition.getX(), 0);
        else if (body.getCenterY() <= 0) this.setCenterPosition(centerPosition.getX(), height);
    }

    private Point2D prevCenterPosition = Point2D.ZERO;

    public void bounceOfEdges(Dimension2D dimension) {
        double width = dimension.getWidth();
        double height = dimension.getHeight();
        Bounds bounds = body.getBoundsInLocal();
        Point2D centerPosition = getCenterPosition();
        if (bounds.getMinX() <= 0 && centerPosition.getX() < prevCenterPosition.getX())
            velocity = new Point2D(-velocity.getX(), velocity.getY());
        if (bounds.getMinY() <= 0 && centerPosition.getY() < prevCenterPosition.getY())
            velocity = new Point2D(velocity.getX(), -velocity.getY());
        if (bounds.getMaxX() >= width && centerPosition.getX() > prevCenterPosition.getX())
            velocity = new Point2D(-velocity.getX(), velocity.getY());
        if (bounds.getMaxY() >= height && centerPosition.getY() > prevCenterPosition.getY())
            velocity = new Point2D(velocity.getX(), -velocity.getY());
    }

    private double getMassByDensityAndRadius() {
        double volume = 4 * Math.PI * Math.pow(body.getRadius(), 3) / 3;
        return densityMaterial * volume;
    }

    public String getName() {
        return name;
    }

    public double getMass() {
        return getMassByDensityAndRadius();
    }

    public Point2D getCenterPosition() {
        return new Point2D(body.getCenterX(), body.getCenterY());
    }

    public void setCenterPosition(Point2D point2D) {
        body.setCenterX(point2D.getX());
        body.setCenterY(point2D.getY());
    }

    public void setCenterPosition(double x, double y) {
        body.setCenterX(x);
        body.setCenterY(y);
    }

    public void addKeyControlForAcceleration() {
        addKeyControlForAcceleration(KeyCode.W, KeyCode.S, KeyCode.A, KeyCode.D);
    }

    private double maxAcceleration;
    private boolean upPressed;
    private boolean dPressed;
    private boolean lPressed;
    private boolean rPressed;

    private EventHandler<KeyEvent> keyPressed;
    private EventHandler<KeyEvent> keyReleased;

    public void addKeyControlForAcceleration(KeyCode up, KeyCode down, KeyCode left, KeyCode right) {
        keyPressed = keyPressed(up, down, left, right);
        keyReleased = keyReleased(up, down, left, right);
        Scene scene = ((Flock) getParent()).getSceneController().getScene();
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, keyReleased);
    }

    private Point2D userInputAcceleration = Point2D.ZERO;

    private static final Point2D X_POS_DIR = new Point2D(1, 0);
    private static final Point2D Y_POS_DIR = new Point2D(0, 1);

    private EventHandler<KeyEvent> keyPressed(KeyCode up, KeyCode down, KeyCode left, KeyCode right) {
        return key -> {
            if (key.getCode() == right && !rPressed) rPressed = pressedAction(X_POS_DIR.multiply(maxAcceleration));
            if (key.getCode() == left && !lPressed) lPressed = pressedAction(X_POS_DIR.multiply(-maxAcceleration));
            if (key.getCode() == down && !dPressed) dPressed = pressedAction(Y_POS_DIR.multiply(maxAcceleration));
            if (key.getCode() == up && !upPressed) upPressed = pressedAction(Y_POS_DIR.multiply(-maxAcceleration));
        };
    }

    private boolean pressedAction(Point2D vector) {
        userInputAcceleration = userInputAcceleration.add(vector);
        return true;
    }

    private EventHandler<KeyEvent> keyReleased(KeyCode up, KeyCode down, KeyCode left, KeyCode right) {
        return key -> {
            if (key.getCode() == right) rPressed = releaseButtonAction(X_POS_DIR.multiply(-maxAcceleration));
            if (key.getCode() == left) lPressed = releaseButtonAction(X_POS_DIR.multiply(maxAcceleration));
            if (key.getCode() == down) dPressed = releaseButtonAction(Y_POS_DIR.multiply(-maxAcceleration));
            if (key.getCode() == up) upPressed = releaseButtonAction(Y_POS_DIR.multiply(maxAcceleration));
            if (!upPressed && !dPressed && !lPressed && !rPressed) userInputAcceleration = Point2D.ZERO;
        };
    }

    private boolean releaseButtonAction(Point2D vector) {
        userInputAcceleration = userInputAcceleration.add(vector);
        return false;
    }

    public void removeKeyControlsForAcceleration() {
        upPressed = dPressed = lPressed = rPressed = false;
        Scene scene = ((Flock) getParent()).getSceneController().getScene();
        scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
        scene.removeEventFilter(KeyEvent.KEY_RELEASED, keyReleased);
    }

    public void updatePaint(Paint paint) {
        body.setFill(paint);
        perceptionCircle.setStroke(paint);
        repelCircle.setStroke(paint);
        visibleVelocityVector.setStroke(paint);
        visibleAccelerationVector.setStroke(paint);
        path.setStroke(paint);
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
