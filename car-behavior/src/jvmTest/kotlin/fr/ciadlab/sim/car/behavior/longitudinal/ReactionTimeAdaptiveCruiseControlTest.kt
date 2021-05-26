package fr.ciadlab.sim.car.behavior.longitudinal

import fr.ciadlab.sim.physics.Units.KilometersPerHour
import fr.ciadlab.sim.physics.unit
import org.junit.Test
import kotlin.test.assertTrue

class ReactionTimeAdaptiveCruiseControlTest {

    @Test
    fun testBraking() {
        val a = reactionTimeAdaptiveCruiseControl(
            50.0 unit KilometersPerHour,
            50.0 unit KilometersPerHour,
            40.0 unit KilometersPerHour,
            20.0)
        assertTrue(a < 0)
    }

    @Test
    fun testAcceleration() {
        val a = reactionTimeAdaptiveCruiseControl(
            50.0 unit KilometersPerHour,
            80.0 unit KilometersPerHour,
            80.0 unit KilometersPerHour,
            40.0,
            tau = 0.5)
        assertTrue(a > 0)
    }

    @Test
    fun testCompareTau() {
        val aTau1 = reactionTimeAdaptiveCruiseControl(
            50.0 unit KilometersPerHour,
            80.0 unit KilometersPerHour,
            80.0 unit KilometersPerHour,
            30.0,
            tau = 0.5)
        val aTau2 = reactionTimeAdaptiveCruiseControl(
            50.0 unit KilometersPerHour,
            80.0 unit KilometersPerHour,
            80.0 unit KilometersPerHour,
            30.0,
            tau = 2.0)
        assertTrue(aTau1 > aTau2)
    }

}
