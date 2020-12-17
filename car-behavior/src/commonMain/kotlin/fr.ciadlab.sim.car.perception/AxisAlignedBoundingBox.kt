package fr.ciadlab.sim.car.perception

data class AxisAlignedBoundingBox(val minX: Double, val minY: Double, val maxX: Double, val maxY: Double) {
    val width: Double by lazy { maxX - minX }
    val height: Double by lazy { maxY - minY }

    /**
     * Checks if the point (x, y) is contained in the given AABB
     * @param x
     * @param y
     * @return <code>true</code> if the point is contained in the AABB, <code>false</code> otherwise
     */
    fun contains(x: Double, y: Double) = x >= minX && x <= maxX && y >= minY && y <= maxY
}