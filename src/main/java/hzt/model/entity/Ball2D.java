package hzt.model.entity;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

import java.util.*;

import static hzt.controller.utils.PhysicsEngine.*;
import static hzt.controller.utils.RandomGenerator.getRandomColor;
import static hzt.controller.utils.RandomGenerator.getRandomDouble;
import static javafx.scene.paint.Color.TRANSPARENT;

public class Ball2D extends Group {

    public static final int MIN_RADIUS = 20, MAX_RADIUS = 100;

    private static int next = 0;

    private final String name;
    private final Circle body;
    private final Circle perceptionCircle;
    private final Map<Ball2D, Line> ballsInPerceptionRadiusMap;

    private double densityMaterial; // kg
    private Point2D velocity = Point2D.ZERO; // m/s
    private Point2D acceleration = Point2D.ZERO; // m/s^2
    private Point2D forceResultant = Point2D.ZERO; // m/s^2
    private double keyPressedAccIncrement;
    private boolean showConnections;

    public Ball2D(String name, double radius, Paint paint) {
        this.name = name;
        this.body = new Circle(radius, paint); // m
        this.perceptionCircle = new Circle();
        this.densityMaterial = DENSITY_IRON;
        ballsInPerceptionRadiusMap = new HashMap<>();
        perceptionCircle.setStrokeType(StrokeType.OUTSIDE);
        perceptionCircle.setDisable(true); //ignores user input
        perceptionCircle.setStroke(paint);
        perceptionCircle.setFill(TRANSPARENT);
        perceptionCircle.centerXProperty().bind(body.centerXProperty());
        perceptionCircle.centerYProperty().bind(body.centerYProperty());
        super.getChildren().addAll(perceptionCircle, body);
    }

    private double getMassByDensityAndRadius() {
        double volume = 4 * Math.PI * Math.pow(body.getRadius(), 3) / 3;
        return densityMaterial * volume;
    }

    public Ball2D(double radius, Paint paint) {
        this("Ball" + ++next, radius, paint);
    }

    public Ball2D() {
        this("Ball" + ++next, getRandomDouble(MIN_RADIUS, MAX_RADIUS), getRandomColor());
    }

    public Point2D getResultantForce() {
        return acceleration.multiply(getMassByDensityAndRadius());
    }

    public String getName() {
        return name;
    }

    public double getMass() {
        return getMassByDensityAndRadius();
    }

