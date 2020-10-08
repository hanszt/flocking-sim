package hzt.model.entity;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.EmptyStackException;
import java.util.Stack;

public class Ball2D extends Circle {

    private String name;
    private double mass; // kg
    private Point2D velocity = Point2D.ZERO; // m/s
    private Point2D acceleration = Point2D.ZERO; // m/s^2

    private Point2D forceResultant = Point2D.ZERO; // m/s^2
    private double accIncrement;

    public Ball2D() {
    }

    public Ball2D(double radius) {
        super(radius);
    }

    public Ball2D(String name, double radius) {
        super(radius);
        this.name = name;
    }

    public Ball2D(double radius, Paint paint) {
        super(radius, paint);
    }


    public Ball2D(Point2D center, double radius) {
        super(center.getX(), center.getY(), radius);
    }

    public Ball2D(Point2D center, double radius, Paint paint) {
        super(center.getX(), center.getY(), radius, paint);
    }

    public Point2D getResultantForce() {
        return acceleration.multiply(mass);
    }

    public String getName() {
        return name;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public Point2D getCenterPosition() {
        return new Point2D(super.getCenterX(), super.getCenterY());
    }

    public void setCenterPosition(Point2D centerPosition) {
        setCenterX(centerPosition.getX());
        setCenterY(centerPosition.getY());
    }

    private EventHandler<KeyEvent> keyPressed;
    private EventHandler<KeyEvent> keyReleased;

    public void addKeyControlForAcceleration(Scene scene) {
        addKeyControlForAcceleration(scene, KeyCode.W, KeyCode.S, KeyCode.A, KeyCode.D);
    }

    public void addKeyControlForAcceleration(Scene scene, KeyCode up, KeyCode down, KeyCode left, KeyCode right) {
        keyPressed = key -> {
            if (key.getCode() == down) acceleration = new Point2D(acceleration.getX(), accIncrement);
            if (key.getCode() == left) acceleration = new Point2D(-accIncrement, acceleration.getY());
            if (key.getCode() == up) acceleration = new Point2D(acceleration.getX(), -accIncrement);
            if (key.getCode() == right) acceleration = new Point2D(accIncrement, acceleration.getY());
//            acceleration = acceleration.normalize().multiply(accIncrement);
        };
        keyReleased = key -> {
            if (key.getCode() == down) acceleration = new Point2D(acceleration.getX(), 0);
            if (key.getCode() == left) acceleration = new Point2D(0, acceleration.getY());
            if (key.getCode() == up) acceleration = new Point2D(acceleration.getX(), 0);
            if (key.getCode() == right) acceleration = new Point2D(0, acceleration.getY());
        };
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, keyReleased);
    }

    public void removeKeyControlsForAcceleration(Scene scene) {
        scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
        scene.removeEventFilter(KeyEvent.KEY_RELEASED, keyReleased);
    }

    public void update(Duration deltaT) {
        accIncrement = 5 / deltaT.toSeconds();
        updatePositionAndVelocityBasedOnAcceleration(deltaT);
        addFriction(0.01);
    }

    private void addFriction(double frictionFactor) {
        velocity = velocity.multiply(1 - frictionFactor);
    }

    private void updatePositionAndVelocityBasedOnAcceleration(Duration deltaT) {
        Point2D position = getCenterPosition();
        velocity = velocity.add(acceleration.multiply(deltaT.toSeconds()));
        position = position.add(velocity.multiply(deltaT.toSeconds()));
        setCenterPosition(position);
//        System.out.printf("position: %2.3f\nvelocity: %2.3f\nacceleration: %2.3f\ndeltaT: %2.3f seconds\n\n",
//                position.magnitude(), velocity.magnitude(), acceleration.magnitude(), deltaT.toSeconds());
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


    public void setSpeedBasedDrag(Stack<Point2D> dragPoints, Duration duration) {
        try {
            Point2D last = dragPoints.pop();
            Point2D secondLast = dragPoints.pop();
            velocity = last.subtract(secondLast).multiply(duration.toMillis() * 2);
//            System.out.println(last);
//            System.out.println(secondLast);
//            System.out.println(velocity);
        } catch (EmptyStackException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Ball{" +
                "centerPosition=" + getCenterPosition() +
                "acceleration=" + acceleration +
                ", mass=" + mass +
                ", name='" + name + '\'' +
                ", velocity=" + velocity +
                '}';
    }

}
