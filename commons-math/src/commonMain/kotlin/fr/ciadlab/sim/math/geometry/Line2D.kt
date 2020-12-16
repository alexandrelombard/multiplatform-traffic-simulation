package fr.ciadlab.sim.math.geometry

import fr.ciadlab.sim.math.util.MathArrays
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.hypot

class Line2D {
    val tolerance: Double
    val angle: Double
    val cos: Double
    val sin: Double
    val originOffset: Double

    constructor(p1: Vector2D, p2: Vector2D, tolerance: Double) {
        this.tolerance = tolerance

        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        val d = hypot(dx, dy)
        if (d == 0.0) {
            angle        = 0.0
            cos          = 1.0
            sin          = 0.0
            originOffset = p1.y
        } else {
            angle        = PI + atan2(-dy, -dx)
            cos          = dx / d
            sin          = dy / d
            originOffset = MathArrays.linearCombination(
                p2.x,
                p1.y,
                -p1.x,
                p2.y
            ) / d
        }
    }

    fun getOffset(v: Vector2D): Double {
        return MathArrays.linearCombination(
            sin,
            v.x,
            -cos,
            v.y,
            1.0,
            this.originOffset
        );
    }
}