package fr.ciadlab.sim.math.geometry

import fr.ciadlab.sim.math.algebra.Vector2D
import kotlin.test.Test
import kotlin.test.assertEquals

class Vector2DTest {

    @Test
    fun testOperatorPlus() {
        val v1 = Vector2D(0.0, 0.0)
        val v2 = Vector2D(1.0, 1.0)

        assertEquals(v2, v1 + v2)
        assertEquals(v1, v1 + v1)
        assertEquals(Vector2D(2.0, 2.0), v2 + v2)
    }

}
