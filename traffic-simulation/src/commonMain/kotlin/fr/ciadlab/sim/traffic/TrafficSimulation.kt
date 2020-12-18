package fr.ciadlab.sim.traffic

import fr.ciadlab.sim.infrastructure.RoadNetwork

class TrafficSimulation(
    val spawners: MutableList<Spawner<*>> = arrayListOf(),
    val exitAreas: MutableList<ExitArea> = arrayListOf(),
    var roadNetwork: RoadNetwork = RoadNetwork())

fun trafficSimulation(op: TrafficSimulation.() -> Unit) {
    val trafficSimulation = TrafficSimulation()
    op.invoke(trafficSimulation)
}

fun TrafficSimulation.roadNetwork(op: RoadNetwork.() -> Unit) {
    val roadNetwork = RoadNetwork()
    op.invoke(roadNetwork)

    this.roadNetwork = roadNetwork
}
