package fr.ciadlab.sim.math.geometry

import fr.ciadlab.sim.math.algebra.Vector3D
import fr.ciadlab.sim.math.algebra.times
import kotlin.math.pow

/**
 * Builds the polyline associated to the given Hermite spline definition
 * The points are list under the format (Control Point 0, Tangent 0), (Control Point 1, Tangent 1), etc.
 * @param points the points
 * @param steps the number of steps for each sub-spline
 */
fun hermiteSpline(vararg points: Vector3D, steps: Int = 10): List<Vector3D> {
    val output = arrayListOf<Vector3D>()

    for(i in 0..points.size - 4 step 2) {
        val p0 = points[i]
        val t0 = points[i + 1]
        val p1 = points[i + 2]
        val t1 = points[i + 3]
        output.addAll(hermiteSpline(p0, t0, p1, t1))
    }

    return output
}

/**
 * Builds the polyline associated to the given Hermite spline definition
 * @param p0 the first control point
 * @param t0 the tangent at the first control point
 * @param p1 the second control point
 * @param t1 the tangent at the second control point
 */
fun hermiteSpline(p0: Vector3D, t0: Vector3D, p1: Vector3D, t1: Vector3D, steps: Int = 10): List<Vector3D> {
    var points = arrayListOf<Vector3D>()

    val h00 = {t: Double -> 2.0 * t.pow(3) - 3 * t.pow(2) + 1}
    val h10 = {t: Double -> t.pow(3) - 2.0 * t.pow(2) + t}
    val h01 = {t: Double -> -2.0 * t.pow(3) + 3.0 * t.pow(2)}
    val h11 = {t: Double -> t.pow(3) - t.pow(2)}

    return (0..steps).map {
        val t = it / steps.toDouble()
        h00(t) * p0 + h10(t) * t0 + h01(t) * p1 + h11(t) * t1
    }.toList()
}
