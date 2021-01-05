package fr.ciadlab.sim.traffic

import fr.ciadlab.sim.car.behavior.DriverBehavioralAction
import fr.ciadlab.sim.infrastructure.RoadNetwork

class TrafficSimulation<VehicleType>(
    /** The list of spawners */
    val spawners: MutableList<Spawner<VehicleType>> = arrayListOf(),
    /** The list of exit areas */
    val exitAreas: MutableList<ExitArea> = arrayListOf(),
    /** The road network */
    var roadNetwork: RoadNetwork = RoadNetwork(),
    /** The vehicle behavior */
    var vehicleBehavior: (VehicleType, Double)->DriverBehavioralAction = { _, _ -> DriverBehavioralAction(0.0, 0.0) },
    /** The function called to update a vehicle */
    var vehicleUpdate: (VehicleType, DriverBehavioralAction, Double)->VehicleType = { v, _, _ -> v },
    /** The function called when a vehicle is spawned */
    val onSpawn: MutableList<(VehicleType)->Unit> = arrayListOf(),
    /** The set of spawned vehicles */
    var vehicles: MutableSet<VehicleType> = hashSetOf()
) {
    /**
     * Run a simulation step
     * @param deltaTime the elapsed time since the last step
     */
    fun step(deltaTime: Double) {
        // Calls the spawning strategies
        spawners.forEach { it.strategy?.invoke(deltaTime) }

        // Run the behaviors and update the vehicles
        val updatedObjects = vehicles.map {
            // Compute the action from the behavior
            val action = vehicleBehavior.invoke(it, deltaTime)
            // Apply the action to get an updated vehicle
            vehicleUpdate.invoke(it, action, deltaTime)
        }

        vehicles = updatedObjects.toMutableSet()
    }
}

fun <VehicleType>trafficSimulation(op: TrafficSimulation<VehicleType>.() -> Unit): TrafficSimulation<VehicleType> {
    val trafficSimulation = TrafficSimulation<VehicleType>()
    op.invoke(trafficSimulation)

    // Add an event handler to register the spawned elements
    trafficSimulation.spawners.forEach {
        it.onGeneration.add { obj -> trafficSimulation.vehicles.add(obj) }
    }

    return trafficSimulation
}

fun <VehicleType>TrafficSimulation<VehicleType>.roadNetwork(op: RoadNetwork.() -> Unit) {
    val roadNetwork = RoadNetwork()
    op.invoke(roadNetwork)

    this.roadNetwork = roadNetwork
}
