package fr.ciadlab.sim.math.geometry

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue


class Vector3DTest {

    @Test
    fun testDistanceToSegment() {
        val a = Vector3D(0.0, 0.0, 0.0)
        val b = Vector3D(2.0, 0.0, 0.0)
        val p = Vector3D(1.0, 1.0, 0.0)

        val d = p.distanceToSegment(a, b)

        assertTrue(abs(d - 1.0) < 0.01)
    }

    @Test
    fun testProjectOnLine() {
        val a = Vector3D(0.0, 0.0, 0.0)
        val b = Vector3D(2.0, 0.0, 0.0)
        val p = Vector3D(1.0, 1.0, 0.0)

        val projection = p.projectOnLine(a, b)

        assertTrue(abs(projection.x - 1.0) < 0.01)
        assertTrue(abs(projection.y - 0.0) < 0.01)
        assertTrue(abs(projection.z - 0.0) < 0.01)
    }

    @Test
    fun testProjectOnLineBefore() {
        val a = Vector3D(0.0, 0.0, 0.0)
        val b = Vector3D(2.0, 0.0, 0.0)
        val p = Vector3D(-1.0, 1.0, 0.0)

        val projection = p.projectOnLine(a, b)

        assertTrue(abs(projection.x - -1.0) < 0.01)
        assertTrue(abs(projection.y - 0.0) < 0.01)
        assertTrue(abs(projection.z - 0.0) < 0.01)
    }

    @Test
    fun testProjectOnLineAfter() {
        val a = Vector3D(0.0, 0.0, 0.0)
        val b = Vector3D(2.0, 0.0, 0.0)
        val p = Vector3D(5.0, 1.0, 2.0)

        val projection = p.projectOnLine(a, b)

        assertTrue(abs(projection.x - 5.0) < 0.01)
        assertTrue(abs(projection.y - 0.0) < 0.01)
        assertTrue(abs(projection.z - 0.0) < 0.01)
    }

    @Test
    fun testProjectOnSegment() {
        val a = Vector3D(0.0, 0.0, 0.0)
        val b = Vector3D(2.0, 0.0, 0.0)
        val p = Vector3D(1.0, 1.0, 0.0)

        val projection = p.projectOnSegment(a, b)

        assertTrue(abs(projection.x - 1.0) < 0.01)
        assertTrue(abs(projection.y - 0.0) < 0.01)
        assertTrue(abs(projection.z - 0.0) < 0.01)
    }

    @Test
    fun testProjectOnSegmentBefore() {
        val a = Vector3D(0.0, 0.0, 0.0)
        val b = Vector3D(2.0, 0.0, 0.0)
        val p = Vector3D(-1.0, 1.0, 0.0)

        val projection = p.projectOnSegment(a, b)

        assertTrue(abs(projection.x - 0.0) < 0.01)
        assertTrue(abs(projection.y - 0.0) < 0.01)
        assertTrue(abs(projection.z - 0.0) < 0.01)
    }

    @Test
    fun testProjectOnSegmentAfter() {
        val a = Vector3D(0.0, 0.0, 0.0)
        val b = Vector3D(2.0, 0.0, 0.0)
        val p = Vector3D(5.0, 1.0, 2.0)

        val projection = p.projectOnSegment(a, b)

        assertTrue(abs(projection.x - 2.0) < 0.01)
        assertTrue(abs(projection.y - 0.0) < 0.01)
        assertTrue(abs(projection.z - 0.0) < 0.01)
    }

    @Test
    fun testProjectOnPolyline() {
        val polyline = listOf(
            Vector3D(0.0, 0.0, 0.0),
            Vector3D(1.0, 0.0, 0.0),
            Vector3D(2.0, 0.0, 0.0))

        val projectionData = polyline.project(Vector3D(0.6, 0.75, 0.0))

        assertTrue(abs(projectionData.projection.x - 0.6) < 0.01)
        assertTrue(abs(projectionData.projection.y - 0.0) < 0.01)
        assertTrue(abs(projectionData.projection.z - 0.0) < 0.01)
        assertTrue(abs(projectionData.distance - 0.75) < 0.01)
        assertTrue(abs(projectionData.length - 0.6) < 0.01)
    }

}