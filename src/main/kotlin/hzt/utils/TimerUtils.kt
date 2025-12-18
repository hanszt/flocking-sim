package hzt.utils

import javafx.util.Duration
import java.util.*

fun taskFor(runnable: Runnable): TimerTask = object : TimerTask() {
    override fun run() = runnable.run()
}

fun Timer.scheduleTask(runnable: Runnable, delay: Duration) = schedule(taskFor(runnable), delay.toMillis().toLong())
