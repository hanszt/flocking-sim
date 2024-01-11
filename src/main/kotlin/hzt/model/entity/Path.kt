package hzt.model.entity

import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Line

class Path : Group() {

    private var lineWidth = 0.0
    private var paint: Paint = Color.WHITE

    fun addLine(currentPosition: Point2D, prevPosition: Point2D) {
        if (currentPosition != Point2D.ZERO && prevPosition != Point2D.ZERO) {
            children.add(Line(prevPosition.x, prevPosition.y, currentPosition.x, currentPosition.y).apply {
                stroke = paint
                strokeWidth = lineWidth
            })
        }
    }

    fun setLineWidth(lineWidth: Double) {
        this.lineWidth = lineWidth
    }

    fun fadeOut() {
        val children = children
        val size = children.size
        for (i in 0 until size) {
            val node = children[i]
            val line = node as Line
            line.opacity = (i.toFloat() / size).toDouble()
        }
    }

    fun removeLine(index: Int) {
        children.removeAt(index)
    }

    val elements: ObservableList<Node>
        get() = children

    fun setStroke(paint: Paint) {
        this.paint = paint
        children.forEach { (it as Line).stroke = paint }
    }
}
