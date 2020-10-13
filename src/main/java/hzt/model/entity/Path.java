package hzt.model.entity;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import static hzt.controller.AnimationService.LINE_STROKE_WIDTH;

public class Path extends Group {

    private Paint paint;
    private final double lineWidth;

    public Path() {
        this.lineWidth = LINE_STROKE_WIDTH;
    }

    public void addLine(Point2D currentPosition, Point2D prevPosition) {
        if (!currentPosition.equals(Point2D.ZERO) && !prevPosition.equals(Point2D.ZERO)) {
            Line line = new Line(prevPosition.getX(), prevPosition.getY(), currentPosition.getX(), currentPosition.getY());
            line.setStroke(paint);
            line.setStrokeWidth(lineWidth);
            getChildren().add(line);
        }
    }

    public void removeLine(int index) {
        getChildren().remove(index);
    }

    public ObservableList<Node> getElements() {
        return getChildren();
    }

    public void setStroke(Paint paint) {
        this.paint = paint;
        getChildren().forEach(l -> ((Line) l).setStroke(paint));
    }

    public void setPathVisible(Boolean visible) {
        setVisible(visible);
        getChildren().forEach(l -> l.setVisible(visible));
    }

}
