package fr.ciadlab.sim.entity

/**
 * Represents an immutable object that can be updated. The update implies the creation of a new object, this creation
 * being notified to all listeners.
 * @author Alexandre Lombard
 */
interface Updatable<T>: Identifiable {
    /**
     * Returns the collection of update listeners for this object
     */
    val onUpdate: MutableList<(T)->Unit>
}
