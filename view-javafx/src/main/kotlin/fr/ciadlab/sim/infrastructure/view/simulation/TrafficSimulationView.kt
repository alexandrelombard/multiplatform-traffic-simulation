package fr.ciadlab.sim.infrastructure.view.simulation

import fr.ciadlab.sim.infrastructure.view.network.RoadNetworkView
import fr.ciadlab.sim.infrastructure.view.network.intersectionView
import fr.ciadlab.sim.infrastructure.view.network.roadNetworkView
import fr.ciadlab.sim.infrastructure.view.network.roadView
import fr.ciadlab.sim.traffic.TrafficSimulation
import javafx.scene.Group
import javafx.scene.Parent
import tornadofx.opcr

class TrafficSimulationView(val trafficSimulation: TrafficSimulation) : Group()

fun Parent.trafficSimulationView(trafficSimulation: TrafficSimulation, op : TrafficSimulationView.() -> Unit = {}) {
    trafficSimulation.spawners.forEach {
        spawnerView(it)
    }

    trafficSimulation.exitAreas.forEach {
        exitAreaView(it)
    }

    roadNetworkView(trafficSimulation.roadNetwork) {
        laneWidth = 3.5
        roadNetwork.roads.forEach { roadView(it, debug = true) }
        roadNetwork.intersections.forEach { intersectionView(it) }
    }
}
