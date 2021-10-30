package hzt.controller.sub_pane

import hzt.controller.FXMLController
import hzt.model.entity.boid.Boid
import hzt.service.StatisticsService
import hzt.utils.TimerUtils.scheduleTask
import javafx.animation.Animation
import javafx.animation.FillTransition
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.util.Duration
import java.util.Timer

class StatisticsController : FXMLController("statisticsPane.fxml") {

    @FXML
    private lateinit var positionXLabel: Label

    @FXML
    private lateinit var positionYLabel: Label

    @FXML
    private lateinit var frictionStatsLabel: Label

    @FXML
    private lateinit var frameRateStatsLabel: Label

    @FXML
    private lateinit var numberOfBoidsLabel: Label

    @FXML
    private lateinit var runTimeLabel: Text

    @FXML
    private lateinit var boidNameLabel: Label

    @FXML
    private lateinit var velocitySelectedBoidLabel: Label

    @FXML
    private lateinit var accelerationSelectedBoidLabel: Label

    @FXML
    private lateinit var boidSizeLabel: Label

    @FXML
    private lateinit var nrOfBoidsInPerceptionRadiusLabel: Label
    
    private val statisticsService = StatisticsService()
    
    fun showStatists(selected: Boid?, frictionFactor: Double, flockSize: Int, runTimeSim: Duration) {
        showGlobalStatistics(frictionFactor, flockSize, runTimeSim)
        showStatisticsAboutSelectedObject(selected)
    }

    private fun showStatisticsAboutSelectedObject(selected: Boid?) {
        if (selected != null) {
            val centerPos = selected.translation
            boidNameLabel.text = String.format("%s", selected.name)
            positionXLabel.text = String.format("$TWO_DEC_DOUBLE p", centerPos.x)
            positionYLabel.text = String.format("$TWO_DEC_DOUBLE p", centerPos.y)
            velocitySelectedBoidLabel.text = String.format("$TWO_DEC_DOUBLE p/s", selected.velocity.magnitude())
            accelerationSelectedBoidLabel.text = String.format("$TWO_DEC_DOUBLE p/s^2", selected.acceleration.magnitude())
            nrOfBoidsInPerceptionRadiusLabel.text = String.format("%-3d", selected.perceptionRadiusMap.size)
            boidSizeLabel.text = String.format("$TWO_DEC_DOUBLE p", selected.distanceFromCenterToOuterEdge * 2)
        }
    }

    private fun showGlobalStatistics(friction: Double, flockSize: Int, runTimeSim: Duration) {
        frictionStatsLabel.text = String.format(TWO_DEC_DOUBLE, friction)
        frameRateStatsLabel.text = String.format(
            "$TWO_DEC_DOUBLE f/s",
            statisticsService.simpleFrameRateMeter.frameRate
        )
        numberOfBoidsLabel.text = String.format("%-3d", flockSize)
        runTimeLabel.text = String.format("%-4.3f seconds", runTimeSim.toSeconds())
    }

    override fun getController(): FXMLController {
        return this
    }

    init {
        Timer().scheduleTask(this::startRuntimeLabelFillTransition, 10_000)
    }

    private fun startRuntimeLabelFillTransition() {
        val transition = FillTransition(Duration.seconds(1.0), runTimeLabel, Color.DARKRED, Color.DARKGREEN)
        transition.cycleCount = Animation.INDEFINITE
        transition.isAutoReverse = true
        transition.play()
    }

    companion object {
        private const val TWO_DEC_DOUBLE = "%-4.2f"
    }
}