    public void setDensityMaterial(double densityMaterial) {
        this.densityMaterial = densityMaterial;
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

    private EventHandler<KeyEvent> keyPressed;
    private EventHandler<KeyEvent> keyReleased;

    public void addKeyControlForAcceleration() {
        addKeyControlForAcceleration(KeyCode.W, KeyCode.S, KeyCode.A, KeyCode.D);
    }

    public void addKeyControlForAcceleration(KeyCode up, KeyCode down, KeyCode left, KeyCode right) {
        keyPressed = key -> {
            if (key.getCode() == down) acceleration = new Point2D(acceleration.getX(), keyPressedAccIncrement);
            if (key.getCode() == left) acceleration = new Point2D(-keyPressedAccIncrement, acceleration.getY());
            if (key.getCode() == up) acceleration = new Point2D(acceleration.getX(), -keyPressedAccIncrement);
            if (key.getCode() == right) acceleration = new Point2D(keyPressedAccIncrement, acceleration.getY());
//            acceleration = acceleration.normalize().multiply(accIncrement);
        };
        keyReleased = key -> {
            if (key.getCode() == down) acceleration = new Point2D(acceleration.getX(), 0);
            if (key.getCode() == left) acceleration = new Point2D(0, acceleration.getY());
            if (key.getCode() == up) acceleration = new Point2D(acceleration.getX(), 0);
            if (key.getCode() == right) acceleration = new Point2D(0, acceleration.getY());
        };
        this.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
        this.getScene().addEventFilter(KeyEvent.KEY_RELEASED, keyReleased);
    }

    public void removeKeyControlsForAcceleration() {
        this.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
        this.getScene().removeEventFilter(KeyEvent.KEY_RELEASED, keyReleased);
    }

    public void update(Duration deltaT, double accelerationMultiplier) {
        keyPressedAccIncrement = accelerationMultiplier / deltaT.toSeconds();
        updatePositionAndVelocityBasedOnAcceleration(deltaT);
        updateBallsInPerceptionRadiusMap();
        if (showConnections) strokeConnections();
    }

    private void strokeConnections() {
        for (Map.Entry<Ball2D, Line> other : ballsInPerceptionRadiusMap.entrySet()) {
            Ball2D otherBall = other.getKey();
            Line lineToOther = other.getValue();
            double distance = otherBall.getCenterPosition().subtract(this.getCenterPosition()).magnitude();
            lineToOther.setStroke(this.body.getFill());
            lineToOther.setDisable(true); // ignores user input
            lineToOther.setOpacity(1 - distance / this.perceptionCircle.getRadius());
            lineToOther.setStartX(this.body.getCenterX());
            lineToOther.setStartY(this.body.getCenterY());
            lineToOther.setEndX(otherBall.getBody().getCenterX());
            lineToOther.setEndY(otherBall.getBody().getCenterY());
            if (!this.getChildren().contains(lineToOther)) this.getChildren().add(lineToOther);
        }
    }

    private void updateBallsInPerceptionRadiusMap() {
        ObservableList<Node> allBalls = this.getParent().getChildrenUnmodifiable();
        for (Node node : allBalls) {
            if (!node.equals(this)) {
                Ball2D ball2D = (Ball2D) node;
                double distance = ball2D.getCenterPosition().subtract(this.getCenterPosition()).magnitude();
                if (distance <= perceptionCircle.getRadius()) {
                    if (!ballsInPerceptionRadiusMap.containsKey(ball2D)) {
                        ballsInPerceptionRadiusMap.put(ball2D, new Line());
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

    private void updatePositionAndVelocityBasedOnAcceleration(Duration deltaT) {
        Point2D position = getCenterPosition();
        double deltaTSeconds = deltaT.toSeconds();
        velocity = velocity.add(acceleration.multiply(deltaTSeconds));
        position = position.add(velocity.multiply(deltaTSeconds));
        setCenterPosition(position);
//        System.out.printf("position: %2.3f\nvelocity: %2.3f\nacceleration: %2.3f\ndeltaT: %2.3f seconds\n\n",
//                position.magnitude(), velocity.magnitude(), acceleration.magnitude(), deltaT.toSeconds());
    }

    public void setSpeedBasedDrag(Stack<Point2D> dragPoints, Duration duration) {
        try {
            final int multiplier = 3;
            Point2D last = dragPoints.pop();
            Point2D secondLast = dragPoints.pop();
            velocity = last.subtract(secondLast).multiply(duration.toMillis() * multiplier);
//            System.out.println(last);
//            System.out.println(secondLast);
//            System.out.println(velocity);
        } catch (EmptyStackException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setColor(Color color) {
        body.setFill(color);
        perceptionCircle.setStroke(color);
    }

    public Circle getBody() {
        return body;
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

    public void setAcceleration(Point2D acceleration) {
        this.acceleration = acceleration;
    }

    public Point2D getForceResultant() {
        return forceResultant;
    }

    public void setForceResultant(Point2D forceResultant) {
        this.forceResultant = forceResultant;
    }

    public Circle getPerceptionCircle() {
        return perceptionCircle;
    }

    public double getPerceptionRadius() {
        return perceptionCircle.getRadius();
    }

    public void setPerceptionRadius(double radius) {
        this.perceptionCircle.setRadius(radius);
    }

    public Map<Ball2D, Line> getBallsInPerceptionRadiusMap() {
        return ballsInPerceptionRadiusMap;
    }

    public void setShowConnections(boolean showConnections) {
        this.showConnections = showConnections;
    }


    @Override
    public String toString() {
        return "Ball2D{" +
                "acceleration=" + acceleration +
                ", body=" + body +
                ", densityMaterial=" + densityMaterial +
                ", name='" + name + '\'' +
                ", velocity=" + velocity +
                '}';
    }
}
