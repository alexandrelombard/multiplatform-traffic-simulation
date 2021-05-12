package fr.ciadlab.sim.car.behavior.longitudinal

import kotlin.math.max
import kotlin.math.min

fun mpcCruiseControl(
    velocity: Double,
    targetVelocity: Double,
    relativeVelocity: Double,
    leaderMaximumDeceleration: Double,
    intervehicularDistance: Double,
    maximumAcceleration: Double,
    headwayTime: Double,
    tau: Double
): Double {
    // Compute command to reach target speed
    val aTargetSpeed = min(maximumAcceleration, (targetVelocity - velocity) / tau)

    // Compute command to maintain safe headway time
    val leaderVelocity = velocity + relativeVelocity
    val leaderVelocityModel = { t: Double -> max(0.0, leaderVelocity - leaderMaximumDeceleration * t) }

    TODO("Not yet implemented")
}
