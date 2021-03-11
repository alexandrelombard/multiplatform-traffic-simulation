package fr.ciadlab.sim.car.perception.signals

import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.infrastructure.intersection.IntersectionTrafficLight

class TrafficLightPerceptionProvider {
    /**
     * Performs the traffic lights detection and returns the state of these lights associated to the appropriate
     * lane connector.
     */
    fun performTrafficLightDetection(
        route: List<Road>,
        trafficLights: Collection<IntersectionTrafficLight>): List<IntersectionTrafficLight> {
        // We consider only the first traffic light on the road, so we check if there is at least two connected
        // roads in the list
        if(route.size < 2)
            return emptyList()

        // We then check if there a traffic light managing the connection between route[0] and route[1]
        return trafficLights.filter {
            it.laneConnectors.any {
                it.sourceRoad == route[0] && it.destinationRoad == route[1]
            }
        }
    }
}
