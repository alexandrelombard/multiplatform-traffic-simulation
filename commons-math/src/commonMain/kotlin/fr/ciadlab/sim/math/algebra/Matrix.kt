package fr.ciadlab.sim.math.algebra

/**
 * Matrix class of any size
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

    operator fun get(x: Int, y: Int): Double {
        return values[x * width + y]
    }

    operator fun times(m: Matrix): Matrix {
        if(this.height != m.width)
            throw IllegalArgumentException("Invalid dimensions for matrix product")

        val resultWidth = this.width
        val resultHeight = m.height

//        val values = Array<Double>(resultWidth * resultHeight) {
//
//        }
        TODO("Not yet implemented")
    }

}
