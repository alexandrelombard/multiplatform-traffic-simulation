package fr.ciadlab.sim.physics

import kotlin.math.PI

fun Float.kph() = this / 3.6
fun Float.ms() = this / 1000
fun Double.kph() = this / 3.6
fun Double.ms() = this / 1000.0

/**
 * Converts a value to SI units
 * @param factor the factor (relative to the SI units) of the current unit
 */
infix fun Float.unit(factor: Double) = this * factor

/**
 * Converts a value to SI units
 * @param factor the factor (relative to the SI units) of the current unit
 */
infix fun Double.unit(factor: Double) = this * factor

object Units {
    // Velocity
    val KilometersPerHour = 1.0 / 3.6
    val MetersPerSecond = 1.0
    // Time
    val Milliseconds = 1.0 / 1000.0
    val Seconds = 1.0
    // Angles
    val Degrees = PI / 180.0
    val Radians = 1.0
    // Distances
    val Kilometers = 1000.0
    val Meters = 1.0
    val Millimeters = 1.0 / 1000.0
    // Mass
    val Kilograms = 1.0
    val Grams = 1.0 / 1000.0
}
