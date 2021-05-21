package fr.ciadlab.sim.car.behavior.default

import fr.ciadlab.sim.car.behavior.DriverBehavior
import fr.ciadlab.sim.car.behavior.DriverAction
import fr.ciadlab.sim.car.behavior.DriverState
import fr.ciadlab.sim.car.behavior.longitudinal.intelligentDriverModelControl
import fr.ciadlab.sim.utils.UUID
import fr.ciadlab.sim.v2x.V2XCommunicationUnit
import fr.ciadlab.sim.v2x.V2XMessage
import fr.ciadlab.sim.v2x.intersection.MessageType
import fr.ciadlab.sim.v2x.intersection.TransparentIntersectionManagerMessage
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.math.max

/**
 * Implementation of a driver behavior regarding the interaction with RSU units for intersection management.
 * It relies on the Transparent Intersection Manager protocol.
 * @author Alexandre Lombard
 */
class RespectV2XAuthorizationListBehavior(
    val vehicle: Vehicle,
    val communicationUnit: V2XCommunicationUnit,
    val authorizationList: List<Pair<UUID, V2XMessage>>,
    val driverState: DriverState,
    val closestRoadSideUnit: UUID?,
    val distanceToIntersectionEntrance: Double,
    val distanceToIntersectionExit: Double,
    val longitudinalControl: (DriverState, Double, Double, Double) -> Double = Companion::idmLongitudinalControl,
) : DriverBehavior {
    override fun apply(deltaTime: Double): DriverAction {
        if(closestRoadSideUnit != null) {
            val authorizations = authorizationList.map { TransparentIntersectionManagerMessage.parse(it.second.data) }
            val position = authorizations.indexOfFirst { it.identifier == communicationUnit.identifier }
            if(position >= 0) {
                // If we are in the authorization list, check the position
                if(distanceToIntersectionExit > 0) {
                    // Still in the intersection, we send an update message
                    communicationUnit.unicast(
                        closestRoadSideUnit,
                        TransparentIntersectionManagerMessage(
                            MessageType.UPDATE,
                            communicationUnit.identifier,
                            distanceToIntersectionEntrance,
                            vehicle.speed))

                    // Depending on the position in the list, we adjust the speed accordingly
                    // TODO Consider lane connectors to optimize the movement
                    if(position == 0) {
                        // Free speed
                        return DriverAction(Double.POSITIVE_INFINITY, 0.0)
                    } else {
                        // Ajust speed according to leader
                        val leader = authorizations[position - 1]
                        return DriverAction(
                            longitudinalControl(
                                driverState,
                                vehicle.speed,
                                vehicle.speed - leader.speed,
                                max(0.0, leader.distance) + distanceToIntersectionEntrance),    // TODO Check this
                            0.0)
                    }
                } else  {
                    // Leaving the intersection, we send an exit message
                    communicationUnit.unicast(
                        closestRoadSideUnit,
                        TransparentIntersectionManagerMessage(
                            MessageType.UPDATE,
                            communicationUnit.identifier,
                            distanceToIntersectionEntrance,
                            vehicle.speed))
                    // And we are free
                    return DriverAction(Double.POSITIVE_INFINITY, 0.0)
                }
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
                    // Adjust the speed (full stop at intersection entrance)
                    return DriverAction(
                        longitudinalControl(driverState, vehicle.speed, vehicle.speed, distanceToIntersectionEntrance), 0.0)
                } else {
                    // Free speed: we have left the intersection
                    return DriverAction(Double.POSITIVE_INFINITY, 0.0)
                }
            }
        }

        return DriverAction(Double.POSITIVE_INFINITY, 0.0)
    }

    companion object {
        fun idmLongitudinalControl(
            driverState: DriverState,
            vehicleSpeed: Double, relativeLeaderSpeed: Double, distance: Double): Double {
            return intelligentDriverModelControl(
                distance,
                vehicleSpeed,
                relativeLeaderSpeed,
                driverState.maximumSpeed)
        }
    }
}

fun Vehicle.respectV2XAuthorizationListBehavior(
    communicationUnit: V2XCommunicationUnit,
    authorizationList: List<Pair<UUID, V2XMessage>>,
    closestRoadSideUnit: UUID?,
    distanceToIntersectionEntrance: Double,
    distanceToIntersectionExit: Double,
    driverState: DriverState): DriverBehavior {
    return RespectV2XAuthorizationListBehavior(
        this,
        communicationUnit,
        authorizationList,
        driverState,
        closestRoadSideUnit,
        distanceToIntersectionEntrance,
        distanceToIntersectionExit)
}
