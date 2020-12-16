package fr.ciadlab.sim.utils

import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.random.Random

/**
 * UUID v4 implementation for JavaScript
 * It's actually a copy paste of the JVM implementation
 * @author Alexandre Lombard
 */
actual class UUID : Comparable<UUID> {

    actual val mostSignificantBits: Long
    actual val leastSignificantBits: Long

    actual constructor(mostSignificantBits: Long, leastSignificantBits: Long) {
        this.mostSignificantBits = mostSignificantBits
        this.leastSignificantBits = leastSignificantBits
    }

    private constructor(data: ByteArray) {
        var msb: Long = 0
        var lsb: Long = 0
        for (i in 0..7) msb = msb shl 8 or (data[i] and 0xff.toByte()).toLong()
        for (i in 8..15) lsb = lsb shl 8 or (data[i] and 0xff.toByte()).toLong()
        this.mostSignificantBits = msb
        this.leastSignificantBits = lsb
    }

    override fun compareTo(other: UUID): Int {
        return  if (this.mostSignificantBits < other.mostSignificantBits) -1
                else if (this.mostSignificantBits > other.mostSignificantBits) 1
                else if (this.leastSignificantBits < other.leastSignificantBits) -1
                else if (this.leastSignificantBits > other.leastSignificantBits) 1
                else 0
    }

    actual override fun toString(): String {
        return digits(mostSignificantBits shr 32, 8) + "-" +
                digits(mostSignificantBits shr 16, 4) + "-" +
                digits(mostSignificantBits, 4) + "-" +
                digits(leastSignificantBits shr 48, 4) + "-" +
                digits(leastSignificantBits, 12)
    }

    private fun digits(`val`: Long, digits: Int): String? {
        val hi = 1L shl digits * 4
        return (hi or (`val` and hi - 1)).toString(16).substring(1)
    }

    actual companion object {
        actual fun randomUUID(): UUID {
            val randomBytes = ByteArray(16)
            Random.nextBytes(randomBytes)
            randomBytes[6] = randomBytes[6] and 0x0f.toByte()    /* clear version        */
            randomBytes[6] = randomBytes[6] or 0x40.toByte()     /* set to version 4     */
            randomBytes[8] = randomBytes[8] and 0x3f.toByte()    /* clear variant        */
            randomBytes[8] = randomBytes[8] or 0x80.toByte()     /* set to IETF variant  */
            return UUID(randomBytes)
        }

        actual fun fromString(name: String): UUID {
            val components: Array<String> = name.split("-").toTypedArray()
            if (components.size != 5) throw IllegalArgumentException("Invalid UUID string: $name")
            for (i in 0..4) components[i] = "0x" + components[i]

            var mostSigBits: Long = components[0].toLong()
            mostSigBits = mostSigBits shl 16
            mostSigBits = mostSigBits or components[1].toLong()
            mostSigBits = mostSigBits shl 16
            mostSigBits = mostSigBits or components[2].toLong()

            var leastSigBits: Long = components[3].toLong()
            leastSigBits = leastSigBits shl 48
            leastSigBits = leastSigBits or components[4].toLong()

            return UUID(mostSigBits, leastSigBits)
        }
    }

}