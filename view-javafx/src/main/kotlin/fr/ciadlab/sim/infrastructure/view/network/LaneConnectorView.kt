package fr.ciadlab.sim.infrastructure.view.network

import fr.ciadlab.sim.infrastructure.LaneConnector
import javafx.scene.paint.Color
import tornadofx.*

class LaneConnectorView(val laneConnector: LaneConnector) {

}

fun RoadNetworkView.laneConnectorView(laneConnector: LaneConnector) {
    // Settings
    val lineWidth = 1.0
    val color = Color.WHITE
    val arrowSize = 3.0
    val arrowRatio = 2.0

    // Aliases
    val sourceLane = laneConnector.sourceLane
    val sourceRoad = laneConnector.sourceRoad
    val destinationLane = laneConnector.destinationLane
    val destinationRoad = laneConnector.destinationRoad

    val sourcePoint = laneConnector.sourcePoint
    val destinationPoint = laneConnector.destinationPoint
    val sourceNormal = laneConnector.sourceNormal
    val destinationNormal = laneConnector.destinationNormal

    val connectorEnd = destinationPoint.add(destinationRoad.laneOffset(destinationLane) * laneWidth, destinationNormal)

    cubiccurve {
        startX = sourcePoint.x + (sourceNormal * sourceRoad.laneOffset(sourceLane) * laneWidth).x
        startY = sourcePoint.y + (sourceNormal * sourceRoad.laneOffset(sourceLane) * laneWidth).y
        endX = connectorEnd.x
        endY = connectorEnd.y
        controlX1 = startX + laneConnector.sourceDirection.x * 20
        controlY1 = startY + laneConnector.sourceDirection.y * 20
        controlX2 = endX - laneConnector.destinationDirection.x * 20
        controlY2 = endY - laneConnector.destinationDirection.y * 20
        stroke = color
        strokeWidth = lineWidth
        fill = Color.TRANSPARENT
    }

    val arrowP1 = connectorEnd.add(arrowRatio * arrowSize, -laneConnector.destinationDirection).add(arrowSize, laneConnector.destinationNormal)
    val arrowP2 = connectorEnd.add(arrowRatio * arrowSize, -laneConnector.destinationDirection).add(arrowSize, -laneConnector.destinationNormal)
    polygon(connectorEnd.x, connectorEnd.y, arrowP1.x, arrowP1.y, arrowP2.x, arrowP2.y) {
        stroke = color
        fill = color
    }

}