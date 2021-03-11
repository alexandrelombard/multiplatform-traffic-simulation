package fr.ciadlab.sim.entity

import fr.ciadlab.sim.utils.UUID

abstract class Updatable<T>(
    val onUpdate: MutableList<(T)->Unit> = arrayListOf(),
    val identifier: UUID = UUID.randomUUID()
) {
    protected fun fireUpdate(newState: T) {
        onUpdate.forEach {
            it.invoke(newState)
        }
    }

    override fun equals(other: Any?): Boolean {
        if(other == null)
            return false

        if(other::class != this::class)
            return false

        if(other !is Updatable<*>)
            return false

        return identifier == other.identifier
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }
}
