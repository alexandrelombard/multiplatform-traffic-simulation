package fr.ciadlab.sim.traffic

import fr.ciadlab.sim.math.geometry.Vector2D

class Spawner<Object>(
    /** A function instantiating an object */
    var generation: (()->Object)? = null,
    /** A strategy for determining when to create new objects */
    var strategy: ((Double)->Unit)? = null,
    /** The position of this spawner */
    var position: Vector2D = Vector2D(0.0, 0.0),
    /** The direction of this spawner */
    var direction: Vector2D = Vector2D(1.0, 0.0),
    /** The listeners to the generation event */
    val onGeneration: MutableList<(Object) -> Unit> = arrayListOf()) {

    /**
     * Generate a vehicle using the generation function and calls the listeners
     * @return the generated object or <code>null</code> if nothing was generated
     */
    fun spawn(): Object? {
        val vehicle = generation?.invoke()
        if(vehicle != null) {
            onGeneration.forEach {
                it.invoke(vehicle)
            }
        }
        return vehicle
    }
}

fun <Object> TrafficSimulation<Object>.spawner(op: Spawner<Object>.() -> Unit): Spawner<Object> {
    // Build the spawner object
    val spawner = Spawner<Object>()
    op.invoke(spawner)
    // Register the spawner in the simulation object
    this.spawners.add(spawner)
    spawner.onGeneration.add(this.onSpawn)
    return spawner
}

fun <Object> Spawner<Object>.generation(op: ()->Object) {
    this.generation = op
}

fun <Object> Spawner<Object>.strategy(op: (Double)->Unit) {
    this.strategy = op
}
