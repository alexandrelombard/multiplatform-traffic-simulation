package fr.ciadlab.sim.car.behavior.longitudinal

import kotlin.math.max
import kotlin.math.min

// Source: https://fr.mathworks.com/help/mpc/ug/adaptive-cruise-control-using-model-predictive-controller.html
// Due to the simplicity of the model (v(t+dt) = a(t) * dt + v(t)), we don't use a QP solver

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
): Double {
    val xl = intervehicularDistance
    val xe = 0.0
    val vl = relativeVelocity + velocity
    val ve = velocity
    val dt = tau

    val effectiveMinA = minimumDeceleration
    val effectiveMaxA = min(maximumAcceleration, (targetVelocity - velocity) / tau)

    val optA = optimalA(xl, vl, xe, ve, timeGap, dt, dDefault)

    return min(max(optA, effectiveMinA), effectiveMaxA)
}

fun optimalA(xl: Double, vl: Double, xe: Double, ve: Double, tg: Double, dt: Double, dDef: Double) =
    (vl * dt + xl - ve * dt + xe - dDef - tg * ve) / (tg * dt)
