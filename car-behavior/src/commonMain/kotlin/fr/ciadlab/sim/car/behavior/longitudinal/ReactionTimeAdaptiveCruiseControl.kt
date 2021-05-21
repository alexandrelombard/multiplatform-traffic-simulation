package fr.ciadlab.sim.car.behavior.longitudinal

import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Apply RT-ACC to compute the desired acceleration of the vehicle
 * @param vehicleSpeed the current speed of the vehicle
 * @param targetSpeed the target speed
 * @param leaderSpeed the current speed of the leader vehicle
 * @param intervehicularDistance the current distance between the two vehicles
 * @param minimumDeceleration the minimum value the car accepts to apply for deceleration (a negative value)
 * @param minimumSpacing the minimum spacing between the two cars
 * @param worstCaseSelfDeceleration the worst-case deceleration (which can be applied if it's not safe to apply the
 *                                  minimum deceleration) - a negative number
 * @param worstCaseLeaderDeceleration the considered worst-case deceleration for the leader (negative number)
 * @param tau the considered worst-case reaction time (from the detection of the leader emergency braking to the
 *            application of the deceleration)
 */
fun reactionTimeAdaptiveCruiseControl(
    vehicleSpeed: Double,
    targetSpeed: Double,
    leaderSpeed: Double,
    intervehicularDistance: Double,
    minimumDeceleration: Double = -2.0,
    minimumSpacing: Double = 6.0,
    worstCaseSelfDeceleration: Double = -8.0,
    worstCaseLeaderDeceleration: Double = -8.0,
    tau: Double = 2.0
): Double {
    var underSquareRoot = (minimumDeceleration * worstCaseLeaderDeceleration * tau.pow(2.0) +
            4.0 * worstCaseLeaderDeceleration * vehicleSpeed * tau +
            4.0 * leaderSpeed.pow(2.0) - 8.0 * worstCaseLeaderDeceleration * (intervehicularDistance - minimumSpacing )) / (4.0 * minimumDeceleration * worstCaseLeaderDeceleration)

    if (underSquareRoot < 0) {
        // Try with bigger deceleration, level 2
        val optimalDeceleration =
            (8.0 * worstCaseLeaderDeceleration * intervehicularDistance - 4.0 * worstCaseLeaderDeceleration * leaderSpeed * tau - 4.0 * leaderSpeed.pow(2.0)) / (worstCaseLeaderDeceleration * tau.pow(2.0))

        underSquareRoot = (optimalDeceleration * worstCaseLeaderDeceleration * tau.pow(2.0) +
                4.0 * worstCaseLeaderDeceleration * vehicleSpeed * tau +
                4.0 * leaderSpeed.pow(2.0) - 8.0 * worstCaseLeaderDeceleration * (intervehicularDistance - minimumSpacing)) / (4.0 * optimalDeceleration * worstCaseLeaderDeceleration)

        if (underSquareRoot < 0 || optimalDeceleration < worstCaseSelfDeceleration) {
            // Emergency brake, level 3
            return worstCaseSelfDeceleration
        }

        val aS = sqrt(underSquareRoot)

        return (optimalDeceleration * tau - 2 * vehicleSpeed - 2.0 * optimalDeceleration * aS) / (2.0 * tau)
    }

    val aS = sqrt(underSquareRoot)

    val expectedAcceleration = (minimumDeceleration * tau - 2 * vehicleSpeed - 2.0 * minimumDeceleration * aS) / (2.0 * tau)

    return min(expectedAcceleration, (targetSpeed - vehicleSpeed) / tau)
}
