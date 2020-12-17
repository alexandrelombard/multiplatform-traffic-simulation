package fr.ciadlab.sim.traffic

import fr.ciadlab.sim.infrastructure.RoadNetwork

class DslTrafficSimulation(
    val spawners: MutableList<DslSpawner<*>> = arrayListOf(),
    val exitAreas: MutableList<DslExitArea> = arrayListOf(),
    var roadNetwork: RoadNetwork = RoadNetwork())

fun trafficSimulation(op: DslTrafficSimulation.() -> Unit) {
    val trafficSimulation = DslTrafficSimulation()
    op.invoke(trafficSimulation)
}

fun DslTrafficSimulation.roadNetwork(op: RoadNetwork.() -> Unit) {
    val roadNetwork = RoadNetwork()
    op.invoke(roadNetwork)

    this.roadNetwork = roadNetwork
}