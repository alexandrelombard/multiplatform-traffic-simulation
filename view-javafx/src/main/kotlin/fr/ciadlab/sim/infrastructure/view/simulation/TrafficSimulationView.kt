package fr.ciadlab.sim.infrastructure.view.simulation

import fr.ciadlab.sim.infrastructure.view.debug.DriverDebugView
import fr.ciadlab.sim.infrastructure.view.debug.driverDebugView
import fr.ciadlab.sim.infrastructure.view.network.intersectionView
import fr.ciadlab.sim.infrastructure.view.network.roadNetworkView
import fr.ciadlab.sim.infrastructure.view.network.roadView
import fr.ciadlab.sim.infrastructure.view.network.trafficLightsView
import fr.ciadlab.sim.infrastructure.view.vehicle.VehicleView
import fr.ciadlab.sim.infrastructure.view.vehicle.vehicleView
import fr.ciadlab.sim.traffic.TrafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle
import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.Parent
import tornadofx.removeFromParent
import java.util.concurrent.ConcurrentHashMap

class TrafficSimulationView(val trafficSimulation: TrafficSimulation<Vehicle>, var debug: Boolean = false) : Group()

fun Parent.trafficSimulationView(
    trafficSimulation: TrafficSimulation<Vehicle>,
    op: TrafficSimulationView.() -> Unit = {}
) {
    // Initialization
    val trafficSimulationView = TrafficSimulationView(trafficSimulation)
    op.invoke(trafficSimulationView)

    // Collection of elements which are to be visually updated
    val vehicleViews = ConcurrentHashMap<Vehicle, VehicleView>()
    val driverDebugViews = ConcurrentHashMap<Vehicle, DriverDebugView>()
    val trafficLightViews = arrayListOf<Group>()

    // Register a listener to on spawn to re-create the vehicle views
    trafficSimulation.onSpawn.add { vehicle, _ ->
        Platform.runLater {
            // Creating a view for the vehicle
            val view = vehicleView(vehicle)
            vehicleViews[vehicle] = view

            // Eventually create the driver debug view
            if(trafficSimulationView.debug) {
                val data = trafficSimulation.debugData[vehicle]
                if(data != null) {
                    driverDebugViews[vehicle] = driverDebugView(data)
                }
            }
        }
    }

    // Register a listener to remove the vehicle views of old vehicles
    trafficSimulation.onDestroy.add { vehicle ->
        Platform.runLater {
            // Removing the view for the vehicle
            vehicleViews[vehicle]?.removeFromParent()
            vehicleViews.remove(vehicle)

            // Removing the debug view of the driver behavior
            driverDebugViews[vehicle]?.removeFromParent()
            driverDebugViews.remove(vehicle)
        }
    }

    // Register a listener to update the traffic light views


    // Register a listener to update the vehicle views
    trafficSimulation.onAfterStep.add {
        // FIXME Copy prevent concurrent access but impacts performance
        val vehicles = hashSetOf(*trafficSimulation.vehicles.toTypedArray())
        Platform.runLater {
            vehicles.forEach {
                // Update the vehicle
                vehicleViews[it]?.update(it)

                // Eventually update the debug display
                driverDebugViews[it]?.update(trafficSimulation.debugData[it])
            }
        }
    }

    // Draw the road network
    roadNetworkView(trafficSimulation.roadNetwork) {
        laneWidth = 3.5
        roadNetwork.roads.forEach { roadView(it, debug = true) }
        roadNetwork.intersections.forEach { intersectionView(it) }
        roadNetwork.trafficLights.forEach { trafficLightsView(it) }
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
