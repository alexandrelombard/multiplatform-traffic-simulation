package fr.ciadlab.sim.car.behavior.default

import fr.ciadlab.sim.car.behavior.DriverAction
import fr.ciadlab.sim.car.behavior.DriverBehavior
import fr.ciadlab.sim.car.behavior.DriverDebugData
import fr.ciadlab.sim.car.behavior.DriverState
import fr.ciadlab.sim.car.behavior.lanechange.*
import fr.ciadlab.sim.car.behavior.lateral.LateralControlModel
import fr.ciadlab.sim.car.behavior.lateral.LateralControlModel.*
import fr.ciadlab.sim.car.behavior.lateral.lombardLateralControl
import fr.ciadlab.sim.car.behavior.lateral.purePursuit
import fr.ciadlab.sim.car.behavior.longitudinal.*
import fr.ciadlab.sim.car.perception.obstacles.ObstacleData
import fr.ciadlab.sim.car.perception.obstacles.RadarPerceptionProvider.Companion.findFollower
import fr.ciadlab.sim.car.perception.obstacles.RadarPerceptionProvider.Companion.findLeader
import fr.ciadlab.sim.math.algebra.*
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlin.math.sqrt

/** Internal alias for Lateral Control */
internal typealias LateralControl = (DriverState, Vehicle) -> Double

/** Internal alias for Longitudinal Control */
internal typealias LongitudinalControl = (DriverState, Vehicle, ObstacleData?) -> Double

// TODO Cleanup the code up-there

/**
 * Reach goal behavior: make a vehicle follow the given route, it combines:
 * - A longitudinal control model to avoid collision of the vehicles in the same lane
 * - A lateral control model to follow the road geometry
 * - A lane-change model to pass slower vehicles
 * @author Alexandre Lombard
 */
