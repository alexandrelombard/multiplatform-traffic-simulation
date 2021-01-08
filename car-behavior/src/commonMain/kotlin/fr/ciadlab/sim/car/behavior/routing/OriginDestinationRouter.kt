package fr.ciadlab.sim.car.behavior.routing

import fr.ciadlab.sim.ai.DijkstraAlgorithm
import fr.ciadlab.sim.car.perception.mapmatching.MapMatchingProvider
import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.infrastructure.RoadNetwork
import fr.ciadlab.sim.math.algebra.Vector2D

/**
 * Computes the route from an origin to a destination for the given road network using the given map-matching provider
 * @author Alexandre Lombard
 */
class OriginDestinationRouter(val roadNetwork: RoadNetwork, val mapMatchingProvider: MapMatchingProvider){

    /**
     * Finds the route between two points
     * @param origin the origin
     * @param destination the destination
     * @return the list of roads or <code>null</code> if unable to join the destination from the origin
     */
    fun findRoute(origin: Vector2D, destination: Vector2D): List<Road>? {
        val originMapMatching = mapMatchingProvider.mapMatching(origin)
        val destinationMapMatching = mapMatchingProvider.mapMatching(destination)

        val path = DijkstraAlgorithm.findShortestPath(
            originMapMatching.road,
            destinationMapMatching.road,
            availableNodes = { roadNetwork.getConnectedRoads(it) },
            distance = { roadA, _ -> roadA.length } // FIXME Bad metric for distance
        )

        return path
    }

}
