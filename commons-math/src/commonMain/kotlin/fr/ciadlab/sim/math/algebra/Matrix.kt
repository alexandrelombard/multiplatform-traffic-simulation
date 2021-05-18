package fr.ciadlab.sim.math.algebra

/**
 * Matrix class of doubles of any size
 * @author Alexandre Lombard
 */
data class Matrix(
    val values: Array<Double>,
    val width: Int) {

    val height: Int = values.size / width

    init {
        if(values.size % width != 0)
            throw IllegalArgumentException("Matrix width mismatch with the count of values " +
                    "(the size must be a multiple of the width)")
    }

    /**
     * Gets the element in position (x, y)
     */
    operator fun get(x: Int, y: Int): Double {
        return values[x * width + y]
    }

    /**
     * Computes the product of two matrices using an iterative algorithm
     * @param m the other matrix
     */
    operator fun times(m: Matrix): Matrix {
        if(this.width != m.height)
            throw IllegalArgumentException("Invalid dimensions for matrix product")

        val resultWidth = m.width
        val resultHeight = this.height

        val values = Array<Double>(resultWidth * resultHeight) {
            // (x, y) coordinates in the result matrix
            val x = it / resultWidth
            val y = it % resultWidth

            0.0 // TODO
        }

        TODO("Not yet implemented")
    }

}
