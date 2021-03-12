package fr.ciadlab.sim.infrastructure.view.basics

import fr.ciadlab.sim.car.behavior.DriverBehavioralAction
import fr.ciadlab.sim.car.behavior.DriverBehavioralState
import fr.ciadlab.sim.car.behavior.default.reachGoalBehavior
import fr.ciadlab.sim.car.behavior.default.respectTrafficLightBehavior
import fr.ciadlab.sim.car.perception.obstacles.RadarPerceptionProvider
import fr.ciadlab.sim.car.perception.signals.TrafficLightPerceptionProvider
import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.math.algebra.project
import fr.ciadlab.sim.math.algebra.toVector3D
import fr.ciadlab.sim.physics.Units
import fr.ciadlab.sim.physics.unit
import fr.ciadlab.sim.traffic.TrafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle

/**
 * Simple functions that can be used as default behaviors.
 * @author Alexandre Lombard
 */

/**
 * Basic vehicle behavior
 */
fun TrafficSimulation<Vehicle>.basicVehicleBehavior (
    routes: Map<Vehicle, List<Pair<Road, Boolean>>?>,
    vehicle: Vehicle,
    deltaTime: Double): DriverBehavioralAction {
    // Retrieve the computed route and the current road
    val route = routes[vehicle]
    val currentRoad = route?.minByOrNull { it.first.points.project(vehicle.position.toVector3D()).distance }

    // Compute perceptions
    val radar = RadarPerceptionProvider()
    val radarData = radar.performRadarDetection(vehicle.position, vehicle.direction, this.vehicles)
    val trafficLights = TrafficLightPerceptionProvider()
    val perceivedTrafficLights = trafficLights.performTrafficLightDetection(
        route?.map { it.first } ?: emptyList(), this.roadNetwork.trafficLights.flatMap { it.trafficLights })

    // Execute the behavior
    val driverBehavioralState = DriverBehavioralState(
        currentRoad?.first ?: this.roadNetwork.roads[0],
        0,      // FIXME
        currentRoad?.second ?: true,
        radarData,
        50.0 unit Units.KilometersPerHour,
        route?.last()?.first?.end() ?: this.roadNetwork.roads[0].end())

    return vehicle.reachGoalBehavior(driverBehavioralState)
        .and(vehicle.respectTrafficLightBehavior(driverBehavioralState, perceivedTrafficLights))
        .apply(deltaTime)
}
