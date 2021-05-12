package fr.ciadlab.sim.car.behavior.longitudinal

import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Applies Gipps model to define the desired acceleration of a vehicle
 * @param intervehicularDistance the distance between the two vehicles (0.0 means the vehicle are colliding)
 * @param velocity the current velocity of this vehicle
 * @param relativeVelocity the relative velocity of the preceding vehicle (negative if the preceding vehicle is slower)
 * @param desiredVelocity the desired velocity of the vehicle
 * @param maximumAcceleration the maximum acceleration of the vehicle
 * @param minimumSpacing the minimum spacing between two vehicles
 * @param tau the reaction time
 * @return the desired acceleration of the vehicle
 */
fun gippsModelControl(
    intervehicularDistance: Double,
    velocity: Double,
    relativeVelocity: Double,
    desiredVelocity: Double,
    maximumAcceleration: Double = 2.0,
    maximumDeceleration: Double = 4.0,
    maximumLeaderDeceleration: Double = 8.0,
    minimumSpacing: Double = 2.0,
    tau: Double = 1.0
): Double {
    val leaderVelocity = velocity + relativeVelocity
    val vnTau = min(
        velocity + 2.5 * maximumAcceleration * tau * (1.0 - velocity / desiredVelocity) * sqrt(0.025 + velocity / desiredVelocity),
        maximumDeceleration * tau + sqrt(maximumDeceleration.pow(2.0) * tau.pow(2.0) - maximumDeceleration * (2.0 * (intervehicularDistance - minimumSpacing) - velocity * tau - leaderVelocity.pow(2.0) / maximumLeaderDeceleration))
    )

    // Return the acceleration: the expected difference of velocity over the time tau
    return (vnTau - velocity) / tau
}
