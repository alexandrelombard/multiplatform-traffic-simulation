package fr.ciadlab.sim.car.perception.obstacles

import fr.ciadlab.sim.math.algebra.Vector2D

/**
 * Radar data
 * @author Alexandre Lombard
 */
data class RadarData(
    val obstacleRelativePosition: Vector2D,
    val obstacleRelativeVelocity: Vector2D)
