package fr.ciadlab.sim.math.algebra

/**
 * Matrix 3x3
 * Intended for internal use.
 * @author Alexandre Lombard
 */
data class Matrix3X3(
    val a00: Double, val a01: Double, val a02: Double,
    val a10: Double, val a11: Double, val a12: Double,
    val a20: Double, val a21: Double, val a22: Double) {
    constructor(v1: Vector3D, v2: Vector3D, v3: Vector3D) : this(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z)

    operator fun times(v: Vector3D): Vector3D {
        return Vector3D(
            a00 * v.x + a10 * v.y + a20 * v.z,
            a01 * v.x + a11 * v.y + a21 * v.z,
            a02 * v.x + a12 * v.y + a22 * v.z)
    }
}
