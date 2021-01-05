package fr.ciadlab.sim.math.algebra

class AffineSpace2D(
    val origin: Vector2D,
    val xAxis: Vector2D,
    val yAxis: Vector2D) {

    /**
     * Express some coordinates from the default (1,0), (0,1) basis centered on (0,0) in this basis
     * @param v the coordinates to convert
     * @return the coordinates of v in the local space
     */
    fun fromDefault(v: Vector2D): Vector2D {
        val translated = v - origin
        val rotated = Matrix2X2(xAxis, yAxis) * translated
        return rotated
    }

}
