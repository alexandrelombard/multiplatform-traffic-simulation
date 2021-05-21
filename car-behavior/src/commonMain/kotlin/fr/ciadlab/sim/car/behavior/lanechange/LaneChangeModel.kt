package fr.ciadlab.sim.car.behavior.lanechange

import fr.ciadlab.sim.car.behavior.DriverState
import fr.ciadlab.sim.vehicle.Vehicle

/**
 * Enumeration of available lane change strategies
 * @author Alexandre Lombard
 */
enum class LaneChangeModel {
    MOBIL
}

typealias LaneChangeStrategy = (DriverState, Vehicle) -> Int
