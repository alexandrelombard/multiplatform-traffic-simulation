package fr.ciadlab.sim.car.behavior

import kotlin.math.min

/**
 * Represents the action of a driver on a vehicle
 * @author Alexandre Lombard
 */
data class DriverAction(
    /** The desired acceleration of the vehicle in m.s-² */
    val targetAcceleration: Double,
    /** The desired wheel angle */
    val targetWheelAngle: Double,
    /** True if the left blinker should be active */
    val blinkerLeft: Boolean = false,
    /** True if the right blinker should be active */
    val blinkerRight: Boolean = false,
    /** Optional debug data */
    val debugData: DriverDebugData? = null) {
    /**
     * Composes two driver behavioral action so the acceleration is limited to the minimum and the wheel angle
     * is applied with priority to the original action.
     * @param action the other action to compose this one with
     * @return the composed behavioral action
     */
    fun and(action: DriverAction): DriverAction {
        return DriverAction(
            targetAcceleration = min(targetAcceleration, action.targetAcceleration),
            targetWheelAngle = targetWheelAngle,
            blinkerLeft = blinkerLeft || action.blinkerLeft,
            blinkerRight = blinkerRight || action.blinkerRight,
            debugData = debugData?.and(action.debugData) ?: action.debugData
        )
    }
}
