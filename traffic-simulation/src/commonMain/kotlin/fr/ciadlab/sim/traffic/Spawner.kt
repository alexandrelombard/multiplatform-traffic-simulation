package fr.ciadlab.sim.traffic

import fr.ciadlab.sim.math.geometry.Vector2D

class Spawner<Object>(
    var generation: (()->Object)? = null,
    var strategy: (()->Unit)? = null,
    var position: Vector2D = Vector2D(0.0, 0.0),
    var direction: Vector2D = Vector2D(1.0, 0.0),
    val onGeneration: MutableList<(Object) -> Unit> = arrayListOf())

fun <Object> TrafficSimulation.spawner(op: Spawner<Object>.() -> Unit): Spawner<Object> {
    val spawner = Spawner<Object>()
    op.invoke(spawner)
    this.spawners.add(spawner)
    return spawner
}

fun <Object> Spawner<Object>.generation(op: ()->Object) {
    this.generation = op
}

fun <Object> Spawner<Object>.strategy(op: ()->Unit) {
    this.strategy = op
}
