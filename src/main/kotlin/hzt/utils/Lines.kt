package hzt.utils

import javafx.scene.shape.Line

fun <T : Line> T.withStart(x: Double, y: Double): T {
    this.startX = x
    this.startY = y
    return this
}

fun <T : Line> T.withEnd(x: Double, y: Double): T {
    this.endX = x
    this.endY = y
    return this
}
