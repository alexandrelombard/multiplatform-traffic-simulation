package fr.ciadlab.sim.tree

import fr.ciadlab.sim.AxisAlignedBoundingBox2D
import fr.ciadlab.sim.math.geometry.Vector2D
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class QuadTreeTest {
    @ExperimentalTime
    @Test
    fun testAddPerformance() {
        val quadTree = QuadTree<Vector2D>(AxisAlignedBoundingBox2D(Vector2D(0.0, 0.0), 100.0)) {
            it
        }

        val duration = measureTime {
            for (i in 0..10000) {
                quadTree.add(
                    Vector2D(
                        Random.nextDouble(-100.0, 100.0),
                        Random.nextDouble(-100.0, 100.0)))
            }
        }

        // Even on a potato PC, it should be below 1s for 10000 add
        // For reference, I get 32.4ms
        assertTrue { duration.inSeconds <= 1.0f }

        println("Duration: $duration - Leaves count: ${quadTree.countLeaves()}")
    }

    @ExperimentalTime
    @Test
    fun testFetchElementsPerformance() {
        val quadTree = QuadTree<Vector2D>(AxisAlignedBoundingBox2D(Vector2D(0.0, 0.0), 100.0)) {
            it
        }

        for (i in 0..10000) {
            quadTree.add(
                Vector2D(
                    Random.nextDouble(-100.0, 100.0),
                    Random.nextDouble(-100.0, 100.0)))
        }

        val duration = measureTime {
            for (i in 0..100) {
                quadTree.fetchElements(
                    AxisAlignedBoundingBox2D(
                        Vector2D(Random.nextDouble(-100.0, 100.0), Random.nextDouble(-100.0, 100.0)),
                        Random.nextDouble(10.0, 50.0)))
            }
        }

        // Even on a potato PC, it should be below 1s for 10000 add
        // For reference, I get 61.7ms
        assertTrue { duration.inSeconds <= 1.0f }

        println("Duration: $duration - Leaves count: ${quadTree.countLeaves()}")
    }
}
