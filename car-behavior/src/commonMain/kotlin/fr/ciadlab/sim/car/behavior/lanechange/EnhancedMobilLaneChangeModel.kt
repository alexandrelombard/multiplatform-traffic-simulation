package fr.ciadlab.sim.car.behavior.lanechange

import fr.ciadlab.sim.car.behavior.longitudinal.LongitudinalModel

/**
 * Contains all the data required by the MOBIL model to compute whether or not a lane change can be
 * performed.
 * Compared to the classical MOBIL model, this one avoid lane-change when the follower (in the same lane) may
 * try to overpass.
 * Also the arrival of a new vehicle behind shouldn't change a previous lane-change decision (once the lane-change is
 * started, the driver is committed). The commitment is deduced from a positive acceleration.
 */
data class EnhancedMobilState(
    /** The current speed of the current vehicle */
    val currentSpeed: Double,
    /** The current acceleration */
    val currentAcceleration: Double,
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
    /** The distance between the follower and the considered vehicle (> 0) */
    val currentFollowerDistance: Double,
    /** The relative speed between the follower and the considered vehicle (followerSpeed - currentSpeed) */
    val currentFollowerRelativeSpeed: Double,
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
        val currentFollowerAcceleration = carFollowingModel(currentFollowerDistance, currentFollowerRelativeSpeed, currentSpeed + currentFollowerRelativeSpeed, maximumSpeed)
        val newFollowerAcceleration = carFollowingModel(newFollowerDistance, newFollowerRelativeSpeed, currentSpeed + newFollowerRelativeSpeed, maximumSpeed)
        return currentAcceleration > 0.0 || (currentFollowerDistance > 20.0 && currentFollowerAcceleration > 0.0 && newFollowerAcceleration > decelerationThreshold)
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
