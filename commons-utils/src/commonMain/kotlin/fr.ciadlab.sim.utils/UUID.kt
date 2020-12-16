package fr.ciadlab.sim.utils

expect class UUID : Comparable<UUID> {

    val mostSignificantBits: Long
    val leastSignificantBits: Long

    constructor(mostSignificantBits: Long, leastSignificantBits: Long)

    override fun toString(): String

    companion object {
        fun randomUUID(): UUID
        fun fromString(name: String): UUID
    }

}
