package fr.ciadlab.sim.traffic

/**
 * Agent class
 * @author Alexandre Lombard
 */
class Agent<Object>(var behavior: (Object, Double) -> Unit) {
}
