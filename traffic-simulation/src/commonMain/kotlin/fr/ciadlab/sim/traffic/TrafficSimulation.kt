package fr.ciadlab.sim.traffic

import fr.ciadlab.sim.infrastructure.RoadNetwork
import fr.ciadlab.sim.vehicle.Vehicle

class TrafficSimulation(
    val spawners: MutableList<Spawner<*>> = arrayListOf(),
    val exitAreas: MutableList<ExitArea> = arrayListOf(),
    var roadNetwork: RoadNetwork = RoadNetwork(),
    val spawnedObjects: MutableSet<Any> = hashSetOf()
) {
    /**
     * Run a simulation step
     * @param deltaTime the elapsed time since the last step
     */
    fun step(deltaTime: Double) {
        // Calls the spawning strategies
        spawners.forEach { it.strategy?.invoke(deltaTime) }
    }
}

fun trafficSimulation(op: TrafficSimulation.() -> Unit): TrafficSimulation {
    val trafficSimulation = TrafficSimulation()
    op.invoke(trafficSimulation)

    // Add an event handler to register the spawned elements
    trafficSimulation.spawners.forEach {
//        it.onGeneration.add({ obj -> trafficSimulation.spawnedObjects.add(obj) })
        // TODO
    }

    return trafficSimulation
}

fun TrafficSimulation.roadNetwork(op: RoadNetwork.() -> Unit) {
    val roadNetwork = RoadNetwork()
    op.invoke(roadNetwork)

    this.roadNetwork = roadNetwork
}
