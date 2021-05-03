package fr.ciadlab.sim.car.perception.obstacles

import fr.ciadlab.sim.math.algebra.AffineSpace2D
import fr.ciadlab.sim.math.algebra.Vector2D

/**
 * Obstacle data
 * @author Alexandre Lombard
 */
data class ObstacleData(
    /** The relative position of the obstacle: x is the lateral displacement, y is the longitudinal displacement */
    val obstacleRelativePosition: Vector2D,
    /** The velocity of the obstacle in the local space: x is the local lateral velocity, y is the local longitudinal velocity */
    val obstacleRelativeVelocity: Vector2D) {
    fun getAbsolutePosition(reference: AffineSpace2D): Vector2D {
        return reference.toDefault(obstacleRelativePosition)
    }
}
