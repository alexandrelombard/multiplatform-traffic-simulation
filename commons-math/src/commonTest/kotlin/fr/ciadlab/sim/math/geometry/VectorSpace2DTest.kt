package fr.ciadlab.sim.math.geometry

import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.math.algebra.VectorSpace2D
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for VectorSpace2D
 * @author Alexandre Lombard
 */
class VectorSpace2DTest {
    @Test
    fun testFromDefault() {
        val v0 = Vector2D(1.0, 1.0)
        val s0 = VectorSpace2D(Vector2D(0.0, 1.0), Vector2D(-1.0, 0.0))

        val r0 = s0.fromDefault(v0)

        assertEquals(1.0, r0.x)
        assertEquals(-1.0, r0.y)
    }
}
