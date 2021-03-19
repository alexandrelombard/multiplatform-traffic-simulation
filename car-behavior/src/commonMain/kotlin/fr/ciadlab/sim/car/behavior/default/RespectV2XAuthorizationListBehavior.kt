package fr.ciadlab.sim.car.behavior.default

import fr.ciadlab.sim.car.behavior.DriverBehavior
import fr.ciadlab.sim.car.behavior.DriverBehavioralAction
import fr.ciadlab.sim.car.behavior.DriverBehavioralState
import fr.ciadlab.sim.car.behavior.longitudinal.intelligentDriverModelControl
import fr.ciadlab.sim.infrastructure.intersection.IntersectionTrafficLight
import fr.ciadlab.sim.utils.UUID
import fr.ciadlab.sim.v2x.V2XCommunicationUnit
import fr.ciadlab.sim.v2x.V2XMessage
import fr.ciadlab.sim.v2x.intersection.MessageType
import fr.ciadlab.sim.v2x.intersection.TransparentIntersectionManagerMessage
import fr.ciadlab.sim.vehicle.Vehicle

data class AuthorizationMessage(
    val
)

class RespectV2XAuthorizationListBehavior(
    val vehicle: Vehicle,
    val communicationUnit: V2XCommunicationUnit,
    val authorizationList: MutableList<Pair<UUID, V2XMessage>>,
    val driverBehavioralState: DriverBehavioralState,
    val closestRoadSideUnit: UUID?,
    val distanceToIntersectionEntrance: Double,
    val distanceToIntersectionExit: Double,
    val longitudinalControl: (DriverBehavioralState, Vehicle, Double) -> Double = Companion::idmLongitudinalControl,
) : DriverBehavior {
    override fun apply(deltaTime: Double): DriverBehavioralAction {
        if(closestRoadSideUnit != null) {
            val authorizations = authorizationList.map { TransparentIntersectionManagerMessage.parse(it.second.data) }
            if(authorizations.any { it.identifier == communicationUnit.identifier }) {
                // If we are in the authorization list, check the position

            } else {
                // If we aren't in the authorization list
                if(distanceToIntersectionEntrance > 0) {
                    // Send a request to enter
                    communicationUnit.unicast(
                        closestRoadSideUnit,
                        TransparentIntersectionManagerMessage(
                            MessageType.APPROACH,
                            communicationUnit.identifier,
                            distanceToIntersectionEntrance,
                            vehicle.speed))
                    // Adjust the speed
                    return DriverBehavioralAction(
                        longitudinalControl(driverBehavioralState, vehicle, distanceToIntersectionEntrance), 0.0)
                } else {
                    // Free speed: we have left the intersection
                    return DriverBehavioralAction(Double.POSITIVE_INFINITY, 0.0)
                }
            }
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

fun Vehicle.respectV2XAuthorizationListBehavior(
    driverBehavioralState: DriverBehavioralState, perceivedTrafficLights: List<IntersectionTrafficLight>): DriverBehavior {
    return RespectTrafficLightBehavior(this, driverBehavioralState, perceivedTrafficLights)
}
