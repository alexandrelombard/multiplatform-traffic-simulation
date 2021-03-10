package fr.ciadlab.sim.infrastructure.intersection

import fr.ciadlab.sim.infrastructure.LaneConnector

fun interface TrafficLightPolicy {
    fun currentState(laneConnector: LaneConnector, currentTime: Double): TrafficLightState
}
