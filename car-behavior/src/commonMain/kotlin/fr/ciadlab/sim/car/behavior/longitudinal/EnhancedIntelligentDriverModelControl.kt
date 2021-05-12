package fr.ciadlab.sim.car.behavior.longitudinal

import kotlin.math.pow
import kotlin.math.tanh

/**
 * Applies IDM+ to define the desired acceleration of a vehicle
 * @param intervehicularDistance the distance between the two vehicles (0.0 means the vehicle are colliding)
 * @param velocity the current velocity of this vehicle
 * @param relativeVelocity the relative velocity of the preceding vehicle (negative if the preceding vehicle is slower)
 * @param desiredVelocity the desired velocity of the vehicle
 * @param maximumAcceleration the maximum acceleration of the vehicle
 * @param comfortableBrakingDeceleration the comfortable braking deceleration (a positive value)
 * @param minimumSpacing the minimum spacing between two vehicles
 * @param desiredHeadwayTime the desired headway time between two vehicles
 * @param aggressivenessExponent the aggressiveness exponent (the higher, the more aggressive)
 * @return the desired acceleration of the vehicle
 */
fun enhancedIntelligentDriverModelControl(
    intervehicularDistance: Double,
    velocity: Double,
    relativeVelocity: Double,
    desiredVelocity: Double,
    leaderAcceleration: Double,
    maximumAcceleration: Double = 2.0,
    comfortableBrakingDeceleration: Double = 4.0,
    minimumSpacing: Double = 2.0,
    desiredHeadwayTime: Double = 2.0,
    aggressivenessExponent: Double = 4.0,
    coolnessFactor: Double = 0.99): Double {
    val vl = velocity + relativeVelocity
    val aIdm = intelligentDriverModelControl(
        intervehicularDistance, velocity, relativeVelocity, desiredVelocity, maximumAcceleration,
        comfortableBrakingDeceleration, minimumSpacing, desiredHeadwayTime, aggressivenessExponent)

    val theta = if(velocity - vl >= 0) 1.0 else 0.0
    val aCah =
        if(vl * (velocity - vl) <= -2.0 * intervehicularDistance * leaderAcceleration)
            (velocity.pow(2.0) * leaderAcceleration) / (vl.pow(2.0) - 2.0 * intervehicularDistance * leaderAcceleration)
        else
            leaderAcceleration - ((velocity - vl).pow(2.0) * theta) / (2.0 * intervehicularDistance)


    return if (aIdm >= aCah) aIdm else (1.0 - coolnessFactor) * aIdm + coolnessFactor * (aCah + comfortableBrakingDeceleration * tanh((aIdm - aCah) / comfortableBrakingDeceleration))
}
