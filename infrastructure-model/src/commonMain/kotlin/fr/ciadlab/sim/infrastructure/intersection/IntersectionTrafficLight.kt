package fr.ciadlab.sim.infrastructure.intersection

import fr.ciadlab.sim.infrastructure.LaneConnector

data class IntersectionTrafficLight(val laneConnectors: List<LaneConnector>)
data class IntersectionTrafficLights(
    val trafficLights: List<IntersectionTrafficLight>, val policy: TrafficLightPolicy)
