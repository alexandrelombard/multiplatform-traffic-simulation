package fr.ciadlab.sim.car.behavior.lanechange

import fr.ciadlab.sim.car.perception.obstacles.ObstacleData
import kotlin.math.abs

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
    currentLeader: ObstacleData,
    targetLanePerceivedVehicles: List<ObstacleData>,
    curryiedCarFollowingModel: (ObstacleData)->Double): Boolean {

    if(currentSpeed < freeSpeed) {
        // If the current speed is below the free speed, and if we can accelerate in the new lane without making someone
        // brake, we do it
        val currentLeaderRelatedAcceleration = curryiedCarFollowingModel(currentLeader)

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
