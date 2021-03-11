package fr.ciadlab.sim.infrastructure.intersection

import fr.ciadlab.sim.entity.Updatable
import fr.ciadlab.sim.infrastructure.LaneConnector

data class IntersectionTrafficLight(
    val laneConnectors: List<LaneConnector>,
    val state: TrafficLightState) : Updatable<IntersectionTrafficLight>() {
    /**
     * Change the state of this traffic light
     */
    fun changeState(newState: TrafficLightState): IntersectionTrafficLight {
        if(state != newState) {
            val updatedTrafficLight = this.copy(state = newState)

            fireUpdate(updatedTrafficLight)

            return updatedTrafficLight
        }

        return this
    }
}

data class IntersectionTrafficLights(
    val trafficLights: MutableSet<IntersectionTrafficLight>, val policy: TrafficLightPolicy) {
    // FIXME Delete later
//    init {
//        // Monitor the update of the traffic lights in the list by listening to the change state event
//        trafficLights.forEach {
//            it.onUpdate += {
//                // Replace the traffic light in the set
//                trafficLights.remove(it)
//                trafficLights.add(it)
//            }
//        }
//    }
}
