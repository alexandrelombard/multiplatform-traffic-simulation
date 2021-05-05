package fr.ciadlab.sim.infrastructure.viewjs.simulation

import fr.ciadlab.sim.infrastructure.view.debug.DriverDebugView
import fr.ciadlab.sim.infrastructure.view.debug.driverDebugView
import fr.ciadlab.sim.infrastructure.viewjs.car.VehicleView
import fr.ciadlab.sim.infrastructure.viewjs.car.vehicleView
import fr.ciadlab.sim.infrastructure.viewjs.network.intersectionView
import fr.ciadlab.sim.infrastructure.viewjs.network.roadNetworkView
import fr.ciadlab.sim.infrastructure.viewjs.network.roadView
import fr.ciadlab.sim.infrastructure.viewjs.network.trafficLightsView
import fr.ciadlab.sim.traffic.TrafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle
import org.w3c.dom.CanvasRenderingContext2D

class TrafficSimulationView(val trafficSimulation: TrafficSimulation<Vehicle>, var debug: Boolean = false)

fun CanvasRenderingContext2D.trafficSimulationView(trafficSimulation: TrafficSimulation<Vehicle>, op: TrafficSimulationView.() -> Unit = {}) {
    this.save()

    val trafficSimulationView = TrafficSimulationView(trafficSimulation)
    op.invoke(trafficSimulationView)

    // Collection of elements which are to be visually updated
    val vehicleViews = hashMapOf<Vehicle, VehicleView>()
    val driverDebugViews = hashMapOf<Vehicle, DriverDebugView>()

    // Register a listener to on spawn to re-create the vehicle views
    trafficSimulation.onSpawn.add { vehicle, _ ->
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

    // Register a listener to remove the vehicle views of old vehicles
    trafficSimulation.onDestroy.add { vehicle ->
        // Removing the view for the vehicle
        vehicleViews.remove(vehicle)

        // Removing the debug view of the driver behavior
        driverDebugViews.remove(vehicle)
    }

    // Register a listener to update the vehicle views
    trafficSimulation.onAfterStep.add {
        val vehicles = trafficSimulation.vehicles
        vehicles.forEach {
            vehicleViews[it]?.update(it)

            // Eventually update the debug display
            driverDebugViews[it]?.update(trafficSimulation.debugData[it])
        }
    }

    // Draw the road network
    roadNetworkView(trafficSimulation.roadNetwork, this.canvas) {
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

    // Draw the debug if required
    if(trafficSimulationView.debug) {
        trafficSimulation.debugData.values.forEach {
            driverDebugView(it)
        }
    }

    this.restore()
}
