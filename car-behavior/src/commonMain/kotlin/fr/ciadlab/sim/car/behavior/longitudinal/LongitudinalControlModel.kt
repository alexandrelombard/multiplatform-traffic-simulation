package fr.ciadlab.sim.car.behavior.longitudinal

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
