package fr.ciadlab.sim.math.util

import kotlin.math.abs

object Precision {
    /**
     * Compares two numbers given some amount of allowed error.
     * @param x the first number
     * @param y the second number
     * @param eps the amount of error to allow when checking for equality
     * @return <ul><li>0 if  {@link #equals(double, double, double) equals(x, y, eps)}</li>
     *       <li>&lt; 0 if !{@link #equals(double, double, double) equals(x, y, eps)} &amp;&amp; x &lt; y</li>
     *       <li>> 0 if !{@link #equals(double, double, double) equals(x, y, eps)} &amp;&amp; x > y or
     *       either argument is NaN</li></ul>
     */
    fun compareTo(x: Double, y: Double, eps: Double): Int {
        if (equals(x, y, eps)) {
            return 0
        } else if (x < y) {
            return -1
        }
        return 1
    }

    /**
     * Returns true iff they are equal as defined by
     * [equals(x, y, 1)][.equals].
     *
     * @param x first value
     * @param y second value
     * @return `true` if the values are equal.
     */
    fun equals(x: Double, y: Double): Boolean {
        return equals(x, y, 1.0)
    }

    /**
     * Returns true if the arguments are equal or within the range of allowed
     * error (inclusive).  Returns `false` if either of the arguments
     * is NaN.
     *
     * @param x first value
     * @param y second value
     * @param eps the amount of absolute error to allow.
     * @return `true` if the values are equal or within range of each other.
     * @since 2.2
     */
    fun equals(x: Double, y: Double, eps: Double): Boolean {
        return abs(y - x) <= eps
    }
}
