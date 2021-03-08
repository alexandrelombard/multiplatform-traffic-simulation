package fr.ciadlab.sim.infrastructure.intersection

import fr.ciadlab.sim.infrastructure.Intersection
import fr.ciadlab.sim.infrastructure.LaneConnector

/**
 * Describes the traffic light organization over a given intersection by binding a traffic light state to a
 * lane connector
 * @author Alexandre Lombard
 */
class TrafficLightIntersectionManager(
    val intersection: Intersection,
    val trafficLights: Map<LaneConnector, TrafficLightState>) {

}
