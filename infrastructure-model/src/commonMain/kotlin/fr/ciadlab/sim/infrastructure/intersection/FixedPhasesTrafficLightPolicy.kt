package fr.ciadlab.sim.infrastructure.intersection

import fr.ciadlab.sim.infrastructure.LaneConnector

data class FixedPhasesTrafficLightPolicy(
    val phases: Map<LaneConnector, List<TrafficLightFixedPhase>>
) {

    fun currentState(laneConnector: LaneConnector, currentTime: Double): TrafficLightState {
        val lanePhase = phases[laneConnector] ?: return TrafficLightState.UNKNOWN
        val totalDuration = lanePhase.sumByDouble { it.duration }

        val position = currentTime % totalDuration

        return findPhase(position, lanePhase).state
    }

    /**
     * Find a phase given time
     */
    private fun findPhase(time: Double, phases: List<TrafficLightFixedPhase>): TrafficLightFixedPhase {
        var i = 0
        var cumulativeTime = 0.0

        while(i < phases.size - 1 && cumulativeTime + phases[i].duration < time) {
            cumulativeTime += phases[i++].duration
        }

        return phases[i]
    }

}

data class TrafficLightFixedPhase(val duration: Double, val state: TrafficLightState)
