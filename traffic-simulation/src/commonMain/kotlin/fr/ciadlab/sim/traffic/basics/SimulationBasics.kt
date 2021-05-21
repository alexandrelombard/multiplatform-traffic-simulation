package fr.ciadlab.sim.traffic.basics

import fr.ciadlab.sim.car.behavior.DriverAction
import fr.ciadlab.sim.traffic.TrafficSimulation
import fr.ciadlab.sim.vehicle.LightState
import fr.ciadlab.sim.vehicle.Vehicle

/**
 * Simple functions that can be used as default behaviors for simulation update.
 * @author Alexandre Lombard
 */

/**
 * Update a vehicle
 */
fun TrafficSimulation<Vehicle>.basicVehicleUpdate(vehicle: Vehicle, action: DriverAction, deltaTime: Double): Vehicle {
    return vehicle
        .update(action.targetAcceleration, action.targetWheelAngle, deltaTime)
        .changeLights(
            vehicle.lights.copy(
                leftBlinker = if (action.blinkerLeft) LightState.BLINKING else LightState.OFF,
                rightBlinker = if(action.blinkerRight) LightState.BLINKING else LightState.OFF))

}
