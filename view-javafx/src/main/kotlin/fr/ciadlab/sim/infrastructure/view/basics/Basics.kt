package fr.ciadlab.sim.infrastructure.view.basics

import fr.ciadlab.sim.car.behavior.DriverBehavioralAction
import fr.ciadlab.sim.car.behavior.DriverBehavioralState
import fr.ciadlab.sim.car.behavior.reachGoalBehavior
import fr.ciadlab.sim.car.behavior.routing.OriginDestinationRouter
import fr.ciadlab.sim.car.perception.mapmatching.MapMatchingProvider
import fr.ciadlab.sim.car.perception.obstacles.RadarPerceptionProvider
import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.infrastructure.RoadNetwork
import fr.ciadlab.sim.math.algebra.project
import fr.ciadlab.sim.math.algebra.toVector3D
import fr.ciadlab.sim.physics.Units
import fr.ciadlab.sim.physics.unit
import fr.ciadlab.sim.traffic.TrafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle

/**
 * Simple functions that can be used as default behaviors
 * @author Alexandre Lombard
 */

fun TrafficSimulation<Vehicle>.basicOnSpawn(vehicle: Vehicle, routes: MutableMap<Vehicle, List<Pair<Road, Boolean>>?>) {
    // Compute a route from the current position to a random exit area
    val destination = exitAreas.random().position
    val router = OriginDestinationRouter(roadNetwork, MapMatchingProvider(roadNetwork))
    val route = router.findRoute(vehicle.position, destination)

    if(route != null) {
        // Define the directions of the roads of the route
        val directions = route.mapIndexed { index, road ->
            if(index < route.size - 1) {
                // If we are before the last, we are forward if the next road is at the end of the current
                roadNetwork.isAtEnd(road, route[index + 1])
            } else {
                // If we are the last, we are forward if the destination is closer to the end of the road
                destination.toVector3D().distance(road.end()) < destination.toVector3D().distance(road.begin())
            }
        }

        // Store it
        routes[vehicle] = route.mapIndexed { index, road -> Pair(road, directions[index]) }
    }
}

fun TrafficSimulation<Vehicle>.basicVehicleBehavior (
    routes: Map<Vehicle, List<Pair<Road, Boolean>>?>,
    roadNetworkModel: RoadNetwork,
    vehicle: Vehicle,
    deltaTime: Double): DriverBehavioralAction {
    // Compute perceptions
    val radar = RadarPerceptionProvider()
    val radarData = radar.performRadarDetection(vehicle.position, vehicle.direction, this.vehicles)
    // Retrieve the computed route and the current road
    val route = routes[vehicle]
    val currentRoad = route?.minByOrNull { it.first.points.project(vehicle.position.toVector3D()).distance }

    // Execute the behavior
    val driverBehavioralState = DriverBehavioralState(
        currentRoad?.first ?: roadNetworkModel.roads[0],
        0,      // FIXME
        currentRoad?.second ?: true,
        radarData,
        50.0 unit Units.KilometersPerHour,
        route?.last()?.first?.end() ?: roadNetworkModel.roads[0].end())

    return vehicle.reachGoalBehavior(driverBehavioralState).apply(deltaTime)
}

fun TrafficSimulation<Vehicle>.basicVehicleUpdate(vehicle: Vehicle, action: DriverBehavioralAction, deltaTime: Double): Vehicle {
    return vehicle.update(action.targetAcceleration, action.targetWheelAngle, deltaTime)
}
