package hzt.controller.sub_pane;

import hzt.controller.FXMLController;
import hzt.model.entity.boid.Boid;
import hzt.service.StatisticsService;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.io.IOException;

import static java.lang.String.format;

public class StatisticsController extends FXMLController {


    private static final String TWO_DEC_DOUBLE = "%-4.2f";

    @FXML
    private Label positionXLabel;
    @FXML
    private Label positionYLabel;
    @FXML
    private Label frictionStatsLabel;
    @FXML
    private Label frameRateStatsLabel;
    @FXML
    private Label numberOfBoidsLabel;
    @FXML
    private Label runTimeLabel;
    @FXML
    private Label boidNameLabel;

    @FXML
    private Label velocitySelectedBoidLabel;
    @FXML
    private Label accelerationSelectedBoidLabel;
    @FXML
    private Label boidSizeLabel;
    @FXML
    private Label nrOfBoidsInPerceptionRadiusLabel;

    private final StatisticsService statisticsService = new StatisticsService();

    public StatisticsController() throws IOException {
        super("statisticsPane.fxml");
    }

    public void showStatists(Boid selected, double frictionFactor, int flockSize, Duration runTimeSim) {
        showGlobalStatistics(frictionFactor, flockSize, runTimeSim);
        showStatisticsAboutSelectedObject(selected);
    }

    private void showStatisticsAboutSelectedObject(Boid selected) {
        if (selected != null) {
            Point2D centerPos = selected.getTranslation();
            boidNameLabel.setText(format("%s", selected.getName()));
            positionXLabel.setText(format(TWO_DEC_DOUBLE + " p", centerPos.getX()));
            positionYLabel.setText(format(TWO_DEC_DOUBLE + " p", centerPos.getY()));

            velocitySelectedBoidLabel.setText(format(TWO_DEC_DOUBLE + " p/s", selected.getVelocity().magnitude()));
            accelerationSelectedBoidLabel.setText(format(TWO_DEC_DOUBLE + " p/s^2", selected.getAcceleration().magnitude()));
            nrOfBoidsInPerceptionRadiusLabel.setText(format("%-3d", selected.getPerceptionRadiusMap().size()));
            boidSizeLabel.setText(format(TWO_DEC_DOUBLE + " p", selected.getDistanceFromCenterToOuterEdge() * 2));
        }
    }

    private void showGlobalStatistics(double friction, int flockSize, Duration runTimeSim) {
        frictionStatsLabel.setText(format(TWO_DEC_DOUBLE, friction));
        frameRateStatsLabel.setText(format(TWO_DEC_DOUBLE + " f/s", statisticsService.getSimpleFrameRateMeter().getFrameRate()));
        numberOfBoidsLabel.setText(format("%-3d", flockSize));
        runTimeLabel.setText(format("%-4.3f seconds", runTimeSim.toSeconds()));
    }

    public StatisticsController getController() {
        return this;
    }
}
