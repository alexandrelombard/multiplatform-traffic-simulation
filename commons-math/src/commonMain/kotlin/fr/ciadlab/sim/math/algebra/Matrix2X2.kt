package fr.ciadlab.sim.math.algebra

/**
 * Matrix 2x2
 * Intended for internal use.
 * @author Alexandre Lombard
 */
data class Matrix2X2(
    val a00: Double, val a01: Double, val a10: Double, val a11: Double) {
    constructor(v1: Vector2D, v2: Vector2D) : this(v1.x, v1.y, v2.x, v2.y)

    /** The determinant of the matrix */
    val determinant: Double by lazy { a00 * a11 - a01 * a10 }

    fun invert(): Matrix2X2 {
        return (1.0 / this.determinant) * Matrix2X2(a11, -a01, -a10, a00)
    }

    operator fun times(v: Vector2D): Vector2D {
        return Vector2D(a00 * v.x + a10 * v.y, a01 * v.x + a11 * v.y)
    }

    operator fun times(m: Matrix2X2): Matrix2X2 {
        return Matrix2X2(
            a00 * m.a00 + a10 * m.a01,
            a01 * m.a00 + a11 * m.a01,
            a00 * m.a10 + a10 * m.a11,
            a01 * m.a10 + a11 * m.a11)
    }

    operator fun times(lambda: Double): Matrix2X2 {
        return Matrix2X2(a00 * lambda, a01 * lambda, a10 * lambda, a11 * lambda)
    }
}

operator fun Double.times(m: Matrix2X2) = m * this
