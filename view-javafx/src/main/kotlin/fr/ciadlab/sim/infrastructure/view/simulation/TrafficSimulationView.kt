package fr.ciadlab.sim.infrastructure.view.simulation

import fr.ciadlab.sim.infrastructure.view.network.intersectionView
import fr.ciadlab.sim.infrastructure.view.network.roadNetworkView
import fr.ciadlab.sim.infrastructure.view.network.roadView
import fr.ciadlab.sim.infrastructure.view.vehicle.vehicleView
import fr.ciadlab.sim.traffic.TrafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle
import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.Parent
import tornadofx.add

class TrafficSimulationView(val trafficSimulation: TrafficSimulation<Vehicle>) : Group()

fun Parent.trafficSimulationView(trafficSimulation: TrafficSimulation<Vehicle>, op : TrafficSimulationView.() -> Unit = {}) {
    // Register a listener to on spawn to re-create the vehicle views
    trafficSimulation.onSpawn.add { vehicle, _-> Platform.runLater { vehicleView(vehicle) } }

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
    // FIXME This function is not regularly called
    trafficSimulation.vehicles.forEach {
        if(it is Vehicle) {
            vehicleView(it)
        }
    }
}
