package fr.ciadlab.sim.traffic.spawner

import fr.ciadlab.sim.traffic.TrafficSimulation
import kotlin.random.Random

/**
 * This spawning strategy generates objects with a random probability driven by a count of expected objects per second.
 * A minimum delay between two successive generation is also considered. Thus, the real generation output will always be
 * below the expected generation rate.
 * @author Alexandre Lombard
 */
class TimeAwareGenerationStrategy(
    val trafficSimulation: TrafficSimulation<*>,
    val expectedObjectsPerSecond: Double = 0.5,
    val minimumDelayBetweenGeneration: Double = 4.0) : GenerationStrategy {

    private var lastGeneration: Double = 0.0

    /**
     * Returns <code>true</code> if a new object should be spawned
     * @param deltaTime the delta time
     * @return <code>true</code> to spawn a new object, <code>false</code> otherwise
     */
    override fun apply(deltaTime: Double): Boolean {
        if(trafficSimulation.simulationTime - lastGeneration < minimumDelayBetweenGeneration)
            return false

        val generate = Random.nextDouble() < expectedObjectsPerSecond * deltaTime
        if(generate) {
            lastGeneration = trafficSimulation.simulationTime
        }
        return generate
    }
}
