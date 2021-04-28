package fr.ciadlab.sim.infrastructure.view.vehicle

import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.vehicle.Vehicle
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import tornadofx.circle
import tornadofx.group
import tornadofx.imageview
import tornadofx.opcr
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class VehicleView(var vehicle: Vehicle): Group() {
    private val BRAKE_COLOR = Color(1.0, 0.0, 0.0, 0.5)

    private lateinit var imageView: ImageView
    private lateinit var leftBrakeLight: Circle
    private lateinit var rightBrakeLight: Circle

    val view = group {
        imageView = imageview {
            image = Image("/car_up_right.png")

            x = vehicle.position.x
            y = vehicle.position.y
            fitWidth = vehicle.length
            fitHeight = 0.5 * vehicle.length
            rotate = Math.toDegrees(vehicle.yaw)
        }
        leftBrakeLight = circle {
            centerX = vehicle.position.x - imageView.fitWidth / 2.0 * cos(vehicle.yaw)
            centerY = vehicle.position.y - imageView.fitWidth / 2.0 * sin(vehicle.yaw)
            radius = imageView.fitWidth / 4.0
            fill = BRAKE_COLOR
        }
        rightBrakeLight = circle {
            centerX = vehicle.position.x - imageView.fitWidth / 2.0 * cos(vehicle.yaw)
            centerY = vehicle.position.y - imageView.fitWidth / 2.0 * sin(vehicle.yaw)
            radius = imageView.fitWidth / 4.0
            fill = BRAKE_COLOR
        }
    }

    fun update(vehicle: Vehicle) {
        imageView.x = vehicle.position.x - imageView.fitWidth / 2.0
        imageView.y = vehicle.position.y - imageView.fitHeight / 2.0
        imageView.rotate = Math.toDegrees(atan2(vehicle.direction.y, vehicle.direction.x))

        val leftBrakeLightPosition = getLeftBrakeLightPosition()
        leftBrakeLight.centerX = leftBrakeLightPosition.x
        leftBrakeLight.centerY = leftBrakeLightPosition.y

        val rightBrakeLightPosition = getRightBrakeLightPosition()
        rightBrakeLight.centerX = rightBrakeLightPosition.x
        rightBrakeLight.centerY = rightBrakeLightPosition.y

        if(vehicle.brakeLightOn) {
            leftBrakeLight.fill = BRAKE_COLOR
            rightBrakeLight.fill = BRAKE_COLOR
        } else {
            leftBrakeLight.fill = Color.TRANSPARENT
            rightBrakeLight.fill = Color.TRANSPARENT
        }

        this.vehicle = vehicle
    }

    private fun getLeftBrakeLightPosition() =
        Vector2D(
            vehicle.position.x - imageView.fitWidth / 2.0 * cos(vehicle.yaw) - sin(vehicle.yaw) * imageView.fitHeight / 3.0,
            vehicle.position.y - imageView.fitWidth / 2.0 * sin(vehicle.yaw) - cos(vehicle.yaw) * imageView.fitHeight / 3.0)

    private fun getRightBrakeLightPosition() =
        Vector2D(
            vehicle.position.x - imageView.fitWidth / 2.0 * cos(vehicle.yaw) + sin(vehicle.yaw) * imageView.fitHeight / 3.0,
            vehicle.position.y - imageView.fitWidth / 2.0 * sin(vehicle.yaw) + cos(vehicle.yaw) * imageView.fitHeight / 3.0)
}

fun Parent.vehicleView(vehicle: Vehicle, op: VehicleView.() -> Unit = {}): VehicleView =
    opcr(this, VehicleView(vehicle), op)
