package fr.ciadlab.sim.utils

/**
 * Wrapper for the JVM UUID class
 * @author Alexandre Lombard
 */
actual class UUID : Comparable<UUID> {

    actual val mostSignificantBits: Long
    actual val leastSignificantBits: Long

    private val jvmUuid: java.util.UUID

    actual constructor(mostSignificantBits: Long, leastSignificantBits: Long) {
        this.mostSignificantBits = mostSignificantBits
        this.leastSignificantBits = leastSignificantBits
        this.jvmUuid = java.util.UUID(mostSignificantBits, leastSignificantBits)
    }

    override fun compareTo(other: UUID): Int {
        return this.jvmUuid.compareTo(other.jvmUuid)
    }

    actual override fun toString(): String {
        return jvmUuid.toString()
    }

    actual companion object {
        /**
         * Generates a random UUID (wrapper for the JVM function)
         */
        actual fun randomUUID(): UUID {
            val jvmUuid = java.util.UUID.randomUUID()
            return UUID(jvmUuid.mostSignificantBits, jvmUuid.leastSignificantBits)
        }

        /**
         * Creates a UUID object from a String (wrapper for the JVM function)
         */
        actual fun fromString(name: String): UUID {
            val jvmUUID = java.util.UUID.fromString(name)
            return UUID(jvmUUID.mostSignificantBits, jvmUUID.leastSignificantBits)
        }
    }

}
