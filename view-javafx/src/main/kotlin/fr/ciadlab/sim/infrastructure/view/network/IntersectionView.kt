package fr.ciadlab.sim.infrastructure.view.network

import fr.ciadlab.sim.infrastructure.Intersection
import fr.ciadlab.sim.math.geometry.MonotoneChain
import fr.ciadlab.sim.math.geometry.Vector2D
import fr.ciadlab.sim.math.geometry.Vector3D
import javafx.scene.paint.Color

import tornadofx.polygon

class IntersectionView(val intersection: Intersection) {

}

fun RoadNetworkView.intersectionView(intersection: Intersection) {
    // Compute the convex hull of the intersection
    val polygonPointsSet = hashSetOf<Vector3D>()

    for(laneConnector in intersection.laneConnectors) {
        val sourceRoad = laneConnector.sourceRoad
        val destinationRoad = laneConnector.destinationRoad

        val sourceBounds =
            if(sourceRoad.isForwardLane(laneConnector.sourceLane)) {
                endBounds(sourceRoad)
            } else {
                beginBounds(sourceRoad)
            }

        val destinationBounds =
            if(destinationRoad.isForwardLane(laneConnector.destinationLane)) {
                beginBounds(destinationRoad)
            } else {
                endBounds(destinationRoad)
            }

        polygonPointsSet.addAll(listOf(sourceBounds.first, sourceBounds.second,
            destinationBounds.first, destinationBounds.second))
    }

    val convexHull = MonotoneChain()
        .findHullVertices(polygonPointsSet.map { Vector2D(it.x, it.y) })

    // Draw the background of the intersection
    polygon(*convexHull.flatMap { listOf(it.x, it.y) }.toTypedArray()) {
        fill = Color.GRAY
    }

    // Draw all the lanes connectors
    intersection.laneConnectors.forEach {
        laneConnectorView(it)
    }
}