package fr.ciadlab.sim.car.behavior.lanechange

import fr.ciadlab.sim.car.behavior.DriverBehavioralState
import fr.ciadlab.sim.vehicle.Vehicle

/**
 * Enumeration of available lane change strategies
 * @author Alexandre Lombard
 */
enum class LaneChangeModel {
    MOBIL
}

typealias LaneChangeStrategy = (DriverBehavioralState, Vehicle) -> Int
