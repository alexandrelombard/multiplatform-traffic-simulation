package fr.ciadlab.sim.infrastructure.viewjs.network

import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.infrastructure.viewjs.canvas.Color
import fr.ciadlab.sim.infrastructure.viewjs.canvas.context2D
import fr.ciadlab.sim.infrastructure.viewjs.canvas.polyline
import fr.ciadlab.sim.math.algebra.Vector3D

fun RoadNetworkView.road(road: Road) {
    val debug = true    // TODO Externalize
    val defaultLineColor = Color.WHITE  // TODO Externalize

    val context = canvas.context2D()
    val height = canvas.height.toDouble()
    val width = canvas.width.toDouble()

    context.save()

    // Background line
    context.polyline(*road.points.flatMap { listOf(it.x, it.y) }.toDoubleArray()) {
        lineWidth = width(road)
        strokeStyle = Color.GRAY
//        strokeLineCap = StrokeLineCap.BUTT
    }

    // Middle line
    val middleLineOffset =
        laneWidth * (road.backwardLanesCount - road.forwardLanesCount + if(road.totalLanesCount % 2 != 0) 0.5 else 0.0)

    if(!road.oneWay) {
        context.polyline(*offset(
            road.points,
            middleLineOffset
        ).flatMap { listOf(it.x, it.y) }.toDoubleArray()) {
            lineWidth = 2.0
            strokeStyle = defaultLineColor
        }
    }

    // Lanes separation
    if(!road.oneWay) {
        // For backward lanes
        for(i in 1 until road.backwardLanesCount) {
            val laneOffset = -laneWidth * i + middleLineOffset
            context.polyline(*offset(
                road.points,
                laneOffset
            ).flatMap { listOf(it.x, it.y) }.toDoubleArray()) {
                lineWidth = 1.0
                strokeStyle = defaultLineColor
                lineDash = listOf(5.0, 10.0)
//                strokeDashArray.addAll(5.0, 10.0)
            }
        }
    }

    // For forward lanes
    for(i in 1 until road.forwardLanesCount) {
        val laneOffset = laneWidth * i + middleLineOffset
        context.polyline(*offset(
            road.points,
            laneOffset
        ).flatMap { listOf(it.x, it.y) }.toDoubleArray()) {
            lineWidth = 1.0
            strokeStyle = Color.YELLOW
            lineDash = listOf(5.0, 10.0)
//            strokeDashArray.addAll(5.0, 10.0)
        }
    }

    // Debug line
    if(debug) {
        context.polyline(*road.points.flatMap { listOf(it.x, it.y) }.toDoubleArray()) {
            lineWidth = 1.0
            strokeStyle = Color.YELLOW
//            strokeLineCap = StrokeLineCap.BUTT
            lineDash = listOf(2.0, 4.0)
        }

//        context.line {
//            startX = road.begin().x
//            startY = road.begin().y
//            endX = startX + road.beginDirection().x * 20
//            endY = startY + road.beginDirection().y * 20
//        }
//
//        context.line {
//            startX = road.end().x
//            startY = road.end().y
//            endX = startX + road.endDirection().x * 20
//            endY = startY + road.endDirection().y * 20
//        }
    }

    context.restore()
}

fun RoadNetworkView.beginBounds(road: Road) : Pair<Vector3D, Vector3D> {
    val width = width(road)
    return Pair(road.begin().add(width / 2.0, road.beginNormal()), road.begin().add(-width / 2.0, road.beginNormal()))
}

fun RoadNetworkView.endBounds(road: Road) : Pair<Vector3D, Vector3D> {
    val width = width(road)
    return Pair(road.end().add(width / 2.0, road.endNormal()), road.end().add(-width / 2.0, road.endNormal()))
}

fun RoadNetworkView.width(road: Road) = laneWidth * road.totalLanesCount

private fun offset(points: List<Vector3D>, offset: Double) : List<Vector3D> {
    val result = arrayListOf<Vector3D>()

    // First point
    val beginDirection = (points[1] - points[0]).normalize()
    val beginNormal =
        Vector3D(-beginDirection.y, beginDirection.x, beginDirection.z)
    result.add(points[0] + (beginNormal * offset))

    // All middle points
    for(i in 1 until points.size - 1) {
        val direction = (points[i + 1] - points[i - 1]).normalize()
        val normal = Vector3D(-direction.y, direction.x, direction.z)

        result.add(points[i] + (normal * offset))
    }

    // Last point
    val endDirection = (points.last() - points[points.lastIndex - 1]).normalize()
    val endNormal =
        Vector3D(-endDirection.y, endDirection.x, endDirection.z)
    result.add(points.last() + (endNormal * offset))

    return result
}
