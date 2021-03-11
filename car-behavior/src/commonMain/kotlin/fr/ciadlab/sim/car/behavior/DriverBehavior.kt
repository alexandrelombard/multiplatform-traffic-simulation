package fr.ciadlab.sim.car.behavior

fun interface DriverBehavior {
    fun apply(deltaTime: Double): DriverBehavioralAction
}
