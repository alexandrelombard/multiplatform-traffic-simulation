package fr.ciadlab.sim.car.behavior.lateral

import kotlin.math.*

import fr.ciadlab.sim.math.geometry.Vector2D

/**
 * Computes the desired angle for the front wheels using the pure pursuit algorithm
 * @param position the position of the vehicle
 * @param yaw the heading of the vehicle
 * @param targetPoint the target point of the vehicle
 * @param frontAxleOffset the distance between the front axle and the position (usually a positive value)
 * @param rearAxleOffset the distance between the rear axle and the position (usually a negative value)
 * @return the desired angle for the front wheels (it may exceed the physical possibilities of the
 *         vehicle)
 */
fun purePursuit(
    position: Vector2D,
    yaw: Double,
    targetPoint: Vector2D,
    frontAxleOffset: Double,
    rearAxleOffset: Double
): Double {
    val e = frontAxleOffset - rearAxleOffset
    val xc = targetPoint.x
    val yc = targetPoint.y
    val xv = position.x + rearAxleOffset * cos(yaw)
    val yv = position.y + rearAxleOffset * sin(yaw)
    val alpha = yaw - PI

    val vec = Vector2D(xc - xv, yc - yv)

    if (vec.norm > 0)
        vec.normalize()
    else
        return 0.0  // It's an error case

    // Internal factor which may be used to bend the curvature
    val k = 2.0

    return asin(k * e * ((yc - yv) * cos(alpha) - (xc - xv) * sin(alpha)) / ((xc - xv).pow(2.0) + (yc - yv).pow(2.0)))
}
