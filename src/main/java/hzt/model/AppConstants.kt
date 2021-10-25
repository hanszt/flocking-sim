package hzt.model

import javafx.geometry.Dimension2D
import javafx.scene.paint.Color
import javafx.util.Duration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.util.Supplier
import java.lang.NumberFormatException
import java.io.File
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.IOException
import java.util.*

object AppConstants {
    private val LOGGER = LogManager.getLogger(AppConstants::class.java)
    private val PROPS = configProperties()
    @JvmField
    val INIT_FRAME_RATE = parsedIntAppProp("framerate", 30) // f/s
    @JvmField
    val INIT_FRAME_DURATION = Duration.seconds(1.0 / INIT_FRAME_RATE) // s/f
    const val MIN_SIZE = 3
    @JvmField
    val INIT_UNIFORM_BALL_COLOR = Color.ORANGE
    @JvmField
    val INIT_SELECTED_BALL_COLOR = Color.RED
    @JvmField
    val INIT_BG_COLOR = Color.NAVY
    @JvmField
    var MIN_STAGE_DIMENSION: Dimension2D? = null
    @JvmField
    var INIT_SCENE_DIMENSION: Dimension2D? = null
    private const val ANSI_RESET = "\u001B[0m"
    private const val ANSI_BLUE = "\u001B[34m"
    const val TITLE = "Flocking Simulation"
    const val DOTTED_LINE = "----------------------------------------------------------------------------------------\n"
    const val CLOSING_MESSAGE = ANSI_BLUE + "See you next Time! :)" + ANSI_RESET
    @JvmField
    val STAGE_OPACITY = parsedDoubleAppProp("stage_opacity", .8)
    fun parsedDoubleAppProp(property: String?, defaultVal: Double): Double {
        var value = defaultVal
        val propertyVal = PROPS.getProperty(property)
        if (propertyVal != null) {
            try {
                value = propertyVal.toDouble()
            } catch (e: NumberFormatException) {
                LOGGER.warn(
                    String.format(
                        "Property '%s' with value '%s' could not be parsed to a double... " +
                                "Falling back to default: %f...", property, propertyVal, defaultVal
                    )
                )
            }
        } else {
            LOGGER.warn(Supplier<Any> {
                String.format(
                    "Property '%s' not found. Falling back to default: %f",
                    property, defaultVal
                )
            })
        }
        return value
    }

    @JvmStatic
    fun parsedIntAppProp(property: String?, defaultVal: Int): Int {
        var value = defaultVal
        val propertyVal = PROPS.getProperty(property)
        if (propertyVal != null) {
            try {
                value = propertyVal.toInt()
            } catch (e: NumberFormatException) {
                LOGGER.warn(
                    String.format(
                        "Property '%s' with value '%s' could not be parsed to an int... " +
                                "Falling back to default: %d...", property, propertyVal, defaultVal
                    )
                )
            }
        } else {
            LOGGER.warn(Supplier<Any> {
                String.format(
                    "Property '%s' not found. Falling back to default: %d",
                    property, defaultVal
                )
            })
        }
        return value
    }

    private fun determineMinStageDimension(): Dimension2D {
        val defaultMinStageWidth = 750
        val defaultMinStageHeight = 500
        val minStageWidth =
            if (INIT_SCENE_DIMENSION!!.width < defaultMinStageWidth) INIT_SCENE_DIMENSION!!.width else defaultMinStageWidth.toDouble()
        val minStageHeight =
            if (INIT_SCENE_DIMENSION!!.height < defaultMinStageHeight) INIT_SCENE_DIMENSION!!.height else defaultMinStageHeight.toDouble()
        return Dimension2D(minStageWidth, minStageHeight)
    }

    private fun configProperties(): Properties {
        val properties = Properties()
        val pathName = "./src/main/resources/app.properties"
        val file = File(pathName)
        try {
            BufferedInputStream(FileInputStream(file)).use { stream -> properties.load(stream) }
        } catch (e: IOException) {
            LOGGER.warn(Supplier<Any> { "$pathName not found..." }, e)
        }
        return properties
    }

    enum class Scene(val fxmlFileName: String, val englishDescription: String) {
        MAIN_SCENE("mainScene.fxml", "Main Scene"), ABOUT_SCENE("aboutScene.fxml", "About Scene");

    }

    init {
        val sceneWidthProp = parsedIntAppProp("init_scene_width", 1200)
        val sceneHeightProp = parsedIntAppProp("init_scene_height", 800)
        INIT_SCENE_DIMENSION = Dimension2D(sceneWidthProp.toDouble(), sceneHeightProp.toDouble())
        MIN_STAGE_DIMENSION = determineMinStageDimension()
    }
}
