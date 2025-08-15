package hzt.utils

import javafx.beans.value.ObservableValue
import javafx.scene.control.Slider
import javafx.stage.Stage

fun <T> ObservableValue<T>.onNewValue(consume: (T) -> Unit) = addListener { _, _, new -> consume(new) }

fun Slider.onChange(consume: (Number) -> Unit) = valueProperty().onNewValue(consume)

fun Stage.inverseFullScreen() {
    isFullScreen = isFullScreen.not()
}

fun String.firstCharUpperCase() = lowercase().replaceFirstChar(Char::uppercaseChar)
