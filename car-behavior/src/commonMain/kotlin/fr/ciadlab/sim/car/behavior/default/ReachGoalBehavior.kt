package fr.ciadlab.sim.car.behavior.default

import fr.ciadlab.sim.car.behavior.DriverBehavior
import fr.ciadlab.sim.car.behavior.DriverBehavioralAction
import fr.ciadlab.sim.car.behavior.DriverBehavioralState
import fr.ciadlab.sim.car.behavior.lanechange.MobilState
import fr.ciadlab.sim.car.behavior.lateral.lombardLateralControl
import fr.ciadlab.sim.car.behavior.lateral.purePursuit
import fr.ciadlab.sim.car.behavior.longitudinal.intelligentDriverModelControl
import fr.ciadlab.sim.car.perception.obstacles.RadarPerceptionProvider.Companion.findFollower
import fr.ciadlab.sim.car.perception.obstacles.RadarPerceptionProvider.Companion.findLeader
import fr.ciadlab.sim.math.algebra.*
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlin.math.sqrt

class ReachGoalBehavior(
    val vehicle: Vehicle,
    val driverBehavioralState: DriverBehavioralState,
    val longitudinalControl: (DriverBehavioralState, Vehicle) -> Double = Companion::idmLongitudinalControl,
    val lateralControl: (DriverBehavioralState, Vehicle) -> Double = Companion::curvatureFollowingLateralControl,
    val laneChangeStrategy: (DriverBehavioralState, Vehicle) -> Int = Companion::mobilLaneSelection)
    : DriverBehavior {

    /**
     * Computes the action of the driver according the current state and the current behavior
     * @param deltaTime the simulation step time
     */
    override fun apply(deltaTime: Double): DriverBehavioralAction {
        var effectiveBehavioralState = driverBehavioralState

        // Apply the longitudinal model for acceleration
        val targetAcceleration = longitudinalControl(effectiveBehavioralState, vehicle)

        // Apply the MOBIL model
        val targetLane = laneChangeStrategy(effectiveBehavioralState, vehicle)
        val leftBlinker = targetLane > driverBehavioralState.currentLaneIndex
        val rightBlinker = targetLane < driverBehavioralState.currentLaneIndex
         effectiveBehavioralState = effectiveBehavioralState.copy(currentLaneIndex = targetLane)

        val targetWheelAngle = lateralControl(effectiveBehavioralState, vehicle)

        return DriverBehavioralAction(targetAcceleration, targetWheelAngle, leftBlinker, rightBlinker)
    }

    companion object {
        // region Lateral control solutions
        fun purePursuitLateralControl(driverBehavioralState: DriverBehavioralState, vehicle: Vehicle): Double {
            // We get the lane
            val laneWidth = 3.5
            val lane = driverBehavioralState.lane(laneWidth)

            // We compute the parameters
            val projectionData = lane.project(vehicle.position.toVector3D())
            val lookaheadDistance = min(30.0, max(5.0, 2.0 * vehicle.velocity.norm))
            val lookaheadLength = (projectionData.length + lookaheadDistance) % lane.length()
            val lookaheadPoint = lane.pointAtLength(lookaheadLength)

            // We compute the command
            return purePursuit(
                position = vehicle.position,
                frontAxleOffset = vehicle.wheelBase / 2.0,
                rearAxleOffset = -vehicle.wheelBase / 2.0,
                yaw = vehicle.direction.alpha,
                targetPoint = lookaheadPoint.xy
            )
        }

        fun stanleyLateralControl(driverBehavioralState: DriverBehavioralState, vehicle: Vehicle): Double {
            // We get the lane
            val laneWidth = 3.5
            val lane = driverBehavioralState.lane(laneWidth)

            // We compute the parameters
            val frontAxlePosition =
                (vehicle.position + Vector2D(vehicle.wheelBase / 2.0, vehicle.direction)).toVector3D()
            val projectionData = lane.project(frontAxlePosition)
            val distance = projectionData.distance
            val polylineSegment = (projectionData.segmentEnd - projectionData.segmentBegin)
            val side =
                (projectionData.segmentEnd - projectionData.segmentBegin).xy.angle((frontAxlePosition - projectionData.segmentBegin).xy)
            val left = side > 0.0
            val angleError = polylineSegment.xy.angle(vehicle.direction)
            val lateralError = distance * if (left) 1 else -1

            // We compute the command
            return fr.ciadlab.sim.car.behavior.lateral.stanleyLateralControl(
                angleError = angleError,
                lateralError = lateralError,
                gain = 5.0,
                velocity = vehicle.velocity.norm
            )
        }

        fun curvatureFollowingLateralControl(driverBehavioralState: DriverBehavioralState, vehicle: Vehicle): Double {
            // We get the lane
            val laneWidth = 3.5
            val lane = driverBehavioralState.lane(laneWidth)

            // We compute the parameters
            val frontAxlePosition =
                (vehicle.position + Vector2D(vehicle.wheelBase / 2.0, vehicle.direction)).toVector3D()
            val projectionData = lane.project(frontAxlePosition)
            val distance = projectionData.distance
            val polylineSegment = (projectionData.segmentEnd - projectionData.segmentBegin)
            val side =
                (projectionData.segmentEnd - projectionData.segmentBegin).xy.angle((frontAxlePosition - projectionData.segmentBegin).xy)
            val left = side > 0.0
            val angleError = polylineSegment.xy.angle(vehicle.direction)
            val lateralError = distance * if (left) 1 else -1

            val lookAheadDistance = max(2.0, sqrt(vehicle.velocity.norm) * 2.0)
            val lookAheadLength =
                if (projectionData.length + lookAheadDistance < lane.length()) projectionData.length + lookAheadDistance else projectionData.length + lookAheadDistance - lane.length()
            val lookAheadTangent =
                (lane.pointAtLength(lookAheadLength + 1) - lane.pointAtLength(lookAheadLength - 1)).normalize()
            val subtraction = lookAheadTangent.xy - vehicle.direction
            val curvature = lookAheadTangent.xy.angle(vehicle.direction).sign * subtraction.norm

            // We compute the command
            return lombardLateralControl(
                angleError = angleError,
                lateralError = lateralError,
                left = left,
                velocity = vehicle.velocity.norm,
                reactionTime = 0.1,     // TODO Should be externalized, this impacts the behavior of the command
                curvature = curvature,
                lookAheadDistance = lookAheadDistance,
                wheelBase = vehicle.wheelBase
            )
        }
        // endregion

        // region Longitudinal control
        fun constantSpeedControl(driverBehavioralState: DriverBehavioralState, vehicle: Vehicle): Double {
            return 0.0
        }

        fun idmLongitudinalControl(driverBehavioralState: DriverBehavioralState, vehicle: Vehicle): Double {
            val closestLeader = findLeader(driverBehavioralState, vehicle, driverBehavioralState.currentLaneIndex)

            return if (closestLeader == null) {
                intelligentDriverModelControl(
                    Double.MAX_VALUE,
                    vehicle.velocity.norm,
                    0.0,
                    driverBehavioralState.maximumSpeed)
            } else {
                intelligentDriverModelControl(
                    closestLeader.obstacleRelativePosition.norm,
                    vehicle.velocity.norm,
                    vehicle.speed - closestLeader.obstacleRelativeVelocity.norm,
                    driverBehavioralState.maximumSpeed,
                    minimumSpacing = 5.0)
            }
        }
        // endregion

        // region Lane-change strategies
        fun mobilLaneSelection(driverBehavioralState: DriverBehavioralState, vehicle: Vehicle): Int {
            val laneIndex = driverBehavioralState.currentLaneIndex
            val leftLaneIndex = driverBehavioralState.currentRoad.leftLaneIndex(laneIndex)
            val rightLaneIndex = driverBehavioralState.currentRoad.rightLaneIndex(laneIndex)

            val halfLaneWidth = 3.5 / 2.0   // FIXME Use a computed value

            if (rightLaneIndex != null) {
                // Check if we can go back to the right lane
                val newLeader = findLeader(driverBehavioralState, vehicle, rightLaneIndex)
                val currentLeader = findLeader(driverBehavioralState, vehicle, driverBehavioralState.currentLaneIndex)
                val newFollower = findFollower(driverBehavioralState, vehicle, rightLaneIndex)

                val mobilState = MobilState(
                    vehicle.speed,
                    if(newFollower == null) Double.POSITIVE_INFINITY else -newFollower.obstacleRelativePosition.y,
                    if(newFollower == null) 0.0 else newFollower.obstacleRelativeVelocity.norm - vehicle.speed,
                    newLeader?.obstacleRelativePosition?.y ?: Double.POSITIVE_INFINITY,
                    vehicle.speed - (newLeader?.obstacleRelativeVelocity?.norm ?: 0.0),
                    currentLeader?.obstacleRelativePosition?.y ?: Double.POSITIVE_INFINITY,
                    vehicle.speed - (currentLeader?.obstacleRelativeVelocity?.y ?: 0.0))

                if((currentLeader == null && newLeader == null) || mobilState.shouldLaneChangeBePerformed(
                        carFollowingModel = { distance, relativeSpeed, speed ->
                        intelligentDriverModelControl(
                            distance, speed, relativeSpeed, driverBehavioralState.maximumSpeed, minimumSpacing = 5.0)
                    })) {
                    return rightLaneIndex
                }
            } else if(leftLaneIndex != null) {
                // It is possible to pass
                val newLeader = findLeader(driverBehavioralState, vehicle, leftLaneIndex)
                val currentLeader = findLeader(driverBehavioralState, vehicle, driverBehavioralState.currentLaneIndex)
                val newFollower = findFollower(driverBehavioralState, vehicle, leftLaneIndex)

                val mobilState = MobilState(
                    vehicle.speed,
                    if(newFollower == null) Double.POSITIVE_INFINITY else -newFollower.obstacleRelativePosition.y,
                    if(newFollower == null) 0.0 else newFollower.obstacleRelativeVelocity.norm - vehicle.speed,
                    newLeader?.obstacleRelativePosition?.y ?: Double.POSITIVE_INFINITY,
                    vehicle.speed - (newLeader?.obstacleRelativeVelocity?.y ?: 0.0),
                    currentLeader?.obstacleRelativePosition?.y ?: Double.POSITIVE_INFINITY,
                    vehicle.speed - (currentLeader?.obstacleRelativeVelocity?.y ?: 0.0))

                if(mobilState.shouldLaneChangeBePerformed(carFollowingModel = { distance, relativeSpeed, speed ->
                        intelligentDriverModelControl(
                            distance, speed, relativeSpeed, driverBehavioralState.maximumSpeed, minimumSpacing = 5.0)
                    })) {
                    return leftLaneIndex
                }
            }

            // No change
            return laneIndex
        }
        // endregion
    }
}

fun Vehicle.reachGoalBehavior(
    driverBehavioralState: DriverBehavioralState,
    longitudinalControl: (driverBehavioralState: DriverBehavioralState, vehicle: Vehicle) -> Double = ReachGoalBehavior.Companion::idmLongitudinalControl,
    lateralControl: (driverBehavioralState: DriverBehavioralState, vehicle: Vehicle) -> Double = ReachGoalBehavior.Companion::curvatureFollowingLateralControl
): DriverBehavior {
    return ReachGoalBehavior(this, driverBehavioralState, longitudinalControl, lateralControl)
}
