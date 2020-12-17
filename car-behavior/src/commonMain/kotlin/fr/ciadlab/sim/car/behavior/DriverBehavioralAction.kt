package fr.ciadlab.sim.car.behavior

/**
 * Represents the action of a driver on a vehicle
 * @author Alexandre Lombard
 */
data class DriverBehavioralAction(
    /** The desired acceleration of the vehicle in m.s-² */
    val targetAcceleration: Double,
    /** The desired wheel angle */
    val targetWheelAngle: Double)