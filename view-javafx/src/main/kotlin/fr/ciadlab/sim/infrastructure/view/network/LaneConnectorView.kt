package fr.ciadlab.sim.infrastructure.view.network

import fr.ciadlab.sim.infrastructure.LaneConnector
import fr.ciadlab.sim.math.algebra.Vector2D
import javafx.scene.paint.Color
import tornadofx.cubiccurve
import tornadofx.polygon

class LaneConnectorView(val laneConnector: LaneConnector) {

}

fun RoadNetworkView.laneConnectorView(laneConnector: LaneConnector) {
    // Settings
    val lineWidth = 1.0
    val color = Color.WHITE
    val arrowSize = 2.0
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
    val distance = Vector2D(sourcePoint.x + (sourceNormal * sourceRoad.laneOffset(sourceLane) * laneWidth).x, sourcePoint.y + (sourceNormal * sourceRoad.laneOffset(sourceLane) * laneWidth).y)
        .distance(connectorEnd.xy)

    val connectorStart = Vector2D(
        sourcePoint.x + (sourceNormal * sourceRoad.laneOffset(sourceLane) * laneWidth).x,
        sourcePoint.y + (sourceNormal * sourceRoad.laneOffset(sourceLane) * laneWidth).y)

    cubiccurve {
        startX = connectorStart.x
        startY = connectorStart.y
        endX = connectorEnd.x
        endY = connectorEnd.y
        controlX1 = connectorStart.x + laneConnector.sourceDirection.x * (0.35 * distance)
        controlY1 = connectorStart.y + laneConnector.sourceDirection.y * (0.35 * distance)
        controlX2 = connectorEnd.x - laneConnector.destinationDirection.x * (0.35 * distance)
        controlY2 = connectorEnd.y - laneConnector.destinationDirection.y * (0.35 * distance)
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
