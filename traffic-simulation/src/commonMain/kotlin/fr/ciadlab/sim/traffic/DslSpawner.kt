package fr.ciadlab.sim.traffic

import fr.ciadlab.sim.math.geometry.Vector2D

class DslSpawner<Object>(
    var generation: (()->Object)? = null,
    var strategy: (()->Unit)? = null,
    var position: Vector2D = Vector2D(0.0, 0.0),
    var direction: Vector2D = Vector2D(1.0, 0.0),
    val onGeneration: MutableList<(Object) -> Unit> = arrayListOf())

fun <Object> DslTrafficSimulation.spawner(op: DslSpawner<Object>.() -> Unit): DslSpawner<Object> {
    val spawner = DslSpawner<Object>()
    op.invoke(spawner)
    this.spawners.add(spawner)
    return spawner
}

fun <Object> DslSpawner<Object>.generation(op: ()->Object) {
    this.generation = op
}

fun <Object> DslSpawner<Object>.strategy(op: ()->Unit) {
    this.strategy = op
}