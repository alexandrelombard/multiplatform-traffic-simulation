package fr.ciadlab.sim.car.perception.signals

import fr.ciadlab.sim.infrastructure.Intersection
import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.infrastructure.intersection.IntersectionTrafficLight
import fr.ciadlab.sim.utils.UUID

class IntersectionRsuPerceptionProvider {
    /**
     * Performs the RSU detection.
     */
    fun performIntersectionRsuDetection(
        route: List<Road>,
        intersections: Collection<Intersection>): List<UUID> {
        // We check if there is at least two connected roads in the list
        if(route.size < 2)
            return emptyList()

        // We then check if there a traffic light managing the connection between route[0] and route[1]
        return intersections
            .filter {  }
            .filter {
            it.laneConnectors.any {
                // FIXME This is not optimal
                var res = false
                for(i in 0 until route.size - 1) {
                    if(it.sourceRoad == route[i] && it.destinationRoad == route[i + 1]) {
                        res = true
                        break
                    }
                }
                res
            }
        }
    }
}
