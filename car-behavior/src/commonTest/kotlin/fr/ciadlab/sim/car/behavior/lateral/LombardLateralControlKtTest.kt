package fr.ciadlab.sim.car.behavior.lateral

import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.math.*
import kotlin.test.Test

class LombardLateralControlKtTest {

    @Test
    fun lombardLateralControl_lineTest() {
        val initialVelocity = 10.0
        val deltaTime = 0.01
        var car = Vehicle(
            Vector2D(0.0, 20.0),
            Vector2D(initialVelocity, 0.0), 0.0,
            Vector2D(1.0, 0.0), 0.0, 3.0, 5.0)

        var cumulativeTime = 0.0

        for(i in 0..2500) {
            val angleError = car.yaw
            val lateralError = car.position.y
            val velocity = car.velocity.norm
            val reactionTime = 0.1
            val lookAheadDistance = max(2.0 * car.wheelBase, 2.0 * velocity)
            val left = car.position.y > 0.0
            val subtraction = Vector2D(1.0, 0.0) - car.direction
//            val curvature = if(subtraction.y > 0.0) -subtraction.norm else subtraction.norm // GOOD
//            val curvature = if(Vector2D.angle(Vector2D(1.0, 0.0), car.direction) > 0.0) -subtraction.norm else subtraction.norm // GOOD
            val det = 1.0 * car.direction.y - 0.0 * car.direction.x
            val curvature = if(det < 0.0) -subtraction.norm else subtraction.norm // GOOD
//            val curvature = subtraction.norm
//            val curvature = if(lateralError < 0.0) -subtraction.norm else subtraction.norm    // NOT GOOD
            val wheelBase = car.wheelBase

            val wheelAngle = lombardLateralControl(
                angleError,
                lateralError,
                left,
                velocity,
                reactionTime,
                curvature,
                lookAheadDistance,
                wheelBase
            )

            car = car.update(0.0, wheelAngle, deltaTime)                                       // Without limit
//            val wheelAngleLimit = 15.0 * PI / 180.0
//            car = car.update(0.0, max(-wheelAngleLimit, min(wheelAngleLimit, wheelAngle)), deltaTime)   // With limit

            println("${cumulativeTime.format()}\t${car.position.y.format()}")

            cumulativeTime += deltaTime
        }
    }

    @Test
    fun lombardLateralControl_circleTest() {
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
            val carTheta = atan2(car.position.y, car.position.x)

            val angleError = car.yaw - (carTheta - PI / 2.0)
            val lateralError = carR - radius
            val velocity = car.velocity.norm
            val reactionTime = 0.1
            val lookAheadDistance = max(2.0 * car.wheelBase, 2.0 * velocity)
//            val lookAheadDistance = max(2.0 * (car.wheelBase + 0.5), 2.0 * sqrt(velocity))  // sqrt(velocity) gives better results here
            val left = carR > radius
            val lookAheadPoint = car.position.add(lookAheadDistance, car.direction)
            val lookAheadPointTheta = atan2(lookAheadPoint.y, lookAheadPoint.x)
            val subtraction = Vector2D(
                cos(lookAheadPointTheta - PI / 2),
                sin(lookAheadPointTheta - PI / 2)
            ) - car.direction
//            val curvature = if(subtraction.y > 0.0) -subtraction.norm else subtraction.norm       // Definitely wrong
//            val curvature = if(lateralError < 0.0) -subtraction.norm else subtraction.norm        // GOOD
//            val curvature = if(Vector2D.angle(Vector2D(cos(lookAheadPointTheta - PI / 2), sin(lookAheadPointTheta - PI / 2)), car.direction) < 0.0) -subtraction.norm else subtraction.norm // GOOD
            val det = cos(lookAheadPointTheta - PI / 2) * car.direction.y - sin(lookAheadPointTheta - PI / 2) * car.direction.x
            val curvature = if(det < 0.0) -subtraction.norm else subtraction.norm // GOOD
//            val curvature = subtraction.norm      // BAD
//            val curvature = if(abs(atan2(subtraction.y, subtraction.x) - atan2(car.direction.y, car.direction.x) + PI / 2) >
//                abs(atan2(subtraction.y, subtraction.x) - atan2(car.direction.y, car.direction.x) - PI / 2)) subtraction.norm else -subtraction.norm  // BAD
            val wheelBase = car.wheelBase

            val wheelAngle = lombardLateralControl(
                angleError,
                lateralError,
                left,
                velocity,
                reactionTime,
                curvature,
                lookAheadDistance,
                wheelBase
            ) - 15.0 / 180.0 * PI

            val wheelAngleLimit = 20.0 * PI / 180.0
//            car = car.update(0.0, wheelAngle, deltaTime)                                        // Without limit
            car = car.update(0.0, max(-wheelAngleLimit, min(wheelAngleLimit, wheelAngle)), deltaTime)     // With limit

            println("${cumulativeTime.format()}\t${(carR - radius).format()}")
//            println("${fmt.format(cumulativeTime)}\t${fmt.format(carR - radius)}\t${fmt.format(curvature)}\t${fmt.format(carTheta)}\t${fmt.format(car.yaw)}")

            cumulativeTime += deltaTime
        }
    }
}
