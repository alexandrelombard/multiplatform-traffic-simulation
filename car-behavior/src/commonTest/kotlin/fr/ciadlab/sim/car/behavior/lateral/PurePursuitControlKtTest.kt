package fr.ciadlab.sim.car.behavior.lateral

import fr.ciadlab.sim.math.geometry.Vector2D
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test

class PurePursuitControlKtTest {

    @Test
    fun purePursuit_lineTest() {
        val initialVelocity = 10.0
        val deltaTime = 0.01
        var car = Vehicle(
            Vector2D(0.0, 20.0),
            Vector2D(initialVelocity, 0.0), 0.0,
            Vector2D(1.0, 0.0), 0.0, 3.0, 5.0)

        var cumulativeTime = 0.0

        for(i in 0..2500) {
            val position = car.position
            val yaw = car.yaw
            val targetPoint =
                Vector2D(car.position.x + 2.0 * car.velocity.norm, 0.0)
            val frontAxleOffset = 2.0
            val rearAxleOffset = -2.0

            val wheelAngle = purePursuit(
                position,
                yaw,
                targetPoint,
                frontAxleOffset,
                rearAxleOffset
            )

            car = car.update(0.0, wheelAngle, deltaTime)

            println("${cumulativeTime.format()}\t${position.y.format()}")

            cumulativeTime += deltaTime
        }
    }

    @Test
    fun purePursuitControl_circleTest() {
        val radius = 10.0
        val initialVelocity = 10.0
        val deltaTime = 0.01
        var car = Vehicle(
            Vector2D(0.0, 20.0),
            Vector2D(initialVelocity, 0.0), 0.0,
            Vector2D(1.0, 0.0), 0.0, 3.0, 5.0)

        var cumulativeTime = 0.0

        for(i in 0..2500) {
            // Polar coordinates
            val carR = car.position.norm
            val carTheta = atan2(car.position.normalize().y, car.position.normalize().x)
            val projection = carTheta - 2 * car.velocity.norm / radius

            val position = car.position
            val yaw = car.yaw
            val targetPoint = Vector2D(
                radius * cos(projection),
                radius * sin(projection)
            )
            val frontAxleOffset = 2.0
            val rearAxleOffset = -2.0

            val wheelAngle = purePursuit(
                position,
                yaw,
                targetPoint,
                frontAxleOffset,
                rearAxleOffset
            )

            car = car.update(0.0, wheelAngle, deltaTime)

            println("${cumulativeTime.format()}\t${(carR - radius).format()}")

            cumulativeTime += deltaTime
        }
    }
}