package fr.ciadlab.sim.car.behavior.longitudinal

/**
 * Represents a minimal longitudinal model
 * - distance is the distance between vehicles
 * - relativeSpeed is the relative speed between the vehicles (follower and leader)
 * - speed is the speed of the follower
 */
typealias LongitudinalModel = (distance: Double, relativeSpeed: Double, speed: Double, maximumSpeed: Double)->Double

/**
 * Enumeration of available lateral control models
 */
enum class LongitudinalControlModel {
    /** Enhanced intelligent driver model */
    ENHANCED_IDM,
    /** Intelligent driver model */
    IDM,
    /** Reaction-time based adaptive cruise control */
    RT_ACC,
    /** Model predictive control */
    MPC,
    /** Gipps model */
    GIPPS
}
