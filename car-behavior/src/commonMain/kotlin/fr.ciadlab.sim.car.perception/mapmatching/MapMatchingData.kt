package fr.ciadlab.sim.car.perception.mapmatching

import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.math.algebra.Vector2D

/**
 * Map-matching data
 * @author Alexandre Lombard
 */
data class MapMatchingData(
    val sourcePosition: Vector2D,
    val roadPosition: Vector2D,
    val road: Road) {
    val distance by lazy { roadPosition.distance(sourcePosition) }
}
