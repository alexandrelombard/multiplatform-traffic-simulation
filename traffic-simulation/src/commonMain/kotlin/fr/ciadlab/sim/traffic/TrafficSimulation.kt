package fr.ciadlab.sim.traffic

import fr.ciadlab.sim.infrastructure.RoadNetwork

class TrafficSimulation<VehicleType>(
    val spawners: MutableList<Spawner<VehicleType>> = arrayListOf(),
    val exitAreas: MutableList<ExitArea> = arrayListOf(),
    var roadNetwork: RoadNetwork = RoadNetwork(),
    val spawnedObjects: MutableSet<VehicleType> = hashSetOf()
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

fun <VehicleType>trafficSimulation(op: TrafficSimulation<VehicleType>.() -> Unit): TrafficSimulation<VehicleType> {
    val trafficSimulation = TrafficSimulation<VehicleType>()
    op.invoke(trafficSimulation)

    // Add an event handler to register the spawned elements
    trafficSimulation.spawners.forEach {
        it.onGeneration.add { obj -> trafficSimulation.spawnedObjects.add(obj) }
    }

    return trafficSimulation
}

fun <VehicleType>TrafficSimulation<VehicleType>.roadNetwork(op: RoadNetwork.() -> Unit) {
    val roadNetwork = RoadNetwork()
    op.invoke(roadNetwork)

    this.roadNetwork = roadNetwork
}
