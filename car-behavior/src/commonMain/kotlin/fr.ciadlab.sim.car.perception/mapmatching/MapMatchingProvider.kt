package fr.ciadlab.sim.car.perception.mapmatching

import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.infrastructure.RoadNetwork
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.math.algebra.Vector3D
import fr.ciadlab.sim.math.algebra.project
import fr.ciadlab.sim.math.algebra.toVector3D

/**
 * Simple implementation of a map-matching provider, not relying on any optimization
 * @author Alexandre Lombard
 */
class MapMatchingProvider(
    /** The road network */
    val roadNetwork: RoadNetwork,
    /** An entry point allowing to define an advanced policy by filtering the potential roads */
    private val filterRoads: (RoadNetwork, Vector2D)->List<Road> = { _, _ -> roadNetwork.roads }) {

    /**
     * Computes the closest road to a position using the given road network
     * @param position the position
     * @return the closest road to the vehicle
     */
    fun mapMatching(position: Vector2D): MapMatchingData {
        val candidateRoads = filterRoads(roadNetwork, position)

        // Find the road
        var minDistance = Double.MAX_VALUE
        var closestRoad = candidateRoads[0]
        var closestRoadPoint = closestRoad.begin().xy
            candidateRoads.forEach {
            val projectionData = it.points.project(Vector3D(position.x, position.y, 0.0))
            if(projectionData.distance < minDistance) {
                minDistance = projectionData.distance
                closestRoad = it
                closestRoadPoint = projectionData.projection.xy
            }
        }

        // Find the lane
        var laneIndex = 0
        var minLaneDistance = Double.POSITIVE_INFINITY
        for(lane in 0 until closestRoad.totalLanesCount) {
            // FIXME Externalize lane width
            val laneProjection = closestRoad.lane(lane).project(closestRoadPoint.toVector3D())

            if(laneProjection.distance < minLaneDistance) {
                minLaneDistance = laneProjection.distance
                laneIndex = lane
            }
        }

        return MapMatchingData(position, closestRoadPoint, closestRoad, laneIndex)
    }

}
