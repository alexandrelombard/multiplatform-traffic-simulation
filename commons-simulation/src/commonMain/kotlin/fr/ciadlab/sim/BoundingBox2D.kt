package fr.ciadlab.sim

import fr.ciadlab.sim.math.geometry.Vector2D

/**
 * Interface common to the different 2D bounding boxes
 * @author Alexandre Lombard
 */
interface BoundingBox2D {
    /**
     * Checks if the present box contains the given point
     * @param point the point to control
     * @return <code>true</code> if the point is in the box, <code>false</code> otherwise
     */
    fun contains(point: Vector2D): Boolean
}
