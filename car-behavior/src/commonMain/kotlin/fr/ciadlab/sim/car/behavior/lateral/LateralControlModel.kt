package fr.ciadlab.sim.car.behavior.lateral

/**
 * Enumerations of available lateral control models
 * @author Alexandre Lombard
 */
enum class LateralControlModel {
    /** Standard pure-pursuit model */
    PURE_PURSUIT,
    /** Stanley's model */
    STANLEY,
    /** Curvature based model */
    CURVATURE_BASED
}
