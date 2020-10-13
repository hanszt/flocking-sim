package hzt.model.entity;

import hzt.controller.AnimationService;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Stack;

import static hzt.controller.main_scene.MainSceneController.UNIFORM_FLOCK;
import static hzt.controller.utils.RandomGenerator.*;

// extends a Pane so it is resizable, so its size is set by its parent, which essentially determine its bounds.
public class Flock extends Pane {

    public static final int MIN_RADIUS = 3, MAX_RADIUS = 10;
    static final int MAX_PATH_SIZE = 200;
    static final int MAX_VISIBLE_VECTOR_LENGTH = 100;
    public static final double MAX_VISIBLE_SPEED_VECTOR_LENGTH = 300, MAX_VISIBLE_ACCELERATION_VECTOR_LENGTH = 1000;
    public static final Color INIT_UNIFORM_BALL_COLOR = Color.ORANGE, INIT_SELECTED_BALL_COLOR = Color.RED;

    private final AnimationService as;

    private Ball2D selectedBall;
    double perceptionRadiusRatio;
    private boolean showConnections, showPerceptionCircle, showVelocityVector, showAccelerationVector, showPath;
    private double maxBallSize = MAX_RADIUS;
    private Color uniformBallColor = INIT_UNIFORM_BALL_COLOR;
    private Color selectedBallColor = INIT_SELECTED_BALL_COLOR;
    private String flockType, physicsEngine;

    public Flock(AnimationService as) {
        //Size is zero so it does not influence the rest of the layout when balls are moved
        this.setMinSize(0, 0);
        this.setMaxSize(0, 0);
        this.as = as;
    }

    public void controlFlockSize(int numberOfBalls, Dimension2D parentDimension) {
        while (this.getChildren().size() != numberOfBalls) {
            if (this.getChildren().size() < numberOfBalls) {
                Ball2D ball2D = createBall();
                ball2D.setCenterPosition(getRandomPositionOnParent(parentDimension.getWidth(), parentDimension.getHeight()));
                ball2D.setPerceptionRadius(ball2D.getBody().getRadius() * perceptionRadiusRatio);
                addMouseFunctionality(ball2D);
                this.getChildren().add(ball2D);
                ball2D.addComponents();
            } else {
                Node ball2D = this.getChildren().get(0);
                this.getChildren().remove(ball2D);
                if (ball2D.equals(selectedBall)) selectedBall = null;
            }
        }
    }

    private Ball2D createBall() {
        if (flockType.equals(UNIFORM_FLOCK)) return new Ball2D(maxBallSize, uniformBallColor);
        else return new Ball2D(getRandomDouble(MIN_RADIUS, maxBallSize), getRandomColor());
    }

    public void addMouseFunctionality(Ball2D ball) {
        ball.getBody().setOnMousePressed(onMousePressed(ball));
        Stack<Point2D> dragPoints = new Stack<>();
        ball.getBody().setOnMouseDragged(onMouseDragged(ball, dragPoints));
        ball.getBody().setOnMouseReleased(e -> ball.setSpeedBasedOnMouseDrag(dragPoints, as.getTimeline().getCycleDuration()));
    }

    private EventHandler<MouseEvent> onMousePressed(Ball2D ball) {
        return mouseEvent -> {
            ball.updatePaint(selectedBallColor);
            ball.setCenterPosition(mouseEvent.getX(), mouseEvent.getY());
            ball.setVelocity(Point2D.ZERO);
            ball.getPath().setVisible(showPath);
            if (!ball.equals(selectedBall)) {
                ball.addKeyControlForAcceleration();
                if (selectedBall != null) {
                    selectedBall.removeKeyControlsForAcceleration();
                    selectedBall.getPath().setPathVisible(false);
                    selectedBall.updatePaint(selectedBall.getInitPaint());
                }
                selectedBall = ball;
            }
        };
    }

    private EventHandler<MouseEvent> onMouseDragged(Ball2D ball, Stack<Point2D> dragPoints) {
        return e -> {
            ball.getBody().setCenterX(e.getX());
            ball.getBody().setCenterY(e.getY());
            dragPoints.add(ball.getCenterPosition());
        };
    }

    public Ball2D getSelectedBall() {
        return selectedBall;
    }

    public void setPerceptionRadiusRatio(double perceptionRadiusRatio) {
        this.perceptionRadiusRatio = perceptionRadiusRatio;
    }

    public boolean isShowConnections() {
        return showConnections;
    }

    public void setShowConnections(boolean showConnections) {
        this.showConnections = showConnections;
    }

    public boolean isShowPerceptionCircle() {
        return showPerceptionCircle;
    }

    public void setShowPerceptionCircle(boolean showPerceptionCircle) {
        this.showPerceptionCircle = showPerceptionCircle;
    }

    public boolean isShowVelocityVector() {
        return showVelocityVector;
    }

    public void setShowVelocityVector(boolean showVelocityVector) {
        this.showVelocityVector = showVelocityVector;
    }

    public boolean isShowAccelerationVector() {
        return showAccelerationVector;
    }

    public void setShowAccelerationVector(boolean showAccelerationVector) {
        this.showAccelerationVector = showAccelerationVector;
    }

    public void setMaxBallSize(double maxBallSize) {
        this.maxBallSize = maxBallSize;
    }

    public void setFlockType(String flockType) {
        this.flockType = flockType;
    }

    public void setSelectedBallColor(Color selectedBallColor) {
        this.selectedBallColor = selectedBallColor;
    }

    public void setUniformBallColor(Color uniformBallColor) {
        this.uniformBallColor = uniformBallColor;
    }

    public void setShowPath(boolean showPath) {
        this.showPath = showPath;
    }

    public String getPhysicsEngine() {
        return physicsEngine;
    }

    public void setPhysicsEngine(String physicsEngine) {
        this.physicsEngine = physicsEngine;
    }
}
