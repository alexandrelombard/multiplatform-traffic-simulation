package fr.ciadlab.sim.car.behavior.longitudinal

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
    val dSafe = { vEgo: Double -> dDefault + timeGap * vEgo}
    val velocityModel = { acc: Double, dt: Double, v: Double -> v + acc * dt  }

    val maxAcceleration = min(maximumAcceleration, maxVelocityConstraint(velocity, targetVelocity, tau))


    TODO("Not yet implemented")
}

fun maxVelocityConstraint(velocity: Double, targetVelocity: Double, deltaTime: Double) =
    (targetVelocity - velocity) / deltaTime

fun mpc() {
    var vEgo = 0.0
    var xEgo = 0.0
    var vLead = 0.0
    var xLead = 0.0

    var acc = 0.0
    var dt = 0.1

    vEgo += acc * dt
    xEgo += vEgo * dt
    xLead += vLead * dt

    var dRel = xLead - xEgo

    TODO("Not yet implement")
}
