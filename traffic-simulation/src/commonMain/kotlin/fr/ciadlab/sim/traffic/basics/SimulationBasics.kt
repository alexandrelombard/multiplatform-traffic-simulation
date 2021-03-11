package fr.ciadlab.sim.traffic.basics

import fr.ciadlab.sim.car.behavior.DriverBehavioralAction
import fr.ciadlab.sim.traffic.TrafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle

/**
 * Simple functions that can be used as default behaviors for simulation update.
 * @author Alexandre Lombard
 */

/**
 * Update a vehicle
 */
fun TrafficSimulation<Vehicle>.basicVehicleUpdate(vehicle: Vehicle, action: DriverBehavioralAction, deltaTime: Double): Vehicle {
    return vehicle.update(action.targetAcceleration, action.targetWheelAngle, deltaTime)
}
