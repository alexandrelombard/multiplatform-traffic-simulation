package fr.ciadlab.sim.entity

import fr.ciadlab.sim.utils.UUID

/**
 * Represents an identifiable object. The identity of this object is defined by the identifier value.
 * @author Alexandre Lombard
 */
interface Identifiable {
    /**
     * Returns the identifier of this object
     */
    val identifier: UUID
}
