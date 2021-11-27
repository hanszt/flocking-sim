package hzt.utils

import javafx.beans.value.ObservableValue
import javafx.scene.control.Slider
import javafx.stage.Stage
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.Stream

fun <T, U, R> Stream<out T>.collectAndThen(downstream: Collector<in T, *, U>, finisher: Function<U, out R>):
        R = collect(Collectors.collectingAndThen(downstream, finisher))

fun <T> ObservableValue<T>.onNewValue(consumer: Consumer<T>) = addListener { _, _, new -> consumer.accept(new) }

fun Slider.onChange(consumer: Consumer<Number>) = valueProperty().onNewValue(consumer::accept)

fun Stage.inverseFullScreen() {
    isFullScreen = isFullScreen.not()
}

fun String.firstCharUpperCase() = lowercase().replaceFirstChar(Char::uppercaseChar)
