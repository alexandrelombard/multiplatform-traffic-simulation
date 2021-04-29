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
    /** The relative velocity of the obstacle: x is the relative lateral velocity, y is the relative longitudinal velocity */
    val obstacleRelativeVelocity: Vector2D) {
    fun getAbsolutePosition(reference: AffineSpace2D): Vector2D {
        return reference.toDefault(obstacleRelativePosition)
    }
}
