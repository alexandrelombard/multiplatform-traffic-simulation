package fr.ciadlab.sim.car.perception.signals

import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.infrastructure.v2x.IntersectionRoadSideUnit
import fr.ciadlab.sim.utils.UUID

class IntersectionRsuPerceptionProvider {
    /**
     * Performs the RSU detection.
     */
    fun performIntersectionRsuDetection(
        route: List<Road>,
        roadSideUnits: Collection<IntersectionRoadSideUnit>): List<UUID> {
        // We check if there is at least two connected roads in the list
        if(route.size < 2)
            return emptyList()

        // We then check if there are RSU managing the intersections
        return roadSideUnits
            .filter {
                // We filter the roadside units, we keep only the ones who are associated to intersections along the
                // route passed as parameter
                it.intersection.laneConnectors.any {
                    var res = false
                    for (i in 0 until route.size - 1) {
                        if (it.sourceRoad == route[i] && it.destinationRoad == route[i + 1]) {
                            res = true
                            break
                        }
                    }
                    res
                }
            }
            .map { it.communicationUnit.identifier }
    }
}
