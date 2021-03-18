package fr.ciadlab.sim.infrastructure.viewjs.canvas

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Image

fun HTMLCanvasElement.context2D() =
    this.getContext("2d") as CanvasRenderingContext2D

// region Line
data class Line(
    var lineWidth: Double = 1.0,
    var strokeStyle: Color = Color.BLACK,
    var lineDash: List<Double> = listOf(),
    var startX: Double = 0.0,
    var startY: Double = 0.0,
    var endX: Double = 0.0,
    var endY: Double = 0.0)

/**
 * Draws a line
 */
fun CanvasRenderingContext2D.line(op: Line.() -> Unit = {}) {
    // Initialize settings
    val line = Line()
    op.invoke(line)

    this.save()

    // Apply style
    this.lineWidth = line.lineWidth
    this.strokeStyle = line.strokeStyle.toString()
    this.setLineDash(line.lineDash.toTypedArray())

    // Draw path
    this.beginPath()
    this.moveTo(line.startX, line.startY)
    this.lineTo(line.endX, line.endY)
    this.stroke()

    this.restore()
}
// endregion

// region Circle
data class Circle(
    var centerX: Double = 0.0, var centerY: Double = 0.0, var radius: Double = 1.0,
    var fill: Color = Color.BLACK, var stroke: Color = Color.BLACK, var strokeWidth: Double = 1.0)

/**
 * Draws a circle
 */
fun CanvasRenderingContext2D.circle(op: Circle.() -> Unit = {}) {
    // Initialize settings
    val circle = Circle()
    op.invoke(circle)

    this.save()

    // Apply style
    this.lineWidth = circle.strokeWidth
    this.fillStyle = circle.fill.toString()
    this.strokeStyle = circle.stroke.toString()

    // Draw circle
    this.beginPath()
    this.arc(circle.centerX, circle.centerY, circle.radius, 0.0, kotlin.math.PI * 2.0)
    this.closePath()
    this.stroke()
    if(circle.fill != Color.TRANSPARENT)
        this.fill()

    this.restore()
}
// endregion

// region Polyline
data class Polyline(
    var lineWidth: Double = 1.0,
    var strokeStyle: Color = Color.BLACK,
    var lineDash: List<Double> = listOf()
)

/**
 * Draws a polyline
 */
fun CanvasRenderingContext2D.polyline(vararg elements: Double, op: Polyline.() -> Unit = {}) {
    // Initialize settings
    val polyline = Polyline()
    op.invoke(polyline)

    this.save()

    // Apply style
    this.lineWidth = polyline.lineWidth
    this.strokeStyle = polyline.strokeStyle.toString()
    this.setLineDash(polyline.lineDash.toTypedArray())

    // Draw path
    if(elements.size >= 2) {
        this.beginPath()
        this.moveTo(elements[0], elements[1])
        for(i in elements.indices step 2) {
            this.lineTo(elements[i], elements[i + 1])
        }
        this.closePath()
        this.stroke()
    }

    this.restore()
}
// endregion

// region Polygon
data class Polygon(var fill: Color = Color.BLACK, var stroke: Color = Color.BLACK)

fun CanvasRenderingContext2D.polygon(vararg elements: Double, op: Polygon.() -> Unit = {}) {
    // Initialize settings
    val polygon = Polygon()
    op.invoke(polygon)

    this.save()

    // Apply style
    this.fillStyle = polygon.fill.toString()
    this.strokeStyle = polygon.stroke.toString()

    // Draw path
    if(elements.size >= 2) {
        this.beginPath()
        this.moveTo(elements[0], elements[1])
        for(i in elements.indices step 2) {
            this.lineTo(elements[i], elements[i + 1])
        }
        this.closePath()
        this.fill()
    }

    this.restore()
}
// endregion

// region Cubic curve
data class CubicCurve(
    var fill: Color = Color.TRANSPARENT,
    var strokeWidth: Double = 1.0,
    var stroke: Color = Color.WHITE,
    var startX: Double = 0.0,
    var startY: Double = 0.0,
    var endX: Double = 0.0,
    var endY: Double = 0.0,
    var controlX1: Double = 0.0,
    var controlY1: Double = 0.0,
    var controlX2: Double = 0.0,
    var controlY2: Double = 0.0)

fun CanvasRenderingContext2D.cubiccurve(op: CubicCurve.() -> Unit = {}) {
    // Initialize settings
    val cubicCurve = CubicCurve()
    op.invoke(cubicCurve)

    this.save()

    // Apply style
    this.fillStyle = cubicCurve.fill.toString()
    this.strokeStyle = cubicCurve.stroke.toString()

    // Draw curve
    this.beginPath()
    this.moveTo(cubicCurve.startX, cubicCurve.startY)
    this.bezierCurveTo(cubicCurve.controlX1, cubicCurve.controlY1,
        cubicCurve.controlX2, cubicCurve.controlY2,
        cubicCurve.endX, cubicCurve.endY)
    this.stroke()

    this.restore()
}
// endregion

// region Image
fun image(src: String): Image {
    // Load the image
    val image = Image()
    image.src = src
    return image
}
// endregion

// region Clear
//fun CanvasRenderingContext2D.clear() {
//    this.clearRect(0.0, 0.0, this.canvas.width.toDouble(), this.canvas.height.toDouble())
//}
fun CanvasRenderingContext2D.clear(canvas: HTMLCanvasElement) {
    this.save()
    this.setTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
    canvas.context2D().fillStyle = Color.rgb(230, 230, 230)
    canvas.context2D().fillRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
    this.restore()
}
// endregion
