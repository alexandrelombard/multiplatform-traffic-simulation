package fr.ciadlab.sim.car.perception

class Grid2D<T>(
    val width: Int, val height: Int, private val data: Array<T>) {

    operator fun get(x: Int, y: Int): T {
        return data[y * width + x]
    }

}

inline fun <reified T> Grid2D(width: Int, height: Int, init: (Int, Int) -> T) =
    Grid2D(width, height, Array(width * height) { init.invoke(it % width, it / width) })