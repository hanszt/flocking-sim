package hzt.model.entity;

import hzt.controller.main_scene.AnimationService;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.Stack;

import static hzt.controller.utils.RandomGenerator.getRandomColor;
import static hzt.controller.utils.RandomGenerator.getRandomPositionOnParent;

// extends a Pane so it is resizable, so its size is set by its parent, which essentially determine its bounds.
public class BallGroup extends AnchorPane {

    private Ball2D selectedBall;
    private final AnimationService as;

    public BallGroup(AnimationService as) {
        //Size is zero so it does not influence the rest of the layout when balls are moved
        this.setMinSize(0, 0);
        this.setMaxSize(0, 0);
        this.as = as;
    }

    public void controlBallAmount(int numberOfBalls, double perceptionRadiusRatio, Dimension2D parentDimension) {
        while (this.getChildren().size() != numberOfBalls) {
            if (this.getChildren().size() < numberOfBalls) {
                Ball2D ball2D = new Ball2D(); // with random color and radius
                ball2D.setCenterPosition(getRandomPositionOnParent(parentDimension.getWidth(), parentDimension.getHeight()));
                ball2D.setPerceptionRadius(ball2D.getBody().getRadius() * perceptionRadiusRatio);
                addMouseFunctionality(ball2D);
                this.getChildren().add(ball2D);
            } else {
                Node ball2D = this.getChildren().get(0);
                this.getChildren().remove(ball2D);
                if (ball2D.equals(selectedBall)) selectedBall = null;
            }
        }
    }

    public void addMouseFunctionality(Ball2D ball) {
        ball.getBody().setOnMousePressed(e -> {
            ball.setColor(Color.RED);
            ball.setCenterPosition(e.getX(), e.getY());
            ball.setVelocity(Point2D.ZERO);
            if (!ball.equals(selectedBall)) {
                ball.addKeyControlForAcceleration();
                if (selectedBall != null) selectedBall.removeKeyControlsForAcceleration();
                selectedBall = ball;
            }
        });
        Stack<Point2D> dragPoints = new Stack<>();
        ball.getBody().setOnMouseDragged(e -> {
            ball.getBody().setCenterX(e.getX());
            ball.getBody().setCenterY(e.getY());
            dragPoints.add(ball.getCenterPosition());
        });
        ball.getBody().setOnMouseReleased(e -> {
            ball.setColor(getRandomColor());
            ball.setSpeedBasedDrag(dragPoints, as.getTimeline().getCycleDuration());
        });
    }

    public Ball2D getSelectedBall() {
        return selectedBall;
    }

}
