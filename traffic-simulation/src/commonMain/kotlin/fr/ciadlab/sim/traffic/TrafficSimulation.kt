package fr.ciadlab.sim.traffic

import fr.ciadlab.sim.car.behavior.DriverBehavioralAction
import fr.ciadlab.sim.infrastructure.RoadNetwork
import fr.ciadlab.sim.vehicle.Position2D

class TrafficSimulation<VehicleType : Position2D>(
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
    val onSpawn: MutableList<(VehicleType, Spawner<VehicleType>)->Unit> = arrayListOf(),
    /** The function called when a vehicle is destroyed */
    val onDestroy: MutableList<(VehicleType)->Unit> = arrayListOf(),
    /** Function called before a step is run */
    val onBeforeStep: MutableList<(Double)->Unit> = arrayListOf(),
    /** Function called after a step is run */
    val onAfterStep: MutableList<(Double)->Unit> = arrayListOf(),
    /** The set of spawned vehicles */
    var vehicles: MutableSet<VehicleType> = hashSetOf()
) {
    var simulationTime = 0.0

    /**
     * Run a simulation step
     * @param deltaTime the elapsed time since the last step
     */
    fun step(deltaTime: Double) {
        // Call before step
        onBeforeStep.forEach { it.invoke(deltaTime) }

        // Destroy the vehicles in the exit areas
        val insideExitArea = vehicles.filter { v -> exitAreas.any { it.isInside(v.position) } }
        vehicles.removeAll(insideExitArea)
        insideExitArea.forEach {
                v -> onDestroy.forEach { it(v) }
        }

        // Calls the spawning strategies
        spawners.forEach { it.strategy?.invoke(deltaTime) }

        // Update the infrastructure
        roadNetwork.trafficLights.forEach {
            val policy = it.policy
            val updatedTrafficLights = it.trafficLights.map {
                it.changeState(policy.currentState(it.laneConnectors.first(), simulationTime))
            }
            with(it.trafficLights) {
                clear()
                addAll(updatedTrafficLights)
            }
        }

        // Run the behaviors and update the vehicles
        val updatedVehicles = vehicles.map {
            // Compute the action from the behavior
            val action = vehicleBehavior.invoke(it, deltaTime)
            // Apply the action to get an updated vehicle
            vehicleUpdate.invoke(it, action, deltaTime)
        }

        vehicles = updatedVehicles.toMutableSet()

        simulationTime += deltaTime

        // Call after step
        onAfterStep.forEach { it.invoke(deltaTime) }
    }
}

fun <VehicleType : Position2D>trafficSimulation(op: TrafficSimulation<VehicleType>.() -> Unit): TrafficSimulation<VehicleType> {
    val trafficSimulation = TrafficSimulation<VehicleType>()
    op.invoke(trafficSimulation)

    // Add an event handler to register the spawned elements
    trafficSimulation.spawners.forEach {
        it.onGeneration.add { obj -> trafficSimulation.vehicles.add(obj) }
    }

    return trafficSimulation
}

fun <VehicleType : Position2D>TrafficSimulation<VehicleType>.roadNetwork(op: RoadNetwork.() -> Unit) {
    val roadNetwork = RoadNetwork()
    op.invoke(roadNetwork)

    this.roadNetwork = roadNetwork
}
