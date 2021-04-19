package fr.ciadlab.sim.infrastructure

import fr.ciadlab.sim.math.algebra.Vector3D
import kotlin.js.JsName


data class Road(
    val points: List<Vector3D> = arrayListOf(),
    val oneWay: Boolean,
    val forwardLanesCount: Int,
    val backwardLanesCount: Int,
    val lanesWidth: List<Double> = MutableList(forwardLanesCount + backwardLanesCount) { 3.5 }) {

    init {
        if(points.size < 2) {
            throw IllegalArgumentException("A road must contain at least two points")
        }
    }

    val totalLanesCount = forwardLanesCount + backwardLanesCount
    val forwardLaneIndex = if(oneWay) 0 else backwardLanesCount

    val backwardLanes = if(backwardLanesCount == 0) emptyList() else 0 until backwardLanesCount
    val forwardLanes = forwardLaneIndex until totalLanesCount

    val length by lazy {
        var l = 0.0
        for(i in 1 until points.size) {
            l += points[i].distance(points[i - 1])
        }
        l
    }

    @JsName("isForwardLane")
    fun isForwardLane(laneIndex: Int) = laneIndex >= backwardLanesCount
    @JsName("isBackwardLane")
    fun isBackwardLane(laneIndex: Int) = laneIndex < backwardLanesCount

    /**
     * Computes the offset of the lane, related to the middle of the road
     * @param laneIndex the index of the lane (starting from 0 to the totalLanesCount)
     * @return the offset of the lane relative to the reference polyline
     */
    @JsName("laneOffset")
    fun laneOffset(laneIndex: Int) =
            if(totalLanesCount % 2 == 0) {
                laneIndex - ((totalLanesCount - 1) / 2.0)
            } else {
                laneIndex - ((totalLanesCount - 1) / 2.0)
            }

    /**
     * Gets the points of the given lane
     * @param laneIndex the index of the lane (starting from 0 to the totalLanesCount)
     * @return the polyline of the lane
     */
    @JsName("lane")
    fun lane(laneIndex: Int): List<Vector3D> {
        val laneOffset = laneOffset(laneIndex) * lanesWidth.subList(0, laneIndex).sum()
        return this.points.offset(laneOffset)
    }

    fun begin() = points.first()
    fun beginDirection(): Vector3D = (points[1] - points[0]).normalize()
    fun beginNormal(): Vector3D {
        val beginDirection =  beginDirection()
        return Vector3D(
            -beginDirection.y,
            beginDirection.x,
            beginDirection.z
        )
    }
    fun end() = points.last()
    fun endDirection(): Vector3D = (points.last() - points[points.size - 2]).normalize()
    fun endNormal(): Vector3D {
        val endDirection = endDirection()
        return Vector3D(-endDirection.y, endDirection.x, endDirection.z)
    }
}

/**
 * Utility function to translate a polyline of Vector3D
 * @param offset the offset to apply on the points of the polyline
 * @return the translated polyline
 */
@JsName("offset")
fun List<Vector3D>.offset(offset: Double) : List<Vector3D> {
    val result = arrayListOf<Vector3D>()

    // First point
    val beginDirection = (this[1] - this[0]).normalize()
    val beginNormal =
        Vector3D(-beginDirection.y, beginDirection.x, beginDirection.z)
    result.add(this[0] + (beginNormal * offset))

    // All middle points
    for(i in 1 until this.size - 1) {
        val direction = (this[i + 1] - this[i - 1]).normalize()
        val normal = Vector3D(-direction.y, direction.x, direction.z)

        result.add(this[i] + (normal * offset))
    }

    // Last point
    val endDirection = (this.last() - this[this.lastIndex - 1]).normalize()
    val endNormal =
        Vector3D(-endDirection.y, endDirection.x, endDirection.z)
    result.add(this.last() + (endNormal * offset))

    return result
}
