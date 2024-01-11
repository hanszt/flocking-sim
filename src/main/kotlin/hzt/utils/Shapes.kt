package hzt.utils

import javafx.scene.paint.Paint
import javafx.scene.shape.Shape
import javafx.scene.shape.StrokeType

fun <T : Shape> T.withFill(paint: Paint): T {
    this.fill = paint
    return this
}

fun <T : Shape> T.withStrokeType(strokeType: StrokeType): T  = also { this.strokeType = strokeType }

fun <T : Shape> T.isDisabled(disable: Boolean): T {
    this.isDisable = disable
    return this
}

fun <T : Shape> T.withTranslation(x: Double, y: Double): T {
    this.translateX = x
    this.translateY = y
    return this
}
