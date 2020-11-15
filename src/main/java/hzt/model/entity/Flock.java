package hzt.model.entity;

import hzt.controller.MainSceneController;
import hzt.model.utils.Engine;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.Collectors;

import static hzt.model.utils.RandomGenerator.*;

// extends a Pane so it is resizable, so its size is set by its parent, which essentially determine its bounds.
@Getter
@Setter
public class Flock extends Group implements Iterable<Boid> {

    public static final int MIN_RADIUS = 3;
    public static final int MAX_RADIUS = 10;
    static final int MAX_PATH_SIZE_ALL = 200;
    static final int MAX_PATH_SIZE = 50;
    static final int MAX_VECTOR_LENGTH = 80;
    public static final Color INIT_UNIFORM_BALL_COLOR = Color.ORANGE;
    public static final Color INIT_SELECTED_BALL_COLOR = Color.RED;

    private final MainSceneController sceneController;

    private Boid selectedBall;
    private Color uniformBallColor = INIT_UNIFORM_BALL_COLOR;
    private Color selectedBallColor = INIT_SELECTED_BALL_COLOR;
    private FlockType flockType;
    private Engine.FlockingSim flockingSim;

    public Flock(MainSceneController mainSceneController) {
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
        Boid boid = flockType.createBall();
        boid.setCenterPosition(getRandomPositionOnParent(parentDimension.getWidth(), parentDimension.getHeight()));
        boid.setPerceptionRadius(boid.getBody().getRadius() * perceptionRadiusRatio);
        boid.setRepelRadius(boid.getBody().getRadius() * repelRadiusRatio);
        sceneController.setBallParams(boid);
        addMouseFunctionality(boid);
        this.getChildren().add(boid);
    }

    private void removeBallFromFLock() {
        ObservableList<Node> list = this.getChildren();
        Boid boid = (Boid) list.get(0);
        this.getChildren().remove(boid);
        this.getChildren().stream().map(n -> (Boid) n).forEach(ball -> {
            ball.getPerceptionRadiusMap().remove(boid);
            ball.getChildren().removeIf(n -> n instanceof Connection);
        });
        if (boid.equals(selectedBall)) {
            selectedBall = !list.isEmpty() ? getRandomSelectedBall() : null;
        }
    }

    public Boid getRandomSelectedBall() {
        Boid ball = (Boid) this.getChildren().get(new Random().nextInt(getChildren().size()));
        ball.updatePaint(selectedBallColor);
        ball.addKeyControlForAcceleration();
        ball.getPath().setVisible(sceneController.getShowPathSelectedButton().isSelected());
        ball.getPerceptionCircle().setVisible(sceneController.getShowPerceptionSelectedBallButton().isSelected());
        return ball;
    }

    @NotNull
    @Override
    public Iterator<Boid> iterator() {
        return getChildren().stream()
                .filter(n -> n instanceof Boid)
                .map(n -> (Boid) n)
                .collect(Collectors.toList()).iterator();
    }

    public abstract static class FlockType {
        abstract Boid createBall();

        @Override
        public abstract String toString();
    }

    private final FlockType random = new FlockType() {
        @Override
        Boid createBall() {
            return new Boid(getRandomDouble(MIN_RADIUS, sceneController.getMaxBallSizeSlider().getValue()), getRandomColor());
        }

        @Override
        public String toString() {
            return "Random flock";
        }
    };

    private final FlockType uniform = new FlockType() {
        @Override
        Boid createBall() {
            return new Boid(sceneController.getMaxBallSizeSlider().getValue(), uniformBallColor);
        }

        @Override
        public String toString() {
            return "Uniform Flock";
        }
    };

    public void addMouseFunctionality(Boid ball) {
        Duration frameDuration = sceneController.getAnimationService().getTimeline().getCycleDuration();
        Deque<Point2D> dragPoints = new ArrayDeque<>();
        ball.getBody().setOnMousePressed(onMousePressed(ball));
        ball.getBody().setOnMouseDragged(onMouseDragged(ball, dragPoints));
        ball.getBody().setOnMouseReleased(e -> ball.setSpeedBasedOnMouseDrag(dragPoints, frameDuration));
    }

    private EventHandler<MouseEvent> onMousePressed(Boid ball) {
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

    private EventHandler<MouseEvent> onMouseDragged(Boid ball, Deque<Point2D> dragPoints) {
        return e -> {
            ball.getBody().setCenterX(e.getX());
            ball.getBody().setCenterY(e.getY());
            dragPoints.add(ball.getCenterPosition());
        };
    }

    public FlockType getRandom() {
        return random;
    }
}
