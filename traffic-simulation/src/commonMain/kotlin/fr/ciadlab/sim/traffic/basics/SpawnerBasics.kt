package fr.ciadlab.sim.traffic.basics

import fr.ciadlab.sim.car.behavior.routing.OriginDestinationRouter
import fr.ciadlab.sim.car.perception.mapmatching.MapMatchingProvider
import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.math.algebra.toVector3D
import fr.ciadlab.sim.traffic.TrafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle

/**
 * Simple functions that can be used as default behaviors for spawners.
 * @author Alexandre Lombard
 */

/**
 *
 */
fun TrafficSimulation<Vehicle>.basicOnSpawn(vehicle: Vehicle, routes: MutableMap<Vehicle, List<Pair<Road, Boolean>>?>) {
    // Compute a route from the current position to a random exit area
    val router = OriginDestinationRouter(roadNetwork, MapMatchingProvider(roadNetwork))

    // We try to find a destination among all the exit areas, if a link cannot be made, we retry with another
    // random destination
    val availableDestinations = mutableListOf(*exitAreas.toTypedArray())

    lateinit var destination: Vector2D  // If route is not null, destination won't be null
    var route: List<Road>? = null

    while (route == null && availableDestinations.isNotEmpty()) {
        val randomDestination = availableDestinations.random()
        destination = randomDestination.position
        route = router.findRoute(vehicle.position, destination)
        availableDestinations.remove(randomDestination)
    }

    if(route != null) {
        // Define the directions of the roads of the route
        val directions = route.mapIndexed { index, road ->
            if(index < route.size - 1) {
                // If we are before the last, we are forward if the next road is at the end of the current
                roadNetwork.isAtEnd(road, route[index + 1])
            } else {
                // If we are the last, we are forward if the destination is closer to the end of the road
                destination.toVector3D().distance(road.end()) < destination.toVector3D().distance(road.begin())
            }
        }

        // Store it
        routes[vehicle] = route.mapIndexed { index, road -> Pair(road, directions[index]) }
    }
}
