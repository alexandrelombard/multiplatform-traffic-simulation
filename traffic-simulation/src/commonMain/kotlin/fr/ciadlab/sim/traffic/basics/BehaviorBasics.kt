package fr.ciadlab.sim.infrastructure.view.basics

import fr.ciadlab.sim.car.behavior.DriverBehavioralAction
import fr.ciadlab.sim.car.behavior.DriverBehavioralState
import fr.ciadlab.sim.car.behavior.default.reachGoalBehavior
import fr.ciadlab.sim.car.behavior.default.respectTrafficLightBehavior
import fr.ciadlab.sim.car.behavior.default.respectV2XAuthorizationListBehavior
import fr.ciadlab.sim.car.perception.mapmatching.MapMatchingProvider
import fr.ciadlab.sim.car.perception.obstacles.ObstacleData
import fr.ciadlab.sim.car.perception.obstacles.RadarPerceptionProvider
import fr.ciadlab.sim.car.perception.signals.TrafficLightPerceptionProvider
import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.infrastructure.intersection.IntersectionTrafficLight
import fr.ciadlab.sim.physics.Units
import fr.ciadlab.sim.physics.unit
import fr.ciadlab.sim.traffic.TrafficSimulation
import fr.ciadlab.sim.utils.UUID
import fr.ciadlab.sim.vehicle.Vehicle

/**
 * Simple functions that can be used as default behaviors.
 * @author Alexandre Lombard
 */

/**
 * Computes the obstacle data
 */
fun TrafficSimulation<Vehicle>.generateObstaclePerceptions(vehicle: Vehicle): List<ObstacleData> {
    val radar = RadarPerceptionProvider()
    val radarData = radar.performRadarDetection(vehicle.position, vehicle.direction, this.vehicles)
    return radarData
}

/**
 * Computes the traffic light perception
 */
fun TrafficSimulation<Vehicle>.generateTrafficLightPerceptions(route: List<Pair<Road, Boolean>>?, vehicle: Vehicle) : List<IntersectionTrafficLight> {
    val trafficLights = TrafficLightPerceptionProvider()
    val perceivedTrafficLights = trafficLights.performTrafficLightDetection(
        route?.map { it.first } ?: emptyList(), this.roadNetwork.trafficLights.flatMap { it.trafficLights })
    return perceivedTrafficLights
}

/**
 * Computes the V2X RSU perception for intersection
 */
fun TrafficSimulation<Vehicle>.generateIntersectionRsuPerceptions(
    route: List<Pair<Road, Boolean>>?, vehicle: Vehicle) : List<UUID> {

}

/**
 * Basic vehicle behavior
 */
fun TrafficSimulation<Vehicle>.basicVehicleBehavior (
    routes: Map<Vehicle, List<Pair<Road, Boolean>>?>,   // TODO Avoid having a route for all vehicles here
    vehicle: Vehicle,
    deltaTime: Double): DriverBehavioralAction {
    // Retrieve the computed route and the current road
    val route = routes[vehicle]
    val mapMatcher = MapMatchingProvider(roadNetwork)
    val mapPosition = mapMatcher.mapMatching(vehicle.position)
    val forward = route?.find { it.first == mapPosition.road }?.second

    // Compute perceptions   // TODO Externalize perceptions
    val obstacleData = generateObstaclePerceptions(vehicle)
    val trafficLights = generateTrafficLightPerceptions(route, vehicle)

    // Execute the behavior
    val driverBehavioralState = DriverBehavioralState(
        mapPosition.road,
        0,      // FIXME
        forward ?: true,
        obstacleData,
        50.0 unit Units.KilometersPerHour,
        route?.last()?.first?.end() ?: this.roadNetwork.roads[0].end())

    return vehicle.reachGoalBehavior(driverBehavioralState)
        .and(vehicle.respectTrafficLightBehavior(driverBehavioralState, trafficLights))
        .apply(deltaTime)
}

/**
 * Basic vehicle behavior with V2X support for intersections
 */
fun TrafficSimulation<Vehicle>.basicV2XVehicleBehavior (
    routes: Map<Vehicle, List<Pair<Road, Boolean>>?>,   // TODO Avoid having a route for all vehicles here
    vehicle: Vehicle,
    deltaTime: Double): DriverBehavioralAction {
    // Retrieve the computed route and the current road
    val route = routes[vehicle]
    val mapMatcher = MapMatchingProvider(roadNetwork)
    val mapPosition = mapMatcher.mapMatching(vehicle.position)
    val forward = route?.find { it.first == mapPosition.road }?.second

    // Compute perceptions   // TODO Externalize perceptions
    val obstacleData = generateObstaclePerceptions(vehicle)
    val trafficLights = generateTrafficLightPerceptions(route, vehicle)
    val intersectionRsus = generateIntersectionRsuPerceptions(route, vehicle)

    // Execute the behavior
    val driverBehavioralState = DriverBehavioralState(
        mapPosition.road,
        0,      // FIXME
        forward ?: true,
        obstacleData,
        50.0 unit Units.KilometersPerHour,
        route?.last()?.first?.end() ?: this.roadNetwork.roads[0].end())

    return vehicle.reachGoalBehavior(driverBehavioralState)
        .and(vehicle.respectTrafficLightBehavior(driverBehavioralState, trafficLights))
        .and(vehicle.respectV2XAuthorizationListBehavior(driverBehavioralState))
        .apply(deltaTime)
}
