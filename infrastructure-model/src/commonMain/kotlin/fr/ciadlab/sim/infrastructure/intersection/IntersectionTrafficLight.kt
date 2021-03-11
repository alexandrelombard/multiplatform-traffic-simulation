package fr.ciadlab.sim.infrastructure.intersection

import fr.ciadlab.sim.entity.Identifiable
import fr.ciadlab.sim.entity.Updatable
import fr.ciadlab.sim.infrastructure.LaneConnector
import fr.ciadlab.sim.utils.UUID

data class IntersectionTrafficLight(
    val laneConnectors: List<LaneConnector>,
    val state: TrafficLightState,
    override val onUpdate: MutableList<(IntersectionTrafficLight)->Unit> = arrayListOf(),
    override val identifier: UUID = UUID.randomUUID()) : Identifiable, Updatable<IntersectionTrafficLight> {
    /**
     * Change the state of this traffic light
     */
    fun changeState(newState: TrafficLightState): IntersectionTrafficLight {
        if(state != newState) {
            val updatedTrafficLight = this.copy(state = newState)

            onUpdate.forEach { it.invoke(updatedTrafficLight) }

            return updatedTrafficLight
        }

        return this
    }

    override fun equals(other: Any?): Boolean {
        if(other == null)
            return false

        if(other !is IntersectionTrafficLight)
            return false

        return identifier == other.identifier
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }
}

data class IntersectionTrafficLights(
    val trafficLights: MutableSet<IntersectionTrafficLight>, val policy: TrafficLightPolicy)
