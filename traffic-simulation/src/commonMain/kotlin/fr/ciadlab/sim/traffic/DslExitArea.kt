package fr.ciadlab.sim.traffic

import fr.ciadlab.sim.math.geometry.Vector2D

class DslExitArea(
    var position: Vector2D = Vector2D(0.0, 0.0),
    /** Checking if a position is inside the exit area, by default it's inside
     * if it's within a 10m radius */
    var isInside: (Vector2D) -> Boolean = { it.distance(position) < 10 })

fun DslTrafficSimulation.exitArea(op: DslExitArea.() -> Unit): DslExitArea {
    val dslExitArea = DslExitArea()
    op.invoke(dslExitArea)
    exitAreas.add(dslExitArea)
    return dslExitArea
}