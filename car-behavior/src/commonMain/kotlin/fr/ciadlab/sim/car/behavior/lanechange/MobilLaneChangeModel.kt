package fr.ciadlab.sim.car.behavior.lanechange

import fr.ciadlab.sim.car.behavior.longitudinal.LongitudinalModel
import fr.ciadlab.sim.car.behavior.longitudinal.intelligentDriverModelControl
import fr.ciadlab.sim.car.behavior.longitudinal.mpcCruiseControl
import fr.ciadlab.sim.car.behavior.longitudinal.reactionTimeAdaptiveCruiseControl

// region Wrapped longitudinal models
val mobilIdm: LongitudinalModel = { distance, relativeSpeed, speed, maximumSpeed ->
    intelligentDriverModelControl(distance, speed, relativeSpeed, maximumSpeed, minimumSpacing = 5.0)
}

val mobilRtAcc: LongitudinalModel = {  distance, relativeSpeed, speed, maximumSpeed ->
    reactionTimeAdaptiveCruiseControl(speed, maximumSpeed, speed + relativeSpeed, distance, tau = 0.5)
}

val mobilMpcAcc: LongitudinalModel = {  distance, relativeSpeed, speed, maximumSpeed ->
    mpcCruiseControl(speed, maximumSpeed, relativeSpeed, distance)
}
// endregion


/**
 * Contains all the data required by the MOBIL model to compute whether or not a lane change can be
 * performed
 */
data class MobilState(
    /** The current speed of the current vehicle */
    val currentSpeed: Double,
    /** The maximum speed of the current vehicle */
    val maximumSpeed: Double,
    /** The distance between the new follower and the considered vehicle (> 0) */
    val newFollowerDistance: Double,
    /** The relative speed between the new follower and the considered vehicle (followerSpeed - currentSpeed) */
    val newFollowerRelativeSpeed: Double,
    /** The distance between the new leader and the considered vehicle (> 0) */
    val newLeaderDistance: Double,
    /** The relative speed between the new leader and the considered vehicle (currentSpeed - leaderSpeed) */
    val newLeaderRelativeSpeed: Double,
    /** The distance between the current leader and the considered vehicle (> 0) */
    val currentLeaderDistance: Double,
    /** The relative speed between the current leader and the considered vehicle (currentSpeed - leaderSpeed) */
    val currentLeaderRelativeSpeed: Double,
    /** The politeness of the driver (according to MOBIL) */
    val politeness: Double = 0.25) {

    /**
     * Determines if a lane-change is safe according to MOBIL (safety criterion): if the computed deceleration is above
     * the deceleration threshold, it's OK.
     * @param carFollowingModel a curryied car-following model giving the acceleration as a function of the inter-
     *          vehicular distance and the relative speed
     * @param decelerationThreshold the maximal accepted deceleration for the new follower (usually a value <= 0)
     */
    fun isLaneChangeSafe(carFollowingModel: LongitudinalModel, decelerationThreshold: Double = 0.0): Boolean {
        val newFollowerAcceleration = carFollowingModel(newFollowerDistance, newFollowerRelativeSpeed, currentSpeed + newFollowerRelativeSpeed, maximumSpeed)
        return newFollowerAcceleration > decelerationThreshold
    }

    /**
     * Determines if a lane-change is profitable according to MOBIL (incentive criterion): if the expected gain in terms
     * of acceleration is above a defined threshold
     * @param carFollowingModel a curryied car-following model giving the acceleration as a function of the inter-
     *          vehicular distance and the relative speed
     * @param accelerationThreshold the minimum acceleration difference expected to consider the lane-change as
     *          profitable
     */
    fun isLaneChangeProfitable(carFollowingModel: LongitudinalModel, accelerationThreshold: Double = 0.0): Boolean {
        val currentLaneAcceleration = carFollowingModel(currentLeaderDistance, currentLeaderRelativeSpeed, currentSpeed, maximumSpeed)
        val targetLaneAcceleration = carFollowingModel(newLeaderDistance, newLeaderRelativeSpeed, currentSpeed, maximumSpeed)
        val imposedFollowerAcceleration = carFollowingModel(newFollowerDistance, newFollowerRelativeSpeed, currentSpeed + newFollowerRelativeSpeed, maximumSpeed)
        val currentFollowerAcceleration = carFollowingModel(newFollowerDistance + newLeaderDistance, newLeaderRelativeSpeed - newFollowerRelativeSpeed, currentSpeed + newFollowerRelativeSpeed, maximumSpeed)
        return targetLaneAcceleration - currentLaneAcceleration > accelerationThreshold
    }

    /**
     * Determines whether or not a lane-change should be performed according to MOBIL
     * @param carFollowingModel a curryied car-following model giving the acceleration as a function of the inter-
     *          vehicular distance and the relative speed
     * @param accelerationThreshold the minimum acceleration difference expected to consider the lane-change as
     *          profitable (default value according to https://traffic-simulation.de/info/info_MOBIL.html)
     * @param decelerationThreshold the maximal accepted deceleration for the new follower (usually a value <= 0)
     */
    fun shouldLaneChangeBePerformed(
        carFollowingModel: LongitudinalModel,
        decelerationThreshold: Double = 0.0,
        accelerationThreshold: Double = 0.2): Boolean {
        // TODO We should compare the expected acceleration gain to the imposed acceleration loss of the new follower (https://traffic-simulation.de/info/info_MOBIL.html)
        return isLaneChangeSafe(carFollowingModel, decelerationThreshold) && isLaneChangeProfitable(carFollowingModel, accelerationThreshold)
    }

}