class ReachGoalBehavior(
    val vehicle: Vehicle,
    val driverState: DriverState,
    val longitudinalControl: LongitudinalControl = Companion::rtAccLongitudinalControl,
    val lateralControl: LateralControl = Companion::curvatureFollowingLateralControl,
    val laneChangeStrategy: LaneChangeStrategy = { d: DriverState, v: Vehicle -> mobilLaneSelection(d, v, mobilRtAcc) }) : DriverBehavior {

    /**
     * Computes the action of the driver according the current state and the current behavior
     * @param deltaTime the simulation step time
     */
    override fun apply(deltaTime: Double): DriverAction {
        var effectiveBehavioralState = driverState

        // Apply the MOBIL model
        val targetLane = laneChangeStrategy(effectiveBehavioralState, vehicle)
        val leftBlinker = targetLane > driverState.currentLaneIndex
        val rightBlinker = targetLane < driverState.currentLaneIndex
        effectiveBehavioralState = effectiveBehavioralState.copy(currentLaneIndex = targetLane)

        // Apply the longitudinal model for acceleration
        val closestLeader = findLeader(effectiveBehavioralState, vehicle, effectiveBehavioralState.currentLaneIndex)
        val targetAcceleration = longitudinalControl(effectiveBehavioralState, vehicle, closestLeader)

        // Apply the lateral model for control
        val targetWheelAngle = lateralControl(effectiveBehavioralState, vehicle)

        // Generate debug data (if required)
        val debugData = DriverDebugData(
            vehiclePosition = vehicle.position,
            leaderPosition = findLeader(driverState, vehicle, driverState.currentLaneIndex)?.getAbsolutePosition(vehicle.frame),
            newLeaderPosition = if(driverState.currentLaneIndex != targetLane) findLeader(driverState, vehicle, targetLane)?.getAbsolutePosition(vehicle.frame) else null,
            newFollowerPosition = if(driverState.currentLaneIndex != targetLane) findFollower(driverState, vehicle, targetLane)?.getAbsolutePosition(vehicle.frame) else null)

        return DriverAction(targetAcceleration, targetWheelAngle, leftBlinker, rightBlinker, debugData)
    }

    companion object {
        // region Lateral control solutions
        fun purePursuitLateralControl(driverState: DriverState, vehicle: Vehicle): Double {
            // We get the lane
            val laneWidth = 3.5
            val lane = driverState.lane(laneWidth)

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

        fun stanleyLateralControl(driverState: DriverState, vehicle: Vehicle): Double {
            // We get the lane
            val laneWidth = 3.5
            val lane = driverState.lane(laneWidth)

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

        fun curvatureFollowingLateralControl(driverState: DriverState, vehicle: Vehicle): Double {
            // We get the lane
            val laneWidth = 3.5
            val lane = driverState.lane(laneWidth)

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
        fun constantSpeedControl(driverState: DriverState, vehicle: Vehicle): Double {
            return 0.0
        }

        fun idmLongitudinalControl(driverState: DriverState, vehicle: Vehicle, closestLeader: ObstacleData?): Double {
            return if (closestLeader == null) {
                intelligentDriverModelControl(
                    Double.MAX_VALUE,
                    vehicle.velocity.norm,
                    0.0,
                    driverState.maximumSpeed)
            } else {
                intelligentDriverModelControl(
                    closestLeader.obstacleRelativePosition.norm,
                    vehicle.velocity.norm,
                    vehicle.speed - closestLeader.obstacleRelativeVelocity.norm,
                    driverState.maximumSpeed,
                    minimumSpacing = 5.0)
            }
        }

        fun rtAccLongitudinalControl(driverState: DriverState, vehicle: Vehicle, closestLeader: ObstacleData?): Double {
            return if(closestLeader == null) {
                reactionTimeAdaptiveCruiseControl(vehicle.speed, driverState.maximumSpeed, 0.0, Double.MAX_VALUE)
            } else {
                reactionTimeAdaptiveCruiseControl(vehicle.speed, driverState.maximumSpeed, closestLeader.obstacleRelativeVelocity.norm, closestLeader.obstacleRelativePosition.norm)
            }
        }

        fun gippsModelLongitudinalControl(driverState: DriverState, vehicle: Vehicle, closestLeader: ObstacleData?): Double {
            return if(closestLeader == null) {
                gippsModelControl(Double.MAX_VALUE, vehicle.speed, 0.0, driverState.maximumSpeed)
            } else {
                gippsModelControl(closestLeader.obstacleRelativePosition.norm, vehicle.speed, closestLeader.obstacleRelativeVelocity.norm - vehicle.speed, driverState.maximumSpeed)
            }
        }

        fun mpcAccLongitudinalControl(driverState: DriverState, vehicle: Vehicle, closestLeader: ObstacleData?): Double {
            return if(closestLeader == null) {
                mpcCruiseControl(
                    vehicle.velocity.norm, driverState.maximumSpeed, 0.0, Double.MAX_VALUE,-8.0,
                    -2.0, 2.0, 2.0, 10.0, 0.4)
            } else {
                mpcCruiseControl(
                    vehicle.velocity.norm, driverState.maximumSpeed,
                    vehicle.speed - closestLeader.obstacleRelativeVelocity.norm, closestLeader.obstacleRelativePosition.norm,
                    -8.0, -2.0, 2.0, 2.0,
                    10.0, 0.4)
            }
        }
        // endregion

        // region Lane-change strategies
        /** Builds a MOBIL longitudinal model from the internal definition of a longitudinal model */
        fun mobilLaneSelection(driverState: DriverState, vehicle: Vehicle, longitudinalModel: LongitudinalModel): Int {
            val laneIndex = driverState.currentLaneIndex
            val leftLaneIndex = driverState.currentRoad.leftLaneIndex(laneIndex)
            val rightLaneIndex = driverState.currentRoad.rightLaneIndex(laneIndex)

            val halfLaneWidth = 3.5 / 2.0   // FIXME Use a computed value

            val selectedLaneIndex = rightLaneIndex ?: leftLaneIndex

            if (selectedLaneIndex != null) {
                // Check if we can go back to the right lane
                val newLeader = findLeader(driverState, vehicle, selectedLaneIndex)
                val currentLeader = findLeader(driverState, vehicle, driverState.currentLaneIndex)
                val newFollower = findFollower(driverState, vehicle, selectedLaneIndex)

                val mobilState = MobilState(
                    vehicle.speed,
                    driverState.maximumSpeed,
                    if(newFollower == null) Double.POSITIVE_INFINITY else newFollower.obstacleRelativePosition.norm,
                    if(newFollower == null) 0.0 else newFollower.obstacleRelativeVelocity.norm - vehicle.speed,
                    newLeader?.obstacleRelativePosition?.y ?: Double.POSITIVE_INFINITY,
                    vehicle.speed - (newLeader?.obstacleRelativeVelocity?.norm ?: 0.0),
                    currentLeader?.obstacleRelativePosition?.y ?: Double.POSITIVE_INFINITY,
                    vehicle.speed - (currentLeader?.obstacleRelativeVelocity?.y ?: 0.0))

                if(mobilState.shouldLaneChangeBePerformed(carFollowingModel = longitudinalModel)) {
                    return selectedLaneIndex
                }
            }

            // No change
            return laneIndex
        }
        // endregion
    }
}

/**
 * Returns the "reach goal behavior" with the selected models
 * @param driverState the current state of the driver
 * @param longitudinalControl the longitudinal control model
 * @param lateralControl the lateral control model
 * @param laneChangeStrategy the lane change strategy model
 */
fun Vehicle.reachGoalBehavior(
    driverState: DriverState,
    longitudinalControl: (driverState: DriverState, vehicle: Vehicle, leader: ObstacleData?) -> Double =
        ReachGoalBehavior.Companion::rtAccLongitudinalControl,
    lateralControl: (driverState: DriverState, vehicle: Vehicle) -> Double =
        ReachGoalBehavior.Companion::curvatureFollowingLateralControl,
    laneChangeStrategy: (driverState: DriverState, vehicle: Vehicle) -> Int = { d, v ->
        // Curryied lane-change strategy, make sure to match MOBIL longitudinal model with the longitudinal control
        ReachGoalBehavior.mobilLaneSelection(d, v, mobilRtAcc)
    }
): DriverBehavior {
    return ReachGoalBehavior(this, driverState, longitudinalControl, lateralControl, laneChangeStrategy)
}

/**
 * Returns the "reach goal behavior" with the selected models
 * @param driverState the current state of the driver
 * @param longitudinalControlModel the longitudinal control model
 * @param lateralControlModel the lateral control model
 */
fun Vehicle.reachGoalBehavior(
    driverState: DriverState,
    longitudinalControlModel: LongitudinalControlModel,
    lateralControlModel: LateralControlModel,
    laneChangeModel: LaneChangeModel) : DriverBehavior {
    val longitudinalControl = when(longitudinalControlModel) {
        LongitudinalControlModel.ENHANCED_IDM -> TODO()
        LongitudinalControlModel.IDM -> ReachGoalBehavior.Companion::idmLongitudinalControl
        LongitudinalControlModel.RT_ACC -> ReachGoalBehavior.Companion::rtAccLongitudinalControl
        LongitudinalControlModel.MPC -> ReachGoalBehavior.Companion::mpcAccLongitudinalControl
        LongitudinalControlModel.GIPPS -> TODO()
    }

    val lateralControl = when(lateralControlModel) {
        PURE_PURSUIT -> ReachGoalBehavior.Companion::purePursuitLateralControl
        STANLEY -> ReachGoalBehavior.Companion::stanleyLateralControl
        CURVATURE_BASED -> ReachGoalBehavior.Companion::curvatureFollowingLateralControl
    }

    val laneChangeStrategy = when(laneChangeModel) {
        LaneChangeModel.MOBIL -> {
            when(longitudinalControlModel) {
                LongitudinalControlModel.ENHANCED_IDM -> TODO()
                LongitudinalControlModel.IDM -> { d: DriverState, v: Vehicle -> ReachGoalBehavior.mobilLaneSelection(d, v, mobilIdm) }
                LongitudinalControlModel.RT_ACC -> { d: DriverState, v: Vehicle -> ReachGoalBehavior.mobilLaneSelection(d, v, mobilRtAcc) }
                LongitudinalControlModel.MPC -> { d: DriverState, v: Vehicle -> ReachGoalBehavior.mobilLaneSelection(d, v, mobilMpcAcc) }
                LongitudinalControlModel.GIPPS -> TODO()
            }
        }
    }

    return this.reachGoalBehavior(driverState, longitudinalControl, lateralControl, laneChangeStrategy)
}
