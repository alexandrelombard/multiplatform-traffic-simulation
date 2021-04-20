package fr.ciadlab.sim.car.behavior.lanechange

import fr.ciadlab.sim.car.perception.obstacles.ObstacleData
import kotlin.math.abs

/**
 * Contains all the data required by the MOBIL model to compute whether or not a lane change can be
 * performed
 */
data class MobilState(
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
    val currentLeaderRelativeSpeed: Double) {

    /**
     * Determines if a lane-change is safe according to MOBIL: if the computed deceleration is above the deceleration
     * threshold, it's OK.
     * @param carFollowingModel a curryied car-following model giving the acceleration as a function of the inter-
     *          vehicular distance and the relative speed
     * @param decelerationThreshold the maximal accepted deceleration for the new follower (usually a value <= 0)
     */
    fun isLaneChangeSafe(carFollowingModel: (Double, Double)->Double, decelerationThreshold: Double = 0.0): Boolean {
        val newFollowerAcceleration = carFollowingModel(newFollowerDistance, newFollowerRelativeSpeed)
        return newFollowerAcceleration > decelerationThreshold
    }

    /**
     * Determines if a lane-change is profitable according to MOBIL: if the expected gain in terms of acceleration
     * is above a defined threshold
     * @param carFollowingModel a curryied car-following model giving the acceleration as a function of the inter-
     *          vehicular distance and the relative speed
     * @param accelerationThreshold the minimum acceleration difference expected to consider the lane-change as
     *          profitable
     */
    fun isLaneChangeProfitable(carFollowingModel: (Double, Double)->Double, accelerationThreshold: Double = 0.0): Boolean {
        val currentLaneAcceleration = carFollowingModel(currentLeaderDistance, currentLeaderRelativeSpeed)
        val targetLaneAcceleration = carFollowingModel(newLeaderDistance, newLeaderRelativeSpeed)
        return targetLaneAcceleration - currentLaneAcceleration > accelerationThreshold
    }

}

/**
 * MOBIL-like lane change model
 * @param currentSpeed
 * @param freeSpeed
 * @param currentLeader the current leader
 * @param targetLanePerceivedVehicles the perceived vehicles in the target lane
 * @param curryiedCarFollowingModel a car-following model related to the self vehicle
 * @return either <code>true</code> or <code>false</code> if the lane change should be performed or not
 */
fun mobilLaneChange(
    currentSpeed: Double,
    freeSpeed: Double,
    currentLeader: ObstacleData?,
    targetLanePerceivedVehicles: List<ObstacleData>,
    curryiedCarFollowingModel: (ObstacleData)->Double): Boolean {

    if(currentSpeed < freeSpeed) {
        // If the current speed is below the free speed, and if we can accelerate in the new lane without making someone
        // brake, we do it
        val currentLeaderRelatedAcceleration =
            if(currentLeader == null) Double.POSITIVE_INFINITY else curryiedCarFollowingModel(currentLeader)

        val newLeader = targetLanePerceivedVehicles
            .filter { it.obstacleRelativePosition.x >= 0 }.minByOrNull { it.obstacleRelativePosition.x }

        val newLeaderRelatedAcceleration =
            if(newLeader == null) Double.POSITIVE_INFINITY else curryiedCarFollowingModel(newLeader)

        if(newLeaderRelatedAcceleration > currentLeaderRelatedAcceleration) {
            // We can increase the speed in the new lane, let's check that we won't make the new follower brake
            // by looking at the headway time (note: this could be changed to something else)
            val newFollower = targetLanePerceivedVehicles
                .filter { it.obstacleRelativePosition.x < 0 }.maxByOrNull { it.obstacleRelativePosition.x }
            if(newFollower != null) {
                val newFollowerDistance = abs(newFollower.obstacleRelativePosition.x)
                val newFollowerVelocity = newFollower.obstacleRelativeVelocity.x + currentSpeed
                val headway = newFollowerDistance / (newFollowerVelocity)

                if(headway > 2.0) {
                    return true
                }
            }
        }
    }

    return false

}
