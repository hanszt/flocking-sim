package hzt.utils

import javafx.beans.property.ObjectProperty
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.Stream

object FxKtUtils {

    fun <T, U, R> Stream<out T>.collectAndThen(
        downstream: Collector<in T, *, U>,
        finisher: Function<U, out R>
    ): R = collect(Collectors.collectingAndThen(downstream, finisher))

    fun <T> ObjectProperty<T>.onNewValue(consumer: Consumer<T>) = addListener { _, _, new -> consumer.accept(new) }

    fun String.firstLetterUpperCase() =
        if (isNotEmpty()) substring(0, 1).uppercase() + substring(1).lowercase() else ""
}
