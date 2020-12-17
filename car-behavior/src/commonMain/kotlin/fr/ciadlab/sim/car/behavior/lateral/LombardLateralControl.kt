package fr.ciadlab.sim.car.behavior.lateral

import kotlin.math.*

/**
 * @param angleError the difference between the target yaw of the road (the yaw of the path point which is the closest
 *                   to the front axle) and the current yaw of the vehicle
 * @param lateralError the lateral error (distance between the path and the front axle)
 * @param velocity the current velocity of the vehicle
 * @param reactionTime the expected reaction time of the vehicle
 * @param curvature the curvature which can be defined as the norm of the difference between the tangent at the target
 *                  point and the direction of the vehicle
 * @param lookAheadDistance the look ahead distance associated to the curvature
 * @param wheelBase the wheel base of the vehicle
 */
fun lombardLateralControl(
    angleError: Double,
    lateralError: Double,
    left: Boolean,
    velocity: Double,
    reactionTime: Double,
    curvature: Double,
    lookAheadDistance: Double,
    wheelBase: Double
): Double {
    val targetPointDistance = velocity * 2.0 * reactionTime
    val sign = if(left) 1 else -1

    val k = 1.0

//    return sign * atan(2.0 * wheelBase * cos(angleError - atan(targetPointDistance / lateralError)) / sqrt(lateralError.pow(2.0) + targetPointDistance.pow(2.0))) +
//            asin(2.0 * wheelBase * asin(curvature / 2.0) / lookAheadDistance)
    return sign * atan(2.0 * wheelBase * cos(angleError - atan(targetPointDistance / lateralError)) / sqrt(lateralError.pow(2.0) + targetPointDistance.pow(2.0))) +
            asin(k * asin(curvature / 2.0) / PI)
}