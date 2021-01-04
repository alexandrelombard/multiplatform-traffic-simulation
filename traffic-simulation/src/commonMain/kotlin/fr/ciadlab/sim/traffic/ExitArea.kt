package fr.ciadlab.sim.traffic

import fr.ciadlab.sim.math.geometry.Vector2D

class ExitArea(
    var position: Vector2D = Vector2D(0.0, 0.0),
    var radius: Double = 10.0,
    /** Checking if a position is inside the exit area, by default it's inside
     * if it's within a 10m radius */
    var isInside: (Vector2D) -> Boolean = { it.distance(position) < radius })

fun TrafficSimulation<*>.exitArea(op: ExitArea.() -> Unit): ExitArea {
    val dslExitArea = ExitArea()
    op.invoke(dslExitArea)
    exitAreas.add(dslExitArea)
    return dslExitArea
}
