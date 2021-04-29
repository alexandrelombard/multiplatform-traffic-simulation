package fr.ciadlab.sim.infrastructure.view.vehicle

import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.vehicle.LightState
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
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class VehicleView(var vehicle: Vehicle): Group() {
    private val BRAKE_COLOR = Color(1.0, 0.0, 0.0, 0.5)
    private val BLINKER_COLOR = Color(1.0, 0.75, 0.0, 0.5)

    private lateinit var imageView: ImageView
    private lateinit var leftBrakeLight: Circle
    private lateinit var rightBrakeLight: Circle

    private lateinit var leftFrontBlinker: Circle
    private lateinit var rightFrontBlinker: Circle

    val view = group {
        // region Vehicle
        imageView = imageview {
            image = Image("/car_up_right.png")

            x = vehicle.position.x
            y = vehicle.position.y
            fitWidth = vehicle.length
            fitHeight = 0.5 * vehicle.length
            rotate = Math.toDegrees(vehicle.yaw)
        }
        // endregion

        // region Brake lights
        val leftBrakeLightPosition = getLeftBrakeLightPosition()
        val rightBrakeLightPosition = getRightBrakeLightPosition()
        leftBrakeLight = circle {
            centerX = leftBrakeLightPosition.x
            centerY = leftBrakeLightPosition.y
            radius = imageView.fitWidth / 4.0
            fill = BRAKE_COLOR
        }
        rightBrakeLight = circle {
            centerX = rightBrakeLightPosition.x
            centerY = rightBrakeLightPosition.y
            radius = imageView.fitWidth / 4.0
            fill = BRAKE_COLOR
        }
        // endregion

        // region Blinkers
        val leftBlinkerPosition = getLeftBlinkerLightPosition()
        val rightBlinkerPosition = getRightBlinkerLightPosition()
        leftFrontBlinker = circle {
            centerX = leftBlinkerPosition.x
            centerY = leftBlinkerPosition.y
            radius = imageView.fitWidth / 4
            fill = BLINKER_COLOR
        }
        rightFrontBlinker = circle {
            centerX = rightBlinkerPosition.x
            centerY = rightBlinkerPosition.y
            radius = imageView.fitWidth / 4
            fill = BLINKER_COLOR
        }
        // endregion
    }

    fun update(vehicle: Vehicle) {
        imageView.x = vehicle.position.x - imageView.fitWidth / 2.0
        imageView.y = vehicle.position.y - imageView.fitHeight / 2.0
        imageView.rotate = Math.toDegrees(atan2(vehicle.direction.y, vehicle.direction.x))

        // region Brake lights
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
        // endregion

        // region Blinkers
        val leftBlinkerPosition = getLeftBlinkerLightPosition()
        leftFrontBlinker.centerX = leftBlinkerPosition.x
        leftFrontBlinker.centerY = leftBlinkerPosition.y

        val rightBlinkerPosition = getRightBlinkerLightPosition()
        rightFrontBlinker.centerX = rightBlinkerPosition.x
        rightFrontBlinker.centerY = rightBlinkerPosition.y

        if(Calendar.getInstance().timeInMillis % 1000 > 500) {
            if(vehicle.lights.leftBlinker == LightState.BLINKING) {
                leftFrontBlinker.fill = BLINKER_COLOR
            }
            if(vehicle.lights.rightBlinker == LightState.BLINKING) {
                rightFrontBlinker.fill = BLINKER_COLOR
            }
        } else {
            leftFrontBlinker.fill = Color.TRANSPARENT
            rightFrontBlinker.fill = Color.TRANSPARENT
        }

        // endregion

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

    private fun getLeftBlinkerLightPosition() =
        Vector2D(
            vehicle.position.x + imageView.fitWidth / 2.0 * cos(vehicle.yaw) + sin(vehicle.yaw) * imageView.fitHeight / 3.0,
            vehicle.position.y + imageView.fitWidth / 2.0 * sin(vehicle.yaw) + cos(vehicle.yaw) * imageView.fitHeight / 3.0)

    private fun getRightBlinkerLightPosition() =
        Vector2D(
            vehicle.position.x + imageView.fitWidth / 2.0 * cos(vehicle.yaw) - sin(vehicle.yaw) * imageView.fitHeight / 3.0,
            vehicle.position.y + imageView.fitWidth / 2.0 * sin(vehicle.yaw) - cos(vehicle.yaw) * imageView.fitHeight / 3.0)
}

fun Parent.vehicleView(vehicle: Vehicle, op: VehicleView.() -> Unit = {}): VehicleView =
    opcr(this, VehicleView(vehicle), op)
