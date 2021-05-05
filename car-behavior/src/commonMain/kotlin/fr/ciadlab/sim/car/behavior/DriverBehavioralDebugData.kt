package fr.ciadlab.sim.car.behavior

import fr.ciadlab.sim.math.algebra.Vector2D

/**
 * Represents a set of data computed by driver behaviors, this storage class is dedicated to debug purposes
 * @author Alexandre Lombard
 */
data class DriverBehavioralDebugData(
    val vehiclePosition: Vector2D? = null,
    val leaderPosition: Vector2D? = null,
    val newLeaderPosition: Vector2D? = null,
    val newFollowerPosition: Vector2D? = null) {
    /**
     * Composes two driver debug data: the added ones overrides the previous ones
     * @param debugData the added debug data
     */
    fun and(debugData: DriverBehavioralDebugData?): DriverBehavioralDebugData {
        return if(debugData == null) {
            this
        } else {
            DriverBehavioralDebugData(
                vehiclePosition = debugData.vehiclePosition ?: vehiclePosition,
                leaderPosition = debugData.leaderPosition ?: leaderPosition,
                newLeaderPosition = debugData.newLeaderPosition ?: newLeaderPosition,
                newFollowerPosition = debugData.newFollowerPosition ?: newFollowerPosition)
        }
    }
}
