package fr.ciadlab.sim.tree

import fr.ciadlab.sim.AxisAlignedBoundingBox2D
import fr.ciadlab.sim.math.geometry.Vector2D
import kotlin.random.Random
import kotlin.test.Test
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
            for (i in 0..1000000) {
                quadTree.add(
                    Vector2D(
                        Random.nextDouble(-100.0, 100.0),
                        Random.nextDouble(-100.0, 100.0)
                    )
                )
            }
        }

        println("Duration: $duration - Leaves count: ${quadTree.countLeaves()}")
    }
}
