package fr.ciadlab.sim

import fr.ciadlab.sim.math.geometry.Vector2D
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AxisAlignedBoundingBox2DTest {
    @Test
    fun intersectsTest() {
        val bb1 = AxisAlignedBoundingBox2D(Vector2D(0.0, 0.0), 2.0)
        val bb2 = AxisAlignedBoundingBox2D(Vector2D(0.5, 0.5), 2.0)
        val bb3 = AxisAlignedBoundingBox2D(Vector2D(1.0, 1.0), 2.0)
        val bb4 = AxisAlignedBoundingBox2D(Vector2D(4.0, 4.0), 2.0)
        val bb5 = AxisAlignedBoundingBox2D(Vector2D(5.0, 5.0), 2.0)

        assertTrue { bb1.intersects(bb2) }
        assertTrue { bb1.intersects(bb3) }
        assertTrue { bb1.intersects(bb4) }
        assertFalse { bb1.intersects(bb5) }
    }
}
