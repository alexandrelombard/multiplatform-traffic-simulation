package fr.ciadlab.sim.math.algebra

/**
 * Represents an immutable Vector3D
 */
data class Vector3D(val x: Double, val y: Double, val z: Double) {
    constructor(scalingFactor: Double, v: Vector3D) :
            this(scalingFactor * v.x, scalingFactor * v.y, scalingFactor * v.z)

    val squaredNorm: Double by lazy { x * x + y * y + z * z }
    val norm: Double by lazy { kotlin.math.sqrt(x * x + y * y + z * z) }

    val xy: Vector2D by lazy { Vector2D(x, y) }
    val xz: Vector2D by lazy { Vector2D(x, z) }
    val yz: Vector2D by lazy { Vector2D(y, z) }

    /**
     * Computes the dot product of this vector and the one given as parameter
     * @param v the other vector
     * @return the dot product of the two vectors
     */
    fun dot(v: Vector3D) = x * v.x + y * v.y + z * v.z
    fun add(scalingFactor: Double, v: Vector3D) = this + Vector3D(
        scalingFactor,
        v
    )
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
    fun distance(v: Vector3D) = (this - v).norm

    /**
     * Computes the cross product of this vector and another vector
     * @param v the other vector
     * @return the cross product of this and <code>v</code>
     */
    fun cross(v: Vector3D) =
        Vector3D(
            this.y * v.z - this.z * v.y,
            this.z * v.x - this.x * v.z,
            this.x * v.y - this.y * v.x)

    /**
     * Calculates the euclidean distance from a point to a line segment.
     * @param v the point
     * @param a start of line segment
     * @param b end of line segment
     * @return distance from v to line segment [a,b]
     */
    fun distanceToSegment(a: Vector3D, b: Vector3D): Double {
        val ab = b - a
        val av = this - a
        if (av.dot(ab) <= 0.0)  // Point is lagging behind start of the segment, so perpendicular distance is not viable.
            return av.norm // Use distance to start of segment instead.
        val bv = this - b
        return if (bv.dot(ab) >= 0.0) bv.norm else ab.cross(av).norm / ab.norm
    }

    /**
     * Computes the projection of this point on the line defined by two points
     * @param a the first point defining the line
     * @param b the second point defining the line
     * @return the projection of this point on the line defined by <code>a</code> and <code>b</code>
     */
    fun projectOnLine(a: Vector3D, b: Vector3D): Vector3D {
        val ab = b - a
        val av = this - a
        val w = av - ab * av.dot(ab) / ab.squaredNorm
        return this - w
    }

    /**
     * Computes the projection of this point on the segment defined by two points.
     * If the projection does not belong to the segment, the closest end of the segment is
     * returned.
     * @param a the first point defining the segment
     * @param b the second point defining the segment
     * @return the projection of this point on the segment defined by <code>a</code> and <code>b</code>
     */
    fun projectOnSegment(a: Vector3D, b: Vector3D): Vector3D {
        val ab = b - a
        val av = this - a

        if(av.dot(ab) <= 0)
            return a
        if(av.dot(ab) >= ab.squaredNorm)
            return b

        val w = av - ab * av.dot(ab) / ab.squaredNorm
        return this - w
    }

    operator fun plus(v: Vector3D) =
        Vector3D(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vector3D) =
        Vector3D(x - v.x, y - v.y, z - v.z)
    operator fun unaryMinus() = Vector3D(-x, -y, -z)
    operator fun times(l: Double) = Vector3D(x * l, y * l, z * l)
    operator fun div(l: Double) = Vector3D(x / l, y / l, z / l)
}

operator fun Double.times(v: Vector3D) = v * this

/**
 * Converts a Vector2D to a Vector3D, the 3rd component will be set to 0.0
 * @return a new Vector3D from the Vector2D
 */
fun Vector2D.toVector3D(): Vector3D {
    return Vector3D(this.x, this.y, 0.0)
}

/**
 * Computes the distance of a point to a polyline
 * @param p the point
 * @return the Euclidean distance of the point to the polyline
 */
fun List<Vector3D>.distance(p: Vector3D): Double {
    var minDistance = Double.MAX_VALUE

    for(i in 0 until this.size - 1) {
        val p0 = this[i]
        val p1 = this[i + 1]

        val distance = p.distanceToSegment(p0, p1)
        if(distance < minDistance) {
            minDistance = distance
        }
    }

    return minDistance
}

data class ProjectionData(
    val projection: Vector3D,
    val length: Double,
    val distance: Double,
    val segmentBegin: Vector3D,
    val segmentEnd: Vector3D)

/**
 * Computes the length of a polyline
 */
fun List<Vector3D>.length(): Double {
    var length = 0.0
    for(i in 0 until this.size - 1) {
        length += this[i].distance(this[i + 1])
    }
    return length
}

/**
 * Gets the coordinates of the point at the given length
 * If the length exceed the length of the path, it returns the last point.
 * If the length is negative the first point will be returned.
 * @param length the curvilinear abscissa of the desired point on the polyline
 * @return the point at the given abscissa
 */
fun List<Vector3D>.pointAtLength(length: Double): Vector3D {
    var currentLength = 0.0
    for(i in 0 until this.size - 1) {
        val nextLength = currentLength + this[i].distance(this[i + 1])

        if(nextLength > length) {
            val ratio = (length - currentLength) / (nextLength - currentLength)
            return (1.0 - ratio) * this[i] + ratio * this[i + 1]
        }

        currentLength = nextLength
    }

    return this.last()
}

fun List<Vector3D>.project(p: Vector3D): ProjectionData {
    if(this.size < 2) {
        throw IllegalArgumentException("The size of the list should be at least 2")
    }

    var minDistance = Double.MAX_VALUE
    var projection: Vector3D = this[0]
    var projectionLength = 0.0
    var currentLength = 0.0
    var segmentBegin: Vector3D = Vector3D(0.0, 0.0, 0.0)
    var segmentEnd: Vector3D = Vector3D(0.0, 0.0, 0.0)

    for(i in 0 until this.size - 1) {
        val p0 = this[i]
        val p1 = this[i + 1]

        val segmentProjection = p.projectOnSegment(p0, p1)
        val distance = p.distance(segmentProjection)
        if(distance < minDistance) {
            minDistance = distance
            projection = segmentProjection
            projectionLength = currentLength + p0.distance(projection)
            segmentBegin = p0
            segmentEnd = p1
        }

        currentLength += p0.distance(p1)
    }

    return ProjectionData(
        projection,
        projectionLength,
        minDistance,
        segmentBegin,
        segmentEnd)
}
