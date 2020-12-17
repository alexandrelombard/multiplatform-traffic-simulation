package fr.ciadlab.sim.car.behavior

import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.math.geometry.Vector3D
import fr.ciadlab.sim.vehicle.Vehicle

/**
 * Contains all the data required by a vehicle to take a decision
 * @author Alexandre Lombard
 */
data class DriverBehavioralState(
    /** The current road of the vehicle */
    val currentRoad: Road,
    /** The current lane index */
    val currentLaneIndex: Int,
    /** The current list of leaders (vehicles that this one will be following) */
    val leaders: List<Vehicle>,
    /** The max speed according to the road */
    val maximumSpeed: Double,
    /** The position of the goal */
    val goal: Vector3D)