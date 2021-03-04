package fr.ciadlab.sim.infrastructure.view.simulation

import fr.ciadlab.sim.infrastructure.view.network.intersectionView
import fr.ciadlab.sim.infrastructure.view.network.roadNetworkView
import fr.ciadlab.sim.infrastructure.view.network.roadView
import fr.ciadlab.sim.infrastructure.view.vehicle.VehicleView
import fr.ciadlab.sim.infrastructure.view.vehicle.vehicleView
import fr.ciadlab.sim.traffic.TrafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle
import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.Parent
import tornadofx.add
import tornadofx.removeFromParent

class TrafficSimulationView(val trafficSimulation: TrafficSimulation<Vehicle>) : Group()

fun Parent.trafficSimulationView(
    trafficSimulation: TrafficSimulation<Vehicle>,
    op: TrafficSimulationView.() -> Unit = {}
) {
    val vehicleViews = hashMapOf<Vehicle, VehicleView>()

    // Register a listener to on spawn to re-create the vehicle views
    trafficSimulation.onSpawn.add { vehicle, _ ->
        Platform.runLater {
            val view = vehicleView(vehicle)             // Creating a view for the vehicle
            vehicleViews[vehicle] = view
        }
    }

    // Register a listener to remove the vehicle views of old vehicles
    trafficSimulation.onDestroy.add { vehicle ->
        Platform.runLater {
            vehicleViews[vehicle]?.removeFromParent()   // Removing the view for the vehicle
            vehicleViews.remove(vehicle)
        }
    }

    // Register a listener to update the vehicle views
    trafficSimulation.onAfterStep.add {
        val vehicles = trafficSimulation.vehicles
        Platform.runLater {
            vehicles.forEach {
                vehicleViews[it]?.update(it)
            }
        }
    }

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

    // Draw the vehicles that are manually created
    trafficSimulation.vehicles.forEach {
        vehicleView(it)
    }
}
