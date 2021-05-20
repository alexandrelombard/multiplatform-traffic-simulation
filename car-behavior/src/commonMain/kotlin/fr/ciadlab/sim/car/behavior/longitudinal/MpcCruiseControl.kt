package fr.ciadlab.sim.car.behavior.longitudinal

import kotlin.math.max
import kotlin.math.min

// Source: https://fr.mathworks.com/help/mpc/ug/adaptive-cruise-control-using-model-predictive-controller.html

fun mpcCruiseControl(
    velocity: Double,                       // v_ego
    targetVelocity: Double,                 // v_set
    relativeVelocity: Double,               // v_lead - v_ego
    leaderMaximumDeceleration: Double,      // b_l
    intervehicularDistance: Double,         // d_rel
    minimumDeceleration: Double,            // a_min (ego)
    maximumAcceleration: Double,            // a_max (both)
    timeGap: Double,                        // t_gap
    dDefault: Double,                       // Ddefault => minimum safe distance
    tau: Double,                            // Ts
    steps: Int
): Double {
    var dSafe =dDefault + timeGap * velocity
    val velocityModel = { acc: Double, dt: Double, v: Double -> v + acc * dt  }

    var xl = intervehicularDistance
    var xe = 0.0
    var vl = relativeVelocity + velocity
    var ve = velocity
    var a = max(minimumDeceleration, min(maximumAcceleration, (targetVelocity - velocity) / tau))
    val dt = tau
    var dRel = intervehicularDistance
    for (i in 0 until steps) {
        // Run the step
        xl += vl * dt
        xe += ve * dt
        ve += a * dt

        dRel = xl - xe
        dSafe = dDefault + timeGap * ve

        a = max(minimumDeceleration, min(maximumAcceleration, (targetVelocity - velocity) / tau))
    }

    TODO("Not yet implemented")
}
