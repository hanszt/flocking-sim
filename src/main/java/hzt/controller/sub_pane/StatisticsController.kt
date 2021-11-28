package hzt.controller.sub_pane

import hzt.controller.FXMLController
import hzt.model.entity.boid.Boid
import hzt.service.StatisticsService
import hzt.utils.scheduleTask
import javafx.animation.Animation
import javafx.animation.FillTransition
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.util.Duration
import java.util.*

class StatisticsController : FXMLController("statisticsPane.fxml") {

    @FXML
    private lateinit var positionXLabel: Label
    @FXML
    private lateinit var positionYLabel: Label
    @FXML
    private lateinit var frictionStatsLabel: Label
    @FXML
    private lateinit var frameRateLabel: Label
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

    private fun showStatisticsAboutSelectedObject(selected: Boid?) = selected?.run {
        val centerPos = translation
        boidNameLabel.text = String.format("%s", name)
        positionXLabel.text = String.format("$TWO_DEC_DOUBLE p", centerPos.x)
        positionYLabel.text = String.format("$TWO_DEC_DOUBLE p", centerPos.y)
        velocitySelectedBoidLabel.text = String.format("$TWO_DEC_DOUBLE p/s", velocity.magnitude())
        accelerationSelectedBoidLabel.text = String.format("$TWO_DEC_DOUBLE p/s^2", acceleration.magnitude())
        nrOfBoidsInPerceptionRadiusLabel.text = String.format("%-3d", perceptionRadiusMap.size)
        boidSizeLabel.text = String.format("$TWO_DEC_DOUBLE p", distanceFromCenterToOuterEdge * 2)
    }

    private fun showGlobalStatistics(friction: Double, flockSize: Int, runTimeSim: Duration) {
        frictionStatsLabel.text = String.format(TWO_DEC_DOUBLE, friction)
        frameRateLabel.text = String.format("$TWO_DEC_DOUBLE f/s", statisticsService.simpleFrameRateMeter.frameRate)
        numberOfBoidsLabel.text = String.format("%-3d", flockSize)
        runTimeLabel.text = String.format("%-4.3f seconds", runTimeSim.toSeconds())
    }

    override fun getController(): FXMLController = this

    init {
        Timer().scheduleTask(this::startRuntimeLabelFillTransition, Duration.seconds(10.0))
    }

    private fun startRuntimeLabelFillTransition() =
        FillTransition(Duration.seconds(1.0), runTimeLabel, Color.DARKRED, Color.DARKGREEN).apply {
            cycleCount = Animation.INDEFINITE
            isAutoReverse = true
        }.play()

    private companion object {
        private const val TWO_DEC_DOUBLE = "%-4.2f"
    }
}
