package hzt.utils

import javafx.beans.value.ObservableValue
import javafx.scene.control.Slider
import javafx.stage.Stage
import java.util.function.Consumer

fun <T> ObservableValue<T>.onNewValue(consumer: Consumer<T>) = addListener { _, _, new -> consumer.accept(new) }

fun Slider.onChange(consumer: Consumer<Number>) = valueProperty().onNewValue(consumer::accept)

fun Stage.inverseFullScreen() {
    isFullScreen = isFullScreen.not()
}

fun String.firstCharUpperCase() = lowercase().replaceFirstChar(Char::uppercaseChar)
