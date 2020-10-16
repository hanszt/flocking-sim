package hzt.model.entity;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static hzt.controller.AnimationService.LINE_STROKE_WIDTH;
import static hzt.controller.utils.Engine.*;
import static hzt.model.entity.Flock.*;
import static javafx.scene.paint.Color.TRANSPARENT;

@ToString
public class Ball2D extends Group {

    private static int next = 0;

    private final String name;
    private final Circle body;
    private final Circle perceptionCircle;
    private final Circle repelDistanceCircle;
    private final VisibleVector visibleAccelerationVector;
    private final VisibleVector visibleVelocityVector;
    private final Path path;
    private final Map<Ball2D, Connection> ballsInPerceptionRadiusMap;
    private final Paint initPaint;

    private final double densityMaterial; // kg
    private Point2D velocity; // pixel/s
    private Point2D acceleration; // pixel/s^2

    public Ball2D(String name, double radius, Paint initPaint) {
        this.name = name;
        this.initPaint = initPaint;
        this.body = new Circle(radius);
        this.perceptionCircle = new Circle();
        this.repelDistanceCircle = new Circle();
        this.visibleVelocityVector = new VisibleVector();
        this.visibleAccelerationVector = new VisibleVector();
        this.path = new Path();
        this.densityMaterial = DENSITY;
        this.velocity = Point2D.ZERO;
        this.acceleration = Point2D.ZERO;
        this.ballsInPerceptionRadiusMap = new HashMap<>();
        configureComponents();
        super.getChildren().addAll(body, perceptionCircle, repelDistanceCircle, visibleVelocityVector, visibleAccelerationVector, path);
    }

    private void configureComponents() {
        configureCircle(perceptionCircle);
        configureCircle(repelDistanceCircle);
        configureLine(visibleVelocityVector);
        configureLine(visibleAccelerationVector);
        updatePaint(initPaint);
        path.setPathVisible(false);
        visibleAccelerationVector.getStrokeDashArray().addAll(4., 4.);
    }

    private void configureCircle(Circle circle) {
        circle.setStrokeType(StrokeType.OUTSIDE);
        circle.setDisable(true); //ignores user input
        circle.setFill(TRANSPARENT);
        circle.centerXProperty().bind(body.centerXProperty());
        circle.centerYProperty().bind(body.centerYProperty());
    }

    private void configureLine(Line line) {
        line.setStrokeWidth(LINE_STROKE_WIDTH);
        line.startXProperty().bind(body.centerXProperty());
        line.startYProperty().bind(body.centerYProperty());
    }

    public Ball2D(double radius, Paint paint) {
        this("Ball" + ++next, radius, paint);
    }

    private Point2D prevAttractionComponent = Point2D.ZERO;

    public void update(Duration deltaT, double accelerationMultiplier, double maxSpeed) {
        Flock flock = (Flock) this.getParent();
        keyPressedAccIncrement = accelerationMultiplier / deltaT.toSeconds();
        Set<Ball2D> ballsSet = ballsInPerceptionRadiusMap.keySet();
        Point2D physicsEngineAcceleration = flock.getFlockingSim().getTotalAcceleration(this, ballsSet);
        prevAttractionComponent = addComponentToAcceleration(physicsEngineAcceleration, prevAttractionComponent);
        updateBallsInPerceptionRadiusMap();
        updateVisibleVector(visibleVelocityVector, velocity, MAX_VISIBLE_SPEED_VECTOR_LENGTH);
        updateVisibleVector(visibleAccelerationVector, acceleration, MAX_VISIBLE_ACCELERATION_VECTOR_LENGTH);
        updatePath();
        perceptionCircle.setVisible(flock.isShowPerceptionCircle());
        repelDistanceCircle.setVisible(flock.isShowRepelCircle());
        visibleVelocityVector.setVisible(flock.isShowVelocityVector());
        visibleAccelerationVector.setVisible(flock.isShowAccelerationVector());
        if (flock.isShowConnections()) strokeConnections();
        else getChildren().removeIf(n -> n instanceof Connection);
        updatePositionAndVelocityBasedOnAcceleration(deltaT, maxSpeed);
    }

