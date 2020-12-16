package fr.ciadlab.sim.math.util

fun doubleToRawLongBits(value: Double) = value.toRawBits()
fun longBitsToDouble(value: Long) = Double.fromBits(value)