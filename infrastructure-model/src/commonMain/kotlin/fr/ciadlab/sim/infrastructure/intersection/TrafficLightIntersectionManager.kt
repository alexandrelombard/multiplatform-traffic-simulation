package fr.ciadlab.sim.infrastructure.intersection

import fr.ciadlab.sim.infrastructure.Intersection
import fr.ciadlab.sim.infrastructure.LaneConnector

/**
 * Describes the traffic light organization over a given intersection by binding traffic light states to
 * lane connectors
 * @author Alexandre Lombard
 */
data class TrafficLightIntersectionManager(
    val intersection: Intersection,
    val trafficLights: Map<LaneConnector, TrafficLightState>) {

    /**
     * Change the state of the given connector
     * @param connector the lane connector
     * @param newState the new state
     * @return the updated traffic manager
     */
    fun changeState(connector: LaneConnector, newState: TrafficLightState): TrafficLightIntersectionManager {
        return this.copy(trafficLights = this.trafficLights + (Pair(connector, newState)))
    }

    /**
     * Change the state of the given connectors
     * @param connectors the lane connectors
     * @param newState the new state
     * @return the updated traffic manager
     */
    fun changeState(connectors: List<LaneConnector>, newState: TrafficLightState): TrafficLightIntersectionManager {
        return this.copy(trafficLights = this.trafficLights + connectors.map { Pair(it, newState) })
    }
}