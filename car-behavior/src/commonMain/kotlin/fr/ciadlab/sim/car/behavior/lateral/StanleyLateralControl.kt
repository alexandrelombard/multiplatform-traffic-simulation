package fr.ciadlab.sim.car.behavior.lateral

import kotlin.math.atan

/**
 * Computes the desired angle for the wheels using Stanley's method
 * @param angleError the difference between the target yaw of the road (the yaw of the path point which is the closest
 *                   to the front axle) and the current yaw of the vehicle
 * @param lateralError the lateral error (distance between the path and the front axle)
 * @param velocity the current velocity of the vehicle
 * @param gain the gain factor (lower means the movement will be smooth, higher means the movement will
 *             be sharp - eventually unstable) ; it typically ranges from 1.0 to 10.0
 * @return the desired angle for the wheels, it may exceed the physical capacity of the vehicle
 */
fun stanleyLateralControl(
    angleError: Double,
    lateralError: Double,
    velocity: Double,
    gain: Double = 5.0
) : Double {
    return angleError + atan(gain * lateralError / velocity)
}