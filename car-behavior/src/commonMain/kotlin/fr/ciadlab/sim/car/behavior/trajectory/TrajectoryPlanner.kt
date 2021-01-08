package fr.ciadlab.sim.car.behavior.trajectory

import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.infrastructure.RoadNetwork
import fr.ciadlab.sim.math.algebra.*
import fr.ciadlab.sim.vehicle.Vehicle

class TrajectoryPlanner(
    val roadNetwork: RoadNetwork,
    val length: Double = 150.0) {

    /**
     * Computes a trajectory from a route
     * @param route the route
     * @return a polyline representing the trajectory
     */
    fun computeTrajectory(
        vehicle: Vehicle, lane: Int, destination: Vector3D, route: List<Road>): List<Vector3D> {
        // Define the directions of the roads of the route
        val forward = route.mapIndexed { index, road ->
            if(index < route.size - 1) {
                // If we are before the last, we are forward if the next road is at the end of the current
                roadNetwork.isAtEnd(road, route[index + 1])
            } else {
                // If we are the last, we are forward if the destination is closer to the end of the road
                destination.distance(road.end()) < destination.distance(road.begin())
            }
        }

        // Find the current road
        var currentRoadIndex = 0
        var currentRoad = route[0]
        var minDistance = Double.POSITIVE_INFINITY
        var projection: ProjectionData = currentRoad.points.project(vehicle.position.toVector3D())

        route.forEachIndexed { index, road ->
            val p = road.points.project(vehicle.position.toVector3D())
            if(p.distance < minDistance) {
                currentRoad = road
                currentRoadIndex = index
                projection = p
                minDistance = p.distance
            }
        }

        // Build the trajectory
        val trajectory = mutableListOf<Vector3D>()
        trajectory.add(projection.projection)

        while(trajectory.length() < length) {
            // TODO Work with the lane instead of the road
            val split = currentRoad.points.split(projection.length)
            if(forward[currentRoadIndex]) {
                trajectory.addAll(split.second)
            } else {
                trajectory.addAll(split.first.reversed())
            }
        }

        // TODO Generate the complete route (including intersections) using a sequence
    }

}
