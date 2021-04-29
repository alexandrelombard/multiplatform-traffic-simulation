package fr.ciadlab.sim.math.algebra

/**
 * Represents a 2D vector space
 * @author Alexandre Lombard
 */
class VectorSpace2D(
    val xAxis: Vector2D,
    val yAxis: Vector2D) {

    private val matrix by lazy { Matrix2X2(xAxis, yAxis) }

    /**
     * Express some coordinates from the default (1,0), (0,1) basis in this basis
     * @param v the coordinates to convert
     * @return the coordinates of v in the local space
     */
    fun fromDefault(v: Vector2D): Vector2D {
        return matrix.invert() * v
    }

    /**
     * Express some coordinates from this basis to the default (1,0), (0,1) basis
     * @param v the coordinates to convert
     * @return the coordinates of v in the default space
     */
    fun toDefault(v: Vector2D): Vector2D {
        return matrix * v
    }
}
