package fr.ciadlab.sim.car.behavior.default

import fr.ciadlab.sim.car.behavior.DriverBehavior
import fr.ciadlab.sim.car.behavior.DriverBehavioralAction
import fr.ciadlab.sim.car.behavior.DriverBehavioralState
import fr.ciadlab.sim.car.behavior.longitudinal.intelligentDriverModelControl
import fr.ciadlab.sim.infrastructure.intersection.IntersectionTrafficLight
import fr.ciadlab.sim.utils.UUID
import fr.ciadlab.sim.v2x.V2XCommunicationUnit
import fr.ciadlab.sim.v2x.V2XMessage
import fr.ciadlab.sim.vehicle.Vehicle

class RespectTransparentIntersectionManagerBehavior(
    val vehicle: Vehicle,
    val communicationUnit: V2XCommunicationUnit,
    val pendingMessages: MutableList<Pair<UUID, V2XMessage>>,
    val driverBehavioralState: DriverBehavioralState,
    val closestRoadSideUnit: UUID?,
    val longitudinalControl: (driverBehavioralState: DriverBehavioralState, vehicle: Vehicle, distanceToTrafficLight: Double) -> Double = Companion::idmLongitudinalControl,
) : DriverBehavior {
    override fun apply(deltaTime: Double): DriverBehavioralAction {
        if(closestRoadSideUnit != null) {

        }

        return DriverBehavioralAction(Double.POSITIVE_INFINITY, 0.0)
    }

    companion object {
        fun idmLongitudinalControl(driverBehavioralState: DriverBehavioralState, vehicle: Vehicle, distance: Double): Double {
            return intelligentDriverModelControl(
                distance,
                vehicle.velocity.norm,
                vehicle.velocity.norm,
                driverBehavioralState.maximumSpeed)
        }
    }
}

fun Vehicle.respectTransparentIntersectionManagerBehavior(
    driverBehavioralState: DriverBehavioralState, perceivedTrafficLights: List<IntersectionTrafficLight>): DriverBehavior {
    return RespectTrafficLightBehavior(this, driverBehavioralState, perceivedTrafficLights)
}
