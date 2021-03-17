package fr.ciadlab.sim.infrastructure.viewjs.simulation

import fr.ciadlab.sim.infrastructure.viewjs.car.VehicleView
import fr.ciadlab.sim.infrastructure.viewjs.car.vehicleView
import fr.ciadlab.sim.infrastructure.viewjs.network.intersectionView
import fr.ciadlab.sim.infrastructure.viewjs.network.roadNetworkView
import fr.ciadlab.sim.infrastructure.viewjs.network.roadView
import fr.ciadlab.sim.infrastructure.viewjs.network.trafficLightsView
import fr.ciadlab.sim.traffic.TrafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle
import org.w3c.dom.CanvasRenderingContext2D

class TrafficSimulationView(val trafficSimulation: TrafficSimulation<Vehicle>)

fun CanvasRenderingContext2D.trafficSimulationView(trafficSimulation: TrafficSimulation<Vehicle>) {
    // Collection of elements which are to be visually updated
    val vehicleViews = hashMapOf<Vehicle, VehicleView>()

    // Register a listener to on spawn to re-create the vehicle views
    trafficSimulation.onSpawn.add { vehicle, _ ->
        val view = vehicleView(vehicle)             // Creating a view for the vehicle
        vehicleViews[vehicle] = view
    }

    // Register a listener to remove the vehicle views of old vehicles
    trafficSimulation.onDestroy.add { vehicle ->
//        vehicleViews[vehicle]?.removeFromParent()   // Removing the view for the vehicle
        vehicleViews.remove(vehicle)
    }

    // Register a listener to update the vehicle views
    trafficSimulation.onAfterStep.add {
        val vehicles = trafficSimulation.vehicles
        vehicles.forEach {
            vehicleViews[it]?.update(it)
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
}
