package fr.ciadlab.sim.infrastructure.view.simulation

import fr.ciadlab.sim.infrastructure.view.network.RoadNetworkView
import fr.ciadlab.sim.infrastructure.view.network.intersectionView
import fr.ciadlab.sim.infrastructure.view.network.roadNetworkView
import fr.ciadlab.sim.infrastructure.view.network.roadView
import fr.ciadlab.sim.infrastructure.view.vehicle.vehicleView
import fr.ciadlab.sim.traffic.TrafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle
import javafx.scene.Group
import javafx.scene.Parent
import tornadofx.opcr

class TrafficSimulationView(val trafficSimulation: TrafficSimulation<*>) : Group()

fun Parent.trafficSimulationView(trafficSimulation: TrafficSimulation<*>, op : TrafficSimulationView.() -> Unit = {}) {
    // Draw the road network
    roadNetworkView(trafficSimulation.roadNetwork) {
        laneWidth = 3.5
        roadNetwork.roads.forEach { roadView(it, debug = true) }
        roadNetwork.intersections.forEach { intersectionView(it) }
    }

    // Draw the spawners
    trafficSimulation.spawners.forEach {
        spawnerView(it)
    }

    // Draw the exit areas
    trafficSimulation.exitAreas.forEach {
        exitAreaView(it)
    }

    // Draw the vehicles
    trafficSimulation.spawnedObjects.forEach {
        if(it is Vehicle) {
            vehicleView(it)
        }
    }
}
