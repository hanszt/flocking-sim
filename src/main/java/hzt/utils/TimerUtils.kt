package hzt.utils

import java.lang.Runnable
import java.util.*

object TimerUtils {
    @JvmStatic
    fun taskFor(runnable: Runnable): TimerTask {
        return object : TimerTask() {
            override fun run() {
                runnable.run()
            }
        }
    }

    fun Timer.scheduleTask(runnable: Runnable, delay: Long) = schedule(taskFor(runnable), delay)
}
