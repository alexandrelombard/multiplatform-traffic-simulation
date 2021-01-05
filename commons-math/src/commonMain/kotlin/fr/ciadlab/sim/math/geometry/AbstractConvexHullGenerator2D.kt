package fr.ciadlab.sim.math.geometry

import fr.ciadlab.sim.math.algebra.Vector2D

/**
 * Abstract base class for convex hull generators in the two-dimensional euclidean space.
 * Inspired by commons-math:3.6.1
 * @author Alexandre Lombard
 */
abstract class AbstractConvexHullGenerator2D(
    /**
     * Indicates if collinear points on the hull shall be present in the output.
     * If `false`, only the extreme points are added to the hull.
     */
    val includeCollinearPoints: Boolean,
    /** Tolerance below which points are considered identical.  */
    val tolerance: Double = DEFAULT_TOLERANCE
) {
    /**
     * Find the convex hull vertices from the set of input points.
     * @param points the set of input points
     * @return the convex hull vertices in CCW winding
     */
    abstract fun findHullVertices(points: Collection<Vector2D>): Collection<Vector2D>

    companion object {
        /** Default value for tolerance.  */
        private const val DEFAULT_TOLERANCE = 1e-10
    }
}
