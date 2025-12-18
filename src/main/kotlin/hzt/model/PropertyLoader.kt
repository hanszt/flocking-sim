package hzt.model

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

/**
 * Utility object responsible for loading and managing application properties from a configuration file.
 *
 * Provides methods to read and parse property values, falling back to default values if the property is missing
 * or cannot be parsed.
 */
object PropertyLoader {

    private val LOGGER: Logger = LoggerFactory.getLogger(PropertyLoader::class.java)
    private val PROPS = configProperties()

    @Suppress("SameParameterValue")
    fun parsedDoubleAppProp(property: String?, defaultVal: Double): Double {
        var value = defaultVal
        val propertyVal = PROPS.getProperty(property)
        if (propertyVal != null) {
            try {
                value = propertyVal.toDouble()
            } catch (e: NumberFormatException) {
                LOGGER.warn(
                    "Property '{}' with value '{}' could not be parsed to a double... " +
                            "Falling back to default: {}...", property, propertyVal, defaultVal
                )
            }
        } else {
            LOGGER.warn("Property '{}' not found. Falling back to default: {}", property, defaultVal)
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
                    "Property '{}' with value '{}' could not be parsed to an int... " +
                            "Falling back to default: {}...", property, propertyVal, defaultVal
                )
            }
        } else {
            LOGGER.warn("Property '{}' not found. Falling back to default: {}", property, defaultVal)
        }
        return value
    }

    private fun configProperties(): Properties {
        val properties = Properties()
        val pathName = "./src/main/resources/app.properties"
        val file = File(pathName)
        try {
            BufferedInputStream(FileInputStream(file)).use(properties::load)
        } catch (e: IOException) {
            LOGGER.warn("$pathName not found...", e)
        }
        return properties
    }
}