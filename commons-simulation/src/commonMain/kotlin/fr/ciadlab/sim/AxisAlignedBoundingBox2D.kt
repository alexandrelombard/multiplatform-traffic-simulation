package fr.ciadlab.sim

import fr.ciadlab.sim.math.geometry.Vector2D
import kotlin.math.max
import kotlin.math.min

/**
 * A 2D axis-aligned bounding box
 * @author Alexandre Lombard
 */
data class AxisAlignedBoundingBox2D(
    val firstPoint: Vector2D,
    val secondPoint: Vector2D
) : BoundingBox2D {
    private val minX: Double by lazy { min(firstPoint.x, secondPoint.x) }
    private val minY: Double by lazy { min(firstPoint.y, secondPoint.y) }
    private val maxX: Double by lazy { max(firstPoint.x, secondPoint.x) }
    private val maxY: Double by lazy { max(firstPoint.y, secondPoint.y) }

    private val xExtent: Double by lazy { (maxX - minX) / 2.0 }
    private val yExtent: Double by lazy { (maxY - minY) / 2.0 }

    /**
     * The center of the axis-aligned bounding-box
     */
    val center: Vector2D by lazy { (firstPoint + secondPoint) * 0.5 }

    /**
     * Builds a square axis-aligned bounding-box
     * @param center the center of the box
     * @param extent half the size of the box
     */
    constructor(center: Vector2D, extent: Double) :
            this(center, extent, extent)

    constructor(center: Vector2D, xExtent: Double, yExtent: Double) :
            this(
                Vector2D(center.x - xExtent, center.y - yExtent),
                Vector2D(center.x + xExtent, center.y + yExtent))

    override fun contains(point: Vector2D) =
        point.x in minX..maxX && point.y in minY..maxY

    fun divide(): Array<AxisAlignedBoundingBox2D> {
        return arrayOf(
            // North east
            AxisAlignedBoundingBox2D(
                Vector2D(center.x + xExtent / 2.0, center.y - yExtent / 2.0), xExtent / 2.0, yExtent / 2.0),
            // North west
            AxisAlignedBoundingBox2D(
                Vector2D(center.x - xExtent / 2.0, center.y - yExtent / 2.0), xExtent / 2.0, yExtent / 2.0),
            // South west
            AxisAlignedBoundingBox2D(
                Vector2D(center.x - xExtent / 2.0, center.y + yExtent / 2.0), xExtent / 2.0, yExtent / 2.0),
            // South east
            AxisAlignedBoundingBox2D(
                Vector2D(center.x + xExtent / 2.0, center.y + yExtent / 2.0), xExtent / 2.0, yExtent / 2.0)
        )
    }

}
