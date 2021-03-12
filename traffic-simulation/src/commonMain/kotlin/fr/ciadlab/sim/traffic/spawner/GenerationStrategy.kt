package fr.ciadlab.sim.traffic.spawner

fun interface GenerationStrategy {
    fun apply(deltaTime: Double): Boolean
}
