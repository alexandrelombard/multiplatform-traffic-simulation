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

    /**
     * Builds a square axis-aligned bounding-box
     * @param center the center of the box
     * @param extent half the size of the box
     */
    constructor(center: Vector2D, extent: Double) :
            this(
                Vector2D(center.x - extent, center.y - extent),
                Vector2D(center.x + extent, center.y + extent))

    override fun contains(point: Vector2D) =
        point.x in minX..maxX && point.y in minY..maxY
}
