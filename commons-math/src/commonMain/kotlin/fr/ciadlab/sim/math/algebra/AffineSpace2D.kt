package fr.ciadlab.sim.math.algebra

class AffineSpace2D(
    /** The origin (in the default space coordinates) */
    val origin: Vector2D,
    /** The x-axis (in the default space coordinates) */
    val xAxis: Vector2D,
    /** The y-axis (in the default space coordinates) */
    val yAxis: Vector2D = Vector2D(-xAxis.y, xAxis.x)) {

    private val matrix by lazy { Matrix2X2(xAxis, yAxis) }

    /**
     * Express some coordinates from the default (1,0), (0,1) basis centered on (0,0) in this basis
     * @param v the coordinates to convert
     * @return the coordinates of v in the local space
     */
    fun fromDefault(v: Vector2D): Vector2D {
        return matrix.invert() * ( v - origin)
    }

    /**
     * Express some coordinates from this space to the default space {(1, 0), (0, 1)}
     * @param v the coordinates to convert
     * @return the coordinates of v in the default space
     */
    fun toDefault(v: Vector2D): Vector2D {
        return matrix * v + origin
    }

}
