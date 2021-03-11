package fr.ciadlab.sim.car.behavior

import fr.ciadlab.sim.car.behavior.longitudinal.intelligentDriverModelControl
import fr.ciadlab.sim.infrastructure.intersection.IntersectionTrafficLight
import fr.ciadlab.sim.infrastructure.intersection.TrafficLightState
import fr.ciadlab.sim.vehicle.Vehicle

class RespectTrafficLightBehavior(
    val vehicle: Vehicle,
    val driverBehavioralState: DriverBehavioralState,
    val perceivedTrafficLights: List<IntersectionTrafficLight>,
    val longitudinalControl: (driverBehavioralState: DriverBehavioralState, vehicle: Vehicle, distanceToTrafficLight: Double) -> Double = RespectTrafficLightBehavior.Companion::idmLongitudinalControl
) : DriverBehavior {
    override fun apply(deltaTime: Double): DriverBehavioralAction {
        // TODO Optimize
        // TODO Consider all potential traffic lights
        // TODO Do not consider a traffic light if we are in the intersection
        // We associate to each traffic light the distance from the vehicle to traffic light, then we take the min if it
        // exists
        val closestTrafficLight =
            this.perceivedTrafficLights
                .associateWith {
                    val laneConnector = it.laneConnectors.firstOrNull { it.sourceRoad == driverBehavioralState.currentRoad }
                    laneConnector?.sourcePoint?.xy?.distance(vehicle.position) ?: Double.POSITIVE_INFINITY
                }.minByOrNull { it.value }

        if(closestTrafficLight != null && closestTrafficLight.key.state == TrafficLightState.RED) {
            // Adapt acceleration, the traffic light is RED
            return DriverBehavioralAction(longitudinalControl(driverBehavioralState, vehicle, closestTrafficLight.value), 0.0)
        }

        return DriverBehavioralAction(Double.POSITIVE_INFINITY, 0.0)
    }

    companion object {
        fun idmLongitudinalControl(driverBehavioralState: DriverBehavioralState, vehicle: Vehicle, distanceToTrafficLight: Double): Double {
            return intelligentDriverModelControl(
                    distanceToTrafficLight,
                    vehicle.velocity.norm,
                    vehicle.velocity.norm,
                    driverBehavioralState.maximumSpeed)
        }
    }
}

fun Vehicle.respectTrafficLightBehavior(
    driverBehavioralState: DriverBehavioralState, perceivedTrafficLights: List<IntersectionTrafficLight>): DriverBehavior {
    return RespectTrafficLightBehavior(this, driverBehavioralState, perceivedTrafficLights)
}
