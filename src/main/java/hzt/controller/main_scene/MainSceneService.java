package hzt.controller.main_scene;

import hzt.controller.AbstractSceneService;
import hzt.controller.services.AnimationService;
import hzt.model.entity.Ball2D;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class MainSceneService extends AbstractSceneService {

    private Ball2D selectedBall;
    private final Set<Ball2D> ballsWithActiveKeyControl = new HashSet<>();
    private final AnimationService as;

    public MainSceneService(AnimationService as) {
        this.as = as;
    }

    public void configureBall(Ball2D ball, SplitPane mainSplitPane, Scene scene) {
        ball.setOnMousePressed(e -> {
            ball.setFill(Color.RED);
            ball.setCenterX(e.getSceneX() - (scene.getWidth() * mainSplitPane.getDividerPositions()[0]));
            ball.setCenterY(e.getSceneY());
            ball.setLayoutX(0);
            ball.setLayoutY(0);
            if (selectedBall != null && !ball.equals(selectedBall)) {
                selectedBall.removeKeyControlsForAcceleration(scene);
                ballsWithActiveKeyControl.remove(selectedBall);
            }
            if (!ballsWithActiveKeyControl.contains(ball)) {
                ball.addKeyControlForAcceleration(scene);
                ballsWithActiveKeyControl.add(ball);
            }
            ballsWithActiveKeyControl.forEach(System.out::println);
            selectedBall = ball;
        });
        Stack<Point2D> dragPoints = new Stack<>();
        ball.setOnMouseDragged((MouseEvent e) -> {
            ball.setCenterX(e.getSceneX() - (scene.getWidth() * mainSplitPane.getDividerPositions()[0]));
            ball.setCenterY(e.getSceneY());
            dragPoints.add(ball.getCenterPosition());
        });
        ball.setOnMouseReleased(e -> {
            ball.setFill(Color.color(Math.random(), Math.random(), Math.random()));
            ball.setSpeedBasedDrag(dragPoints, as.getTimeline().getCycleDuration());
        });
    }

    private static final int MIN_RADIUS = 4, MAX_RADIUS = 20;

    public void setupBallsAndAddToBallGroup(Group ballGroup, SplitPane mainSplitPane, Stage stage, int numberOfBalls) {
        Scene scene = stage.getScene();
        while (ballGroup.getChildren().size() != numberOfBalls) {
            if (ballGroup.getChildren().size() < numberOfBalls) {
                Ball2D ball2D = new Ball2D(getRandomInt(MIN_RADIUS, MAX_RADIUS), Color.color(Math.random(), Math.random(), Math.random()));
                ball2D.setCenterPosition(getRandomPosition());
                configureBall(ball2D, mainSplitPane, scene);
                ballGroup.getChildren().add(ball2D);
            } else ballGroup.getChildren().remove(0);
        }
    }

    private int getRandomInt(int minRadius, int maxRadius) {
        return (int) (Math.random() * (maxRadius - minRadius)) + minRadius;
    }

    private Point2D getRandomPosition() {
        int min = 0, max = 500;
        return new Point2D(getRandomInt(min, max), getRandomInt(min, max));
    }

    public Ball2D getSelectedBall() {
        return selectedBall;
    }
}
