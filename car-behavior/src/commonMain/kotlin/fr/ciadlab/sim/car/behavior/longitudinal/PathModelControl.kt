package fr.ciadlab.sim.car.behavior.longitudinal

/**
 * Applies PATH to define the desired acceleration of a vehicle
 * @param leaderAcceleration the leader acceleration
 * @param c1 parameter C1 (0 <= C1 <= 1)
 * @param wn bandwidth of the controller
 * @param epsilon damping ratio
 * @return the desired acceleration of the vehicle
 * @see https://link.springer.com/referenceworkentry/10.1007%2F978-0-85729-085-4_8
 */
fun pathModelControl(
    intervehicularDistance: Double,
    desiredSpacing: Double,
    velocity: Double,
    relativeVelocity: Double,
    desiredVelocity: Double,
    leaderAcceleration: Double,
    maximumAcceleration: Double = 2.0,
    comfortableBrakingDeceleration: Double = 4.0,
    minimumSpacing: Double = 2.0,
    desiredHeadwayTime: Double = 2.0,
    aggressivenessExponent: Double = 4.0,
    c1: Double = 0.5,
    wn: Double = 1.0,
    epsilon: Double = 1.0): Double {
    val epsI = -intervehicularDistance + desiredSpacing

    val acc = (1.0 - c1) * leaderAcceleration

    TODO("Not yet implemented")
}
