package fr.ciadlab.sim.infrastructure.intersection

import fr.ciadlab.sim.infrastructure.LaneConnector

data class FixedPhasesTrafficLightPolicy(
    val phases: Map<LaneConnector, List<TrafficLightFixedPhase>>
) {

    fun currentState(laneConnector: LaneConnector, currentTime: Double): TrafficLightState {
        // TODO
        return TrafficLightState.GREEN
    }

}

data class TrafficLightFixedPhase(val startTime: Double, val state: TrafficLightState) :
        Comparable<TrafficLightFixedPhase> {
    override fun compareTo(other: TrafficLightFixedPhase): Int {
        return startTime.compareTo(other.startTime)
    }
}
