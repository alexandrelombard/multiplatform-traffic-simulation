package fr.ciadlab.sim.math.algebra

import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Class representing a immutable Vector2D
 * @author Alexandre Lombard
 */
data class Vector2D(val x: Double, val y: Double) {

    constructor(scalingFactor: Double, v: Vector2D) : this(scalingFactor * v.x, scalingFactor * v.y)

    val alpha: Double by lazy { atan2(y, x) }
    val squaredNorm: Double by lazy { x * x + y * y }
    val norm: Double by lazy { kotlin.math.sqrt(x * x + y * y) }

    /**
     * Computes the dot product of this vector and the one given as parameter
     * @param v the other vector
     * @return the dot product of the two vectors
     */
    fun dot(v: Vector2D) = x * v.x + y * v.y

    fun cross(v: Vector2D) = x * v.y - y * v.x

    fun add(scalingFactor: Double, v: Vector2D) = this + Vector2D(scalingFactor, v)

    /**
     * Returns a normalized version of this vector
     * @return a new instance of this vector normalized
     */
    fun normalize() = this / norm
    /**
     * Computes the euclidean distance between the two vectors assuming they are representing points
     * @param v the second point
     * @return the euclidean distance between this and v
     */
    fun distance(v: Vector2D) = (this - v).norm

    /**
     * Computes the shortest signed angle between this vector and the one given as parameter
     * @param v the second vector
     */
    fun angle(v: Vector2D) = atan2(this.cross(v), this.dot(v))

    /**
     * Rotates the vector according to the given angle
     * @param angle the angle of rotation (in radians)
     */
    fun rotate(angle: Double) =
        Vector2D(cos(angle) * x - sin(angle) * y, sin(angle) * x + cos(angle) * y)

    operator fun plus(v: Vector2D) =
        Vector2D(x + v.x, y + v.y)
    operator fun minus(v: Vector2D) =
        Vector2D(x - v.x, y - v.y)
    operator fun unaryMinus() = Vector2D(-x, -y)
    operator fun times(l: Double) = Vector2D(x * l, y * l)
    operator fun div(l: Double) = Vector2D(x / l, y / l)

    operator fun Double.times(v: Vector2D) = v * this
}