    private void updateVisibleVector(Line line, Point2D vector, double maxMagnitude) {
        Point2D begin = getCenterPosition();
        Point2D end = begin.add(vector);
        end = begin.add(end.subtract(begin).normalize().multiply(MAX_VISIBLE_VECTOR_LENGTH * vector.magnitude() / maxMagnitude));
        double visibleVectorMagnitude = end.subtract(begin).magnitude();
        if (visibleVectorMagnitude > MAX_VISIBLE_VECTOR_LENGTH) {
            end = begin.add(end.subtract(begin).normalize().multiply(MAX_VISIBLE_VECTOR_LENGTH));
        }
        line.setEndX(end.getX());
        line.setEndY(end.getY());
    }

    private void strokeConnections() {
        for (Map.Entry<Ball2D, Connection> other : ballsInPerceptionRadiusMap.entrySet()) {
            Ball2D otherBall = other.getKey();
            Line lineToOther = other.getValue();
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
        }
    }

    private void updatePath() {
        int size = path.getElements().size();
        path.addLine(getCenterPosition(), prevCenterPosition);
        if (size >= MAX_PATH_SIZE) path.removeLine(0);
    }

    private void updateBallsInPerceptionRadiusMap() {
        ObservableList<Node> allBalls = this.getParent().getChildrenUnmodifiable();
        for (Node node : allBalls) {
            if (!node.equals(this)) {
                Ball2D ball2D = (Ball2D) node;
                double distance = ball2D.getCenterPosition().subtract(this.getCenterPosition()).magnitude();
                if (distance < perceptionCircle.getRadius()) {
                    if (!ballsInPerceptionRadiusMap.containsKey(ball2D)) {
                        ballsInPerceptionRadiusMap.put(ball2D, new Connection());
                    }
                } else {
                    Line lineToOther = ballsInPerceptionRadiusMap.get(ball2D);
                    this.getChildren().remove(lineToOther);
                    ballsInPerceptionRadiusMap.remove(ball2D);
                }
            }
        }
    }

    public void addFriction(double frictionFactor) {
        velocity = velocity.multiply(1 - frictionFactor);
    }

    private void updatePositionAndVelocityBasedOnAcceleration(Duration deltaT, double maxSpeed) {
        Point2D position = getCenterPosition();
        double deltaTSeconds = deltaT.toSeconds();
        velocity = velocity.add(acceleration.multiply(deltaTSeconds));
        if (velocity.magnitude() > maxSpeed) velocity = velocity.normalize().multiply(maxSpeed);
        prevCenterPosition = position;
        position = position.add(velocity.multiply(deltaTSeconds));
        setCenterPosition(position);
    }

    public Point2D addComponentToAcceleration(Point2D acceleration, Point2D prevAcceleration) {
        this.acceleration = this.acceleration.add(acceleration.subtract(prevAcceleration));
        return acceleration;
    }

    public void setSpeedBasedOnMouseDrag(Stack<Point2D> dragPoints, Duration duration) {
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
        double width = dimension.getWidth(), height = dimension.getHeight();
        Point2D centerPosition = getCenterPosition();
        if (body.getCenterX() >= width) {
            this.setCenterPosition(new Point2D(0, centerPosition.getY()));
        } else if (body.getCenterX() <= 0) {
            this.setCenterPosition(new Point2D(width, centerPosition.getY()));
        }
        if (body.getCenterY() >= height) {
            this.setCenterPosition(new Point2D(centerPosition.getX(), 0));
        } else if (body.getCenterY() <= 0) {
            this.setCenterPosition(new Point2D(centerPosition.getX(), height));
        }
    }

    private Point2D prevCenterPosition = Point2D.ZERO;

    public void bounceOfEdges(Dimension2D dimension) {
        double width = dimension.getWidth(), height = dimension.getHeight();
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

    public void setCenterPosition(double x, double y) {
        body.setCenterX(x);
        body.setCenterY(y);
    }

    public void setCenterPosition(Point2D centerPosition) {
        setCenterPosition(centerPosition.getX(), centerPosition.getY());
    }

    public void addKeyControlForAcceleration() {
        addKeyControlForAcceleration(KeyCode.W, KeyCode.S, KeyCode.A, KeyCode.D);
    }

    private double keyPressedAccIncrement;
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    private EventHandler<KeyEvent> keyPressed, keyReleased;

    public void addKeyControlForAcceleration(KeyCode up, KeyCode down, KeyCode left, KeyCode right) {
        //           acceleration = acceleration.normalize().multiply(accIncrement);
        keyPressed = keyPressed(up, down, left, right);
        keyReleased = keyReleased(up, down, left, right);
        this.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyPressed(up, down, left, right));
        this.getScene().addEventFilter(KeyEvent.KEY_RELEASED, keyReleased);
    }

    private EventHandler<KeyEvent> keyPressed(KeyCode up, KeyCode down, KeyCode left, KeyCode right) {
       return key -> {
            if (key.getCode() == down && !downPressed) {
                downPressed = true;
                acceleration = acceleration.add(new Point2D(0, keyPressedAccIncrement));
            }
            if (key.getCode() == left && !leftPressed) {
                leftPressed = true;
                acceleration = acceleration.add(new Point2D(-keyPressedAccIncrement, 0));
            }
            if (key.getCode() == up && !upPressed) {
                upPressed = true;
                acceleration = acceleration.add(new Point2D(0, -keyPressedAccIncrement));
            }
            if (key.getCode() == right && !rightPressed) {
                rightPressed = true;
                acceleration = acceleration.add(new Point2D(keyPressedAccIncrement, 0));
            }
        };
    }

    private EventHandler<KeyEvent> keyReleased(KeyCode up, KeyCode down, KeyCode left, KeyCode right) {
        return  key -> {
            if (key.getCode() == down) {
                downPressed = false;
                acceleration = acceleration.subtract(new Point2D(0, keyPressedAccIncrement));
            }
            if (key.getCode() == left) {
                leftPressed = false;
                acceleration = acceleration.subtract(new Point2D(-keyPressedAccIncrement, 0));
            }
            if (key.getCode() == up) {
                upPressed = false;
                acceleration = acceleration.subtract(new Point2D(0, -keyPressedAccIncrement));
            }
            if (key.getCode() == right) {
                rightPressed = false;
                acceleration = acceleration.subtract(new Point2D(keyPressedAccIncrement, 0));
            }
        };
    }

    public void removeKeyControlsForAcceleration() {
        upPressed = downPressed = leftPressed = rightPressed = false;
        this.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
        this.getScene().removeEventFilter(KeyEvent.KEY_RELEASED, keyReleased);
    }

    public void updatePaint(Paint paint) {
        body.setFill(paint);
        perceptionCircle.setStroke(paint);
        repelDistanceCircle.setStroke(paint);
        visibleVelocityVector.setStroke(paint);
        visibleAccelerationVector.setStroke(paint);
        path.setStroke(paint);
    }

    public Circle getBody() {
        return body;
    }

    public Path getPath() {
        return path;
    }

    public Paint getInitPaint() {
        return initPaint;
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    public Point2D getAcceleration() {
        return acceleration;
    }

    public void setPerceptionRadius(double radius) {
        this.perceptionCircle.setRadius(radius);
    }

    public float getRepelRadius() {
        return (float) repelDistanceCircle.getRadius();
    }

    public void setRepelRadius(double radius) {
        this.repelDistanceCircle.setRadius(radius);
    }

    public Map<Ball2D, Connection> getBallsInPerceptionRadiusMap() {
        return ballsInPerceptionRadiusMap;
    }

}
