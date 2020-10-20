package hzt.model.entity;

import hzt.controller.main_scene.MainSceneController;
import hzt.controller.utils.Engine;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.util.Stack;

import static hzt.controller.utils.RandomGenerator.*;

// extends a Pane so it is resizable, so its size is set by its parent, which essentially determine its bounds.
@Getter
@Setter
public class Flock extends Pane {

    public static final int MIN_RADIUS = 3, MAX_RADIUS = 10;
    static final int MAX_PATH_SIZE_ALL = 200, MAX_PATH_SIZE = 50;
    static final int MAX_VISIBLE_VECTOR_LENGTH = 100;
    public static final double MAX_VISIBLE_SPEED_VECTOR_LENGTH = 300, MAX_VISIBLE_ACCELERATION_VECTOR_LENGTH = 1000;
    public static final Color INIT_UNIFORM_BALL_COLOR = Color.ORANGE, INIT_SELECTED_BALL_COLOR = Color.RED;

    private final MainSceneController sceneController;

    private Ball2D selectedBall;
    private Color uniformBallColor = INIT_UNIFORM_BALL_COLOR, selectedBallColor = INIT_SELECTED_BALL_COLOR;
    private FlockType flockType;
    private Engine.FlockingSim flockingSim;

    public Flock(MainSceneController mainSceneController) {
        //Size is zero so it does not influence the rest of the layout when balls are moved
        this.setMaxSize(0, 0);
        this.sceneController = mainSceneController;
    }

    public void controlFlockSize(int numberOfBalls, Dimension2D parentDimension) {
        while (this.getChildren().size() != numberOfBalls) {
            if (this.getChildren().size() < numberOfBalls) addBallToFlock(parentDimension);
            else removeBallFromFLock();
        }
    }

    private void addBallToFlock(Dimension2D parentDimension) {
        double perceptionRadiusRatio = sceneController.getPerceptionRadiusSlider().getValue();
        double repelRadiusRatio = sceneController.getRepelDistanceSlider().getValue();
        Ball2D ball2D = flockType.createBall();
        ball2D.setCenterPosition(getRandomPositionOnParent(parentDimension.getWidth(), parentDimension.getHeight()));
        ball2D.setPerceptionRadius(ball2D.getBody().getRadius() * perceptionRadiusRatio);
        ball2D.setRepelRadius(ball2D.getBody().getRadius() * repelRadiusRatio);
        sceneController.setBallParams(ball2D);
        addMouseFunctionality(ball2D);
        this.getChildren().add(ball2D);
    }

    private void removeBallFromFLock() {
        Ball2D ball2D = (Ball2D) this.getChildren().get(0);
       this.getChildren().remove(ball2D);
        this.getChildren().stream().map(n -> (Ball2D) n).forEach(ball -> {
            ball.getPerceptionRadiusMap().remove(ball2D);
            ball.getChildren().removeIf(n -> n instanceof Connection);
        });
        if (ball2D.equals(selectedBall)) {
            selectedBall = this.getChildren().size() > 0 ? getRandomNewSelectedBall() : null;
        }
    }

    public Ball2D getRandomNewSelectedBall() {
        Ball2D ball = (Ball2D) this.getChildren().get((int) (Math.random() * getChildren().size()));
        ball.updatePaint(selectedBallColor);
        ball.addKeyControlForAcceleration();
        ball.getPath().setVisible(sceneController.getShowPathSelectedButton().isSelected());
        return ball;
    }

    public abstract static class FlockType {
        abstract Ball2D createBall();

        @Override
        public abstract String toString();
    }

    private final FlockType random = new FlockType() {
        @Override
        Ball2D createBall() {
            return new Ball2D(getRandomDouble(MIN_RADIUS, sceneController.getMaxBallSizeSlider().getValue()), getRandomColor());
        }

        @Override
        public String toString() {
            return "Random flock";
        }
    };

    private final FlockType uniform = new FlockType() {
        @Override
        Ball2D createBall() {
            return new Ball2D(sceneController.getMaxBallSizeSlider().getValue(), uniformBallColor);
        }

        @Override
        public String toString() {
            return "Uniform Flock";
        }
    };

    public void addMouseFunctionality(Ball2D ball) {
        Duration frameDuration = sceneController.getAnimationService().getTimeline().getCycleDuration();
        Stack<Point2D> dragPoints = new Stack<>();
        ball.getBody().setOnMousePressed(onMousePressed(ball));
        ball.getBody().setOnMouseDragged(onMouseDragged(ball, dragPoints));
        ball.getBody().setOnMouseReleased(e -> ball.setSpeedBasedOnMouseDrag(dragPoints, frameDuration));
    }

    private EventHandler<MouseEvent> onMousePressed(Ball2D ball) {
        return mouseEvent -> {
            ball.updatePaint(selectedBallColor);
            ball.setCenterPosition(mouseEvent.getX(), mouseEvent.getY());
            ball.setVelocity(Point2D.ZERO);
            ball.getPath().setVisible(sceneController.getShowPathSelectedButton().isSelected());
            if (!ball.equals(selectedBall)) {
                ball.addKeyControlForAcceleration();
                if (selectedBall != null) {
                    selectedBall.removeKeyControlsForAcceleration();
                    selectedBall.getPath().setVisible(sceneController.getShowAllPathsButton().isSelected());
                    selectedBall.updatePaint(flockType.equals(uniform) ? uniformBallColor : selectedBall.getInitPaint());
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
}
