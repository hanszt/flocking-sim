package hzt.controller.services;

import hzt.model.entity.Ball2D;
import javafx.scene.Group;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Set;

public class PhysicsEngine {

    private final Set<Ball2D> balls = new HashSet<>();

    public void run(Duration cycleDuration, Group group) {
        group.getChildren().forEach(ball2D -> ((Ball2D) ball2D).update(cycleDuration));
    }

    public Set<Ball2D> getBallSet() {
        return balls;
    }
}
