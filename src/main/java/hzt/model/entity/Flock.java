package hzt.model.entity;

import hzt.model.FlockProperties;
import hzt.model.utils.Engine;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Random;
import java.util.stream.Collectors;

import static hzt.controller.scenes.MainSceneController.MAX_NUMBER_OF_BOIDS;
import static hzt.model.AppConstants.*;
import static hzt.model.utils.RandomGenerator.*;
import static java.lang.Math.*;

public class Flock extends Group implements Iterable<Boid> {

    private final FlockProperties flockProperties = new FlockProperties();
    private final Scene mainScene;

    private Boid selectedBoid;
    private Color uniformBallColor = INIT_UNIFORM_BALL_COLOR;
    private Color selectedBallColor = INIT_SELECTED_BALL_COLOR;
    private FlockType flockType;
    private Engine.FlockingSim flockingSim;

    public Flock(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public void controlFlockSize(int numberOfBalls, Dimension2D parentDimension) {
        while (this.getChildren().size() != numberOfBalls) {
            if (this.getChildren().size() < numberOfBalls) addBoidToFlock(parentDimension);
            else removeBoidFromFLock();
        }
    }

    private Boid addBoidToFlock(Dimension2D parentDimension) {
        Boid boid = flockType.createBoid(flockProperties.getMaxBoidSize());
        flockType.setCenterPosition(boid, parentDimension);

        boid.setPerceptionRadius(boid.getBody().getRadius() * flockProperties.getPerceptionRadiusRatio());
        boid.setRepelRadius(boid.getBody().getRadius() * flockProperties.getRepelRadiusRatio());
        boid.addMouseFunctionality();
        this.getChildren().add(boid);
        boid.setVisibilityBoidComponents(flockProperties);
        return boid;
    }

    private void removeBoidFromFLock() {
        ObservableList<Node> list = this.getChildren();
        Boid boid = (Boid) list.get(0);
        this.getChildren().remove(boid);
        for (Node node : this.getChildren()) {
            Boid other = (Boid) node;
            other.getPerceptionRadiusMap().remove(boid);
            other.getChildren().removeIf(n -> n instanceof Connection);
        }
        if (boid.equals(selectedBoid)) {
            selectedBoid = !list.isEmpty() ? getRandomSelectedBoid() : null;
        }
    }

    public void addBoidToFlockAtMouseTip(MouseEvent mouseEvent, Dimension2D dimension2D, Slider numberOfBoidsSlider) {
        int numberOfBoids = getChildren().size();
        boolean isMiddleOrSecondary = mouseEvent.isMiddleButtonDown() || mouseEvent.isSecondaryButtonDown();
        if (isMiddleOrSecondary) {
            if (numberOfBoids < MAX_NUMBER_OF_BOIDS) {
                numberOfBoidsSlider.setValue(numberOfBoids);
                Boid boid = addBoidToFlock(dimension2D);
                boid.setCenterPosition(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
            } else {
                removeBoidFromFLock();
                Boid boid = addBoidToFlock(dimension2D);
                boid.setCenterPosition(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
            }
        }
    }

    public Boid getRandomSelectedBoid() {
        Boid boid = (Boid) this.getChildren().get(new Random().nextInt(getChildren().size()));
        boid.updatePaint(selectedBallColor);
        boid.addKeyControlForAcceleration();
        boid.toFront();
        updateSelectedBoidComponentsVisibility(boid);
        return boid;
    }

    @NotNull
    @Override
    public Iterator<Boid> iterator() {
        return getChildren().stream()
                .filter(Boid.class::isInstance)
                .map(Boid.class::cast)
                .collect(Collectors.toList()).iterator();
    }

    public void updateSelectedBoidComponentsVisibility(Boid selectedBoid) {
        selectedBoid.getPerceptionCircle().setVisible(flockProperties.isSelectedPerceptionCircleVisible());
        selectedBoid.getPath().setVisible(flockProperties.isSelectedPathVisible());
    }

    public void updateBoidComponentsVisibility(Boid boid) {
        boid.getPerceptionCircle().setVisible(flockProperties.isPerceptionCircleVisible());
        boid.getPath().setVisible(flockProperties.isAllPathsVisible());
    }


    public abstract static class FlockType {

        abstract Boid createBoid(double maxBallSize);

        abstract void setCenterPosition(Boid boid, Dimension2D dimension);

        @Override
        public String toString() {
            return "FlockType";
        }

    }

    private final FlockType random = new FlockType() {
        @Override
        Boid createBoid(double maxBoidSize) {
            return new Boid(getRandomDouble(MIN_RADIUS, maxBoidSize), getRandomColor());
        }

        @Override
        void setCenterPosition(Boid boid, Dimension2D dimension) {
            boid.setCenterPosition(getRandomPositionOnParent(dimension.getWidth(), dimension.getHeight()));
        }

        @Override
        public String toString() {
            return "Random flock";
        }
    };


    private final FlockType uniform = new FlockType() {
        @Override
        Boid createBoid(double maxBoidSize) {
            return new Boid(maxBoidSize, uniformBallColor);
        }

        @Override
        void setCenterPosition(Boid boid, Dimension2D dimension) {
            boid.setCenterPosition(getRandomPositionOnParent(dimension.getWidth(), dimension.getHeight()));
        }

        @Override
        public String toString() {
            return "Uniform Flock";
        }
    };


    private final FlockType uniformOrdered = new FlockType() {
        @Override
        Boid createBoid(double maxBoidSize) {
            return new Boid(maxBoidSize, uniformBallColor);
        }

        @Override
        void setCenterPosition(Boid boid, Dimension2D dimension) {
            int index = Boid.getNext() % MAX_NUMBER_OF_BOIDS;
            boid.setCenterPosition(getCirclePositionOnParent(dimension.getWidth(), dimension.getHeight(), index));
        }

        private Point2D getCirclePositionOnParent(double width, double height, int index) {
            Point2D centerPosition = new Point2D(width / 2, height / 2);
            double positionMultiplier = (width + height) / 8;
            Point2D circularPosition = new Point2D(
                    positionMultiplier * cos((2 * index * PI) / MAX_NUMBER_OF_BOIDS),
                    positionMultiplier * sin((2 * index * PI) / MAX_NUMBER_OF_BOIDS));
            return circularPosition.add(centerPosition);
        }

        @Override
        public String toString() {
            return "Uniform ordered Flock";
        }
    };

    public FlockType getUniformOrdered() {
        return uniformOrdered;
    }

    public FlockType getUniform() {
        return uniform;
    }

    public FlockType getRandom() {
        return random;
    }

    public FlockType getFlockType() {
        return flockType;
    }

    public FlockProperties getFlockProperties() {
        return flockProperties;
    }

    public Engine.FlockingSim getFlockingSim() {
        return flockingSim;
    }

    public Scene getMainScene() {
        return mainScene;
    }

    public Boid getSelectedBoid() {
        return selectedBoid;
    }

    public void setSelectedBoid(Boid selectedBoid) {
        this.selectedBoid = selectedBoid;
    }

    public void setUniformBallColor(Color uniformBallColor) {
        this.uniformBallColor = uniformBallColor;
    }

    public Color getSelectedBallColor() {
        return selectedBallColor;
    }

    public void setSelectedBallColor(Color selectedBallColor) {
        this.selectedBallColor = selectedBallColor;
    }

    public void setFlockType(FlockType flockType) {
        this.flockType = flockType;
    }

    public void setFlockingSim(Engine.FlockingSim flockingSim) {
        this.flockingSim = flockingSim;
    }
}
