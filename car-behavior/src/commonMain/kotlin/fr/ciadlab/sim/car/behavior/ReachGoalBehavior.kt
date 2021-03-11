package fr.ciadlab.sim.car.behavior

import fr.ciadlab.sim.car.behavior.lateral.lombardLateralControl
import fr.ciadlab.sim.car.behavior.lateral.purePursuit
import fr.ciadlab.sim.car.behavior.longitudinal.intelligentDriverModelControl
import fr.ciadlab.sim.math.algebra.*
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlin.math.sqrt

class ReachGoalBehavior(
    val vehicle: Vehicle,
    val driverBehavioralState: DriverBehavioralState,
    val longitudinalControl: (driverBehavioralState: DriverBehavioralState, vehicle: Vehicle) -> Double = Companion::idmLongitudinalControl,
    val lateralControl: (driverBehavioralState: DriverBehavioralState, vehicle: Vehicle) -> Double = Companion::curvatureFollowingLateralControl) : DriverBehavior {

    /**
     * Computes the action of the driver according the current state and the current behavior
     * @param deltaTime the simulation step time
     */
    override fun apply(deltaTime: Double): DriverBehavioralAction {
        val targetAcceleration = longitudinalControl.invoke(driverBehavioralState, vehicle)
        val targetWheelAngle = lateralControl.invoke(driverBehavioralState, vehicle)
        // return vehicle.update(targetAcceleration, targetWheelAngle, deltaTime)
        return DriverBehavioralAction(targetAcceleration, targetWheelAngle)
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
            val closestLeader = driverBehavioralState.leaders.minByOrNull { it.obstacleRelativePosition.norm }

            return if (closestLeader == null) {
                intelligentDriverModelControl(
                    Double.MAX_VALUE,
                    vehicle.velocity.norm,
                    0.0,
                    driverBehavioralState.maximumSpeed
                )
            } else {
                intelligentDriverModelControl(
                    closestLeader.obstacleRelativePosition.norm,
                    vehicle.velocity.norm,
                    closestLeader.obstacleRelativeVelocity.norm,
                    driverBehavioralState.maximumSpeed
                )
            }
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
