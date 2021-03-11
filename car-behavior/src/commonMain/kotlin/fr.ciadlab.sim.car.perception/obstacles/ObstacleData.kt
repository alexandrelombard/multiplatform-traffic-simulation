package fr.ciadlab.sim.car.perception.obstacles

import fr.ciadlab.sim.math.algebra.Vector2D

/**
 * Obstacle data
 * @author Alexandre Lombard
 */
data class ObstacleData(
    val obstacleRelativePosition: Vector2D,
    val obstacleRelativeVelocity: Vector2D)
