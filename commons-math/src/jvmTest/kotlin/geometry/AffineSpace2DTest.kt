package geometry

import fr.ciadlab.sim.math.algebra.AffineSpace2D
import fr.ciadlab.sim.math.algebra.Vector2D
import org.junit.Test
import kotlin.test.assertEquals

class AffineSpace2DTest {
    @Test
    fun testFromDefault_withCenteredSpace() {
        val v0 = Vector2D(1.0, 1.0)
        val s0 = AffineSpace2D(Vector2D(0.0, 0.0), Vector2D(0.0, 1.0), Vector2D(-1.0, 0.0))

        val r0 = s0.fromDefault(v0)

        assertEquals(1.0, r0.x)
        assertEquals(-1.0, r0.y)
    }

    @Test
    fun testFromDefault() {
        val v0 = Vector2D(1.0, 1.0)
        val s0 = AffineSpace2D(Vector2D(-1.0, 1.0), Vector2D(0.0, 1.0), Vector2D(-1.0, 0.0))

        val r0 = s0.fromDefault(v0)

        assertEquals(0.0, r0.x)
        assertEquals(-2.0, r0.y)
    }

    @Test
    fun testToDefault_withCenteredSpace() {
        val v0 = Vector2D(1.0, -1.0)
        val s0 = AffineSpace2D(Vector2D(0.0, 0.0), Vector2D(0.0, 1.0), Vector2D(-1.0, 0.0))

        val r0 = s0.toDefault(v0)

        assertEquals(1.0, r0.x)
        assertEquals(1.0, r0.y)
    }

    @Test
    fun testToDefault() {
        val v0 = Vector2D(0.0, -2.0)
        val s0 = AffineSpace2D(Vector2D(-1.0, 1.0), Vector2D(0.0, 1.0), Vector2D(-1.0, 0.0))

        val r0 = s0.toDefault(v0)

        assertEquals(1.0, r0.x)
        assertEquals(1.0, r0.y)
    }

    @Test
    fun testCommutativity() {
        val v0 = Vector2D(1.0, 1.0)
        val s0 = AffineSpace2D(Vector2D(5.0, -3.0), Vector2D(1.0, 1.0).normalize(), Vector2D(-1.0, 1.0).normalize())

        assertEquals(v0, s0.toDefault(s0.fromDefault(v0)))
    }

    @Test
    fun testSimpleFromDefault() {
        val v0 = Vector2D(2.0, 1.0)
        val s0 = AffineSpace2D(Vector2D(0.0, 0.0), Vector2D(0.0, 1.0))

        assertEquals(1.0, s0.fromDefault(v0).x)
        assertEquals(-2.0, s0.fromDefault(v0).y)
    }
}
