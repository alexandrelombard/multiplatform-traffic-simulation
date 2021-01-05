package fr.ciadlab.sim.math.geometry

import fr.ciadlab.sim.math.algebra.Vector2D
import kotlin.math.max

/**
 * Class representing a triangle composed of 3 points
 */
class Triangle2D(
    val a: Vector2D,
    val b: Vector2D,
    val c: Vector2D
) {
    val hypotenuse: Double by lazy { maxOf((b - a).norm, (c - a).norm, (c - b).norm) }

    /**
     * Checks if a point is inside the triangle or not
     * @param p the point to test
     * @return <code>true</code> if the point is inside the triangle
     */
    fun isInside(p: Vector2D): Boolean {
        // https://stackoverflow.com/questions/2049582/how-to-determine-if-a-point-is-in-a-2d-triangle
        val d1 = sign(p, a, b)
        val d2 = sign(p, b, c)
        val d3 = sign(p, c, a)

        val hasNeg = (d1 < 0) || (d2 < 0) || (d3 < 0)
        val hasPos = (d1 > 0) || (d2 > 0) || (d3 > 0)

        return !(hasNeg && hasPos)
    }

    private fun sign(a: Vector2D, b: Vector2D, c: Vector2D): Double =
        (a.x - c.x) * (b.y - c.y) - (b.x - c.x) * (a.y - c.y)
}
