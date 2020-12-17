package fr.ciadlab.sim.infrastructure.view.network

import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.infrastructure.offset
import fr.ciadlab.sim.math.geometry.Vector3D
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import tornadofx.*

fun RoadNetworkView.roadView(road: Road, debug: Boolean = false) : Unit {
    val defaultLineColor = Color.WHITE

    // Background line
    polyline(*road.points.flatMap { listOf(it.x, it.y) }.toTypedArray()) {
        strokeWidth = width(road)
        strokeType = StrokeType.CENTERED
        strokeLineJoin = StrokeLineJoin.ROUND
        stroke = Color.GRAY
        strokeLineCap = StrokeLineCap.BUTT
    }

    // Middle line
    val middleLineOffset =
            laneWidth * (road.backwardLanesCount - road.forwardLanesCount + if(road.totalLanesCount % 2 != 0) 0.5 else 0.0)

    if(!road.oneWay) {
        polyline(*road.points.offset(middleLineOffset).flatMap { listOf(it.x, it.y) }.toTypedArray()) {
            strokeWidth = 1.0
            stroke = defaultLineColor
        }
    }

    // Lanes separation
    if(!road.oneWay) {
        // For backward lanes
        for(i in 1 until road.backwardLanesCount) {
            val laneOffset = -laneWidth * i + middleLineOffset
            polyline(*road.points.offset(laneOffset).flatMap { listOf(it.x, it.y) }.toTypedArray()) {
                strokeWidth = 0.5
                stroke = defaultLineColor
                strokeDashArray.addAll(2.0, 4.0)
            }
        }
    }

    // For forward lanes
    for(i in 1 until road.forwardLanesCount) {
        val laneOffset = laneWidth * i + middleLineOffset
        polyline(*road.points.offset(laneOffset).flatMap { listOf(it.x, it.y) }.toTypedArray()) {
            strokeWidth = 0.5
            stroke = Color.YELLOW
            strokeDashArray.addAll(2.0, 4.0)
        }
    }

    // Debug line
    if(debug) {
        polyline(*road.points.flatMap { listOf(it.x, it.y) }.toTypedArray()) {
            strokeWidth = 1.0
            stroke = Color.MAGENTA
            strokeLineCap = StrokeLineCap.BUTT
            strokeDashArray.addAll(1.0, 2.0)
        }

        line {
            startX = road.begin().x
            startY = road.begin().y
            endX = startX + road.beginDirection().x * 20
            endY = startY + road.beginDirection().y * 20
        }

        line {
            startX = road.end().x
            startY = road.end().y
            endX = startX + road.endDirection().x * 20
            endY = startY + road.endDirection().y * 20
        }
    }
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