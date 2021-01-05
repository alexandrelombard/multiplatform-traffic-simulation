package fr.ciadlab.sim.math.geometry


import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.math.util.Precision
import kotlin.math.abs

class MonotoneChain : AbstractConvexHullGenerator2D {

    constructor() : this(false)
    constructor(includeCollinearPoints: Boolean) : super(includeCollinearPoints)

    override fun findHullVertices(points: Collection<Vector2D>): Collection<Vector2D> {
        val pointsSortedByXAxis = points.toMutableList()

        // sort the points in increasing order on the x-axis
        pointsSortedByXAxis.sortWith(Comparator { o1, o2 ->
            val tolerance: Double = tolerance
            // need to take the tolerance value into account, otherwise collinear points
            // will not be handled correctly when building the upper/lower hull
            val diff: Int = Precision.compareTo(o1.x, o2.x, tolerance)
            if (diff == 0) {
                Precision.compareTo(o1.y, o2.y, tolerance)
            } else {
                diff
            }
        })

        // build lower hull
        val lowerHull = arrayListOf<Vector2D>()
        for (p in pointsSortedByXAxis) {
            updateHull(p, lowerHull)
        }

        // build upper hull
        val upperHull = arrayListOf<Vector2D>()
        for (idx in pointsSortedByXAxis.indices.reversed()) {
            val p = pointsSortedByXAxis[idx]
            updateHull(p, upperHull)
        }

        // concatenate the lower and upper hulls
        // the last point of each list is omitted as it is repeated at the beginning of the other list
        // concatenate the lower and upper hulls
        // the last point of each list is omitted as it is repeated at the beginning of the other list
        val hullVertices: MutableList<Vector2D> =
            ArrayList(lowerHull.size + upperHull.size - 2)
        for (idx in 0 until lowerHull.size - 1) {
            hullVertices.add(lowerHull[idx])
        }
        for (idx in 0 until upperHull.size - 1) {
            hullVertices.add(upperHull[idx])
        }

        // special case: if the lower and upper hull may contain only 1 point if all are identical
        // special case: if the lower and upper hull may contain only 1 point if all are identical
        if (hullVertices.isEmpty() && !lowerHull.isEmpty()) {
            hullVertices.add(lowerHull[0])
        }

        return hullVertices
    }

    /**
     * Update the partial hull with the current point.
     *
     * @param point the current point
     * @param hull the partial hull
     */
    private fun updateHull(
        point: Vector2D,
        hull: MutableList<Vector2D>
    ) {
        val tolerance: Double = tolerance
        if (hull.size == 1) { // ensure that we do not add an identical point
            val p1 = hull[0]
            if (p1.distance(point) < tolerance) {
                return
            }
        }
        while (hull.size >= 2) {
            val size = hull.size
            val p1 = hull[size - 2]
            val p2 = hull[size - 1]
            val offset: Double = Line2D(p1, p2, tolerance).getOffset(point)
            if (abs(offset) < tolerance) { // the point is collinear to the line (p1, p2)
                val distanceToCurrent = p1.distance(point)
                if (distanceToCurrent < tolerance || p2.distance(point) < tolerance) { // the point is assumed to be identical to either p1 or p2
                    return
                }
                val distanceToLast = p1.distance(p2)
                if (includeCollinearPoints) {
                    val index = if (distanceToCurrent < distanceToLast) size - 1 else size
                    hull.add(index, point)
                } else {
                    if (distanceToCurrent > distanceToLast) {
                        hull.removeAt(size - 1)
                        hull.add(point)
                    }
                }
                return
            } else if (offset > 0) {
                hull.removeAt(size - 1)
            } else {
                break
            }
        }
        hull.add(point)
    }

}
