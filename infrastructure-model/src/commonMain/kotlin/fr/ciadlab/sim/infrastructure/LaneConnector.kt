package fr.ciadlab.sim.infrastructure

import fr.ciadlab.sim.math.algebra.Vector3D

data class LaneConnector(
    val sourceRoad: Road,
    val sourceLane: Int,
    val destinationRoad: Road,
    val destinationLane: Int) {

    val sourcePoint =
            if(sourceRoad.isBackwardLane(sourceLane)) {
                sourceRoad.begin()
            } else {
                sourceRoad.end()
            }

    val sourceDirection: Vector3D =
            if(sourceRoad.isBackwardLane(sourceLane)) {
                -sourceRoad.beginDirection()
            } else {
                sourceRoad.endDirection()
            }

    val sourceNormal =
            if(sourceRoad.isBackwardLane(sourceLane)) {
                Vector3D(
                    sourceDirection.y,
                    -sourceDirection.x,
                    sourceDirection.z
                )
            } else {
                Vector3D(
                    -sourceDirection.y,
                    sourceDirection.x,
                    sourceDirection.z
                )
            }

    val destinationPoint =
            if(destinationRoad.isBackwardLane(destinationLane)) {
                destinationRoad.end()
            } else {
                destinationRoad.begin()
            }

    val destinationDirection: Vector3D =
            if(destinationRoad.isBackwardLane(destinationLane)) {
                -destinationRoad.endDirection()
            } else {
                destinationRoad.beginDirection()
            }

    val destinationNormal =
            if(destinationRoad.isBackwardLane(destinationLane)) {
                Vector3D(
                    destinationDirection.y,
                    -destinationDirection.x,
                    destinationDirection.z
                )
            } else {
                Vector3D(
                    -destinationDirection.y,
                    destinationDirection.x,
                    destinationDirection.z
                )
            }
}
