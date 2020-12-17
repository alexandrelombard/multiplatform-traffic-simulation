package fr.ciadlab.sim.car.behavior.lateral

import fr.ciadlab.sim.math.geometry.Vector2D
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.test.Test

class StanleyLateralControlKtTest {

    @Test
    fun stanleyLateralControl_testLine() {
        var car = Vehicle(
            Vector2D(0.0, 20.0),
            Vector2D(10.0, 0.0), 0.0,
            Vector2D(1.0, 0.0), 0.0, 3.0, 5.0)
        val deltaTime = 0.01

        var cumulativeTime = 0.0

        for(i in 0..2500) {
            val angleError = car.yaw
            val lateralError = car.position.y
            val velocity = car.velocity.norm
            val gain = 5.0

            val wheelAngle = stanleyLateralControl(
                angleError,
                lateralError,
                velocity,
                gain
            )

            car = car.update(0.0, wheelAngle, deltaTime)

            println("${cumulativeTime.format()}\t${car.position.y.format()}")

            cumulativeTime += deltaTime
        }
    }

    @Test
    fun stanleyLateralControl_circleTest() {
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
//            val projection = carTheta - 2 * car.velocity.norm / radius

            val angleError = car.yaw - (carTheta - PI / 2.0)
            val lateralError = carR - radius
            val velocity = car.velocity.norm

            val gain = 5.0

            val wheelAngle = stanleyLateralControl(
                angleError,
                lateralError,
                velocity,
                gain
            )

            car = car.update(0.0, wheelAngle, deltaTime)

            println("${cumulativeTime.format()}\t${(carR - radius).format()}")

            cumulativeTime += deltaTime
        }
    }
}