package fr.ciadlab.sim.vehicle

import fr.ciadlab.sim.entity.Identifiable
import fr.ciadlab.sim.entity.Updatable
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.physics.Units.Degrees
import fr.ciadlab.sim.physics.unit
import fr.ciadlab.sim.utils.UUID
import kotlin.math.*
import kotlin.random.Random

/**
 * Simple and immutable 2D model of a vehicle.
 * The vehicle is updated by making new copy of the object to ensure thread-safety.
 * At the creation a UUID is given to the car. This UUID remains through subsequent updates.
 */
// TODO Use the Updatable abstract class
data class Vehicle(
    override val position: Vector2D,
    val velocity: Vector2D,
    val acceleration: Double,
    val direction: Vector2D,
    val wheelAngle: Double,
    val wheelBase: Double,
    val length: Double,
    val wheelAngleLimit: Double = 15.0 unit Degrees,
    val minAcceleration: Double = -8.0,
    val maxAcceleration: Double = 2.0,
    val lastCommand: VehicleCommand? = null,
    override val identifier: UUID = UUID.randomUUID(),
    override val onUpdate: MutableList<(Vehicle)->Unit> = arrayListOf()) : Position2D, Identifiable, Updatable<Vehicle> {

    /**
     * The heading of the vehicle
     */
    val yaw: Double by lazy { atan2(direction.y, direction.x) }

    /**
     * The speed of the vehicle
     */
    val speed: Double by lazy { velocity.norm }

    /**
     * True if the acceleration is negative (vehicle braking), below a threshold
     */
    val brakeLightOn = this.lastCommand?.acceleration ?: -0.1 < 0.0

    /**
     * Updates the vehicle with the given vehicle command
     * @param vehicleCommand the vehicle command
     * @param deltaTime the delta of time for the update
     */
    fun update(vehicleCommand: VehicleCommand, deltaTime: Double) =
        update(vehicleCommand.acceleration, vehicleCommand.wheelAngle, deltaTime)

    /**
     * Change instantaneously the speed of the vehicle
     * @param newSpeed the new speed
     */
    fun changeSpeed(newSpeed: Double) =
        this.copy(velocity = this.direction.normalize() * newSpeed)

    /**
     * Updates the vehicle with the given acceleration and the given angle of the wheel
     * @param acceleration the acceleration
     * @param wheelAngle the angle of the front wheel
     * @param deltaTime the delta of time for the update
     */
    fun update(acceleration: Double, wheelAngle: Double, deltaTime: Double): Vehicle {
        if(acceleration.isNaN())
            throw IllegalArgumentException("Acceleration cannot be NaN")
        if(wheelAngle.isNaN())
            throw IllegalArgumentException("Wheel angle cannot be NaN")

        // Get the current direction angle
        val direction = this.direction.alpha

        // Compute the new speed
        val newSpeed = this.velocity.norm + this.acceleration * deltaTime

        // Compute the traveled distance during the deltaTime assuming a constant acceleration for the period
        val traveledDistance = this.velocity.norm * deltaTime + 0.5 * this.acceleration * (deltaTime * deltaTime)

        // Compute the rotation properties according to the wheel angle
        // val radius = this.wheelBase / Math.tan(this.wheelAngle)
        val radius = this.wheelBase / tan(-this.wheelAngle)

        val newPosition : Vector2D
        val newDirectionVector : Vector2D
        val newVelocity : Vector2D

        if(abs(radius) > 1e7) {
            // We are in a straight line
            newDirectionVector = this.direction		// The direction doesn't change
            newVelocity = Vector2D(newSpeed, newDirectionVector)
            newPosition = this.position.add(traveledDistance, newDirectionVector)
        } else {
            val centerAngle = direction + PI / 2.0
            val centerDirection =
                Vector2D(cos(centerAngle), sin(centerAngle))
            val center = this.position.add(radius, centerDirection) // The sign of the radius is considered here
            val centralAngle = traveledDistance / radius // Warning: is the radius sign relevant here?
            // Compute the new direction angle according to the wheel angle and the acceleration
            val newDirection = direction + centralAngle
            newDirectionVector =
                Vector2D(cos(newDirection), sin(newDirection))

            // Compute the new velocity
            newVelocity = Vector2D(newSpeed, newDirectionVector)
            // Compute the new position
            val dx = this.position.x - center.x
            val dy = this.position.y - center.y
            newPosition = Vector2D(
                dx * cos(centralAngle) - dy * sin(centralAngle) + center.x,
                dx * sin(centralAngle) + dy * cos(centralAngle) + center.y
            )
        }

        // Returns the updated vehicle
        val updatedVehicle =
            this.copy(
                position = newPosition,
                velocity = newVelocity,
                acceleration = max(minAcceleration, min(maxAcceleration, acceleration)),
                direction = newDirectionVector,
                lastCommand = VehicleCommand(acceleration, wheelAngle),
                wheelAngle = max(-wheelAngleLimit, min(wheelAngleLimit, wheelAngle)))

        // Call the handlers
        onUpdate.forEach { it.invoke(updatedVehicle) }

        return updatedVehicle
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other == null)
            return false

        if(!(other is Vehicle))
            return false

        return identifier == other.identifier
    }
}

/**
 * Represents a vehicle command for this simple model
 */
data class VehicleCommand(val acceleration: Double, val wheelAngle: Double)

fun Vehicle.withSimulatedPositionError(errorRadius: Double = 0.1): Vehicle {
    val alpha = Random.nextDouble(2 * PI)
    return this.copy(
        position = this.position + Vector2D(cos(alpha), sin(alpha)) *  errorRadius)
}

fun Vehicle.withSimulatedDirectionError(errorAngleLimit: Double = 0.1): Vehicle {
    val erroneousDirection = this.direction.rotate(Random.nextDouble(-errorAngleLimit, errorAngleLimit))

    return this.copy(
        direction = erroneousDirection,
        velocity = Vector2D(this.velocity.norm, erroneousDirection))
}
