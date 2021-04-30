package fr.ciadlab.sim.infrastructure.viewjs.car

import fr.ciadlab.sim.infrastructure.viewjs.canvas.Color
import fr.ciadlab.sim.infrastructure.viewjs.canvas.circle
import fr.ciadlab.sim.infrastructure.viewjs.canvas.image
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.vehicle.LightState
import fr.ciadlab.sim.vehicle.Vehicle
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Image
import kotlin.js.Date
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private var carImage: Image? = null

private val imageBasePath = "./assets/js/mps/fr/ciadlab/sim/infrastructure/viewjs/car"
//private val imageBasePath = "./build/distributions/fr/ciadlab/sim/infrastructure/viewjs/car"

class VehicleView (var vehicle: Vehicle) {
    private val BRAKE_COLOR = Color.rgba(1.0, 0.0, 0.0, 0.5)
    private val BLINKER_COLOR = Color.rgba(1.0, 0.75, 0.0, 0.5)

    val fitWidth: Double
    val fitHeight: Double

    init {
        fitWidth = vehicle.length
        fitHeight = 0.5 * vehicle.length
    }

    fun update(vehicle: Vehicle) {
        this.vehicle = vehicle
    }

    fun draw(canvas: CanvasRenderingContext2D) {
        canvas.save()
        canvas.translate(vehicle.position.x, vehicle.position.y)
        canvas.rotate(vehicle.direction.alpha)
        val carLength = vehicle.length
        val carWidth = vehicle.length / 2.0
        canvas.drawImage(
            image,
            -carLength / 2.0,
            -carWidth / 2.0,
            carLength,
            carWidth)

        // region Brake lights
        val leftBrakeLightPosition = getLeftBrakeLightPosition()
        canvas.circle {
            centerX = leftBrakeLightPosition.x
            centerY = leftBrakeLightPosition.y
            radius = fitWidth / 4.0
            fill = if(vehicle.brakeLightOn) BRAKE_COLOR else Color.TRANSPARENT
            strokeWidth = 0.0
        }

        val rightBrakeLightPosition = getRightBrakeLightPosition()
        canvas.circle {
            centerX = rightBrakeLightPosition.x
            centerY = rightBrakeLightPosition.y
            radius = fitWidth / 4.0
            fill = if(vehicle.brakeLightOn) BRAKE_COLOR else Color.TRANSPARENT
            strokeWidth = 0.0
        }
        // endregion

        // region Blinkers
        val leftBlinkerPosition = getLeftBlinkerLightPosition()
        canvas.circle {
            centerX = leftBlinkerPosition.x
            centerY = leftBlinkerPosition.y
            radius = fitWidth / 4.0
            fill = if(vehicle.lights.leftBlinker == LightState.BLINKING && Date().getMilliseconds() % 1000 > 500) BLINKER_COLOR else Color.TRANSPARENT
            strokeWidth = 0.0
        }

        val rightBlinkerPosition = getRightBlinkerLightPosition()
        canvas.circle {
            centerX = rightBlinkerPosition.x
            centerY = rightBlinkerPosition.y
            radius = fitWidth / 4.0
            fill = if(vehicle.lights.rightBlinker == LightState.BLINKING && Date().getMilliseconds() % 1000 > 500) BLINKER_COLOR else Color.TRANSPARENT
            strokeWidth = 0.0
        }
        // endregion

        canvas.restore()
    }

    companion object {
        var image: Image = image("$imageBasePath/car_up_right.png")
    }

    private fun toDegrees(radian: Double) = radian / PI * 180.0

    private fun getLeftBrakeLightPosition() =
        Vector2D(
            - fitWidth / 2.0 * cos(vehicle.yaw) - sin(vehicle.yaw) * fitHeight / 3.0,
            - fitWidth / 2.0 * sin(vehicle.yaw) - cos(vehicle.yaw) * fitHeight / 3.0)

    private fun getRightBrakeLightPosition() =
        Vector2D(
            - fitWidth / 2.0 * cos(vehicle.yaw) + sin(vehicle.yaw) * fitHeight / 3.0,
            - fitWidth / 2.0 * sin(vehicle.yaw) + cos(vehicle.yaw) * fitHeight / 3.0)

    private fun getLeftBlinkerLightPosition() =
        Vector2D(
            fitWidth / 2.0 * cos(vehicle.yaw) + sin(vehicle.yaw) * fitHeight / 3.0,
            fitWidth / 2.0 * sin(vehicle.yaw) + cos(vehicle.yaw) * fitHeight / 3.0)

    private fun getRightBlinkerLightPosition() =
        Vector2D(
            fitWidth / 2.0 * cos(vehicle.yaw) - sin(vehicle.yaw) * fitHeight / 3.0,
            fitWidth / 2.0 * sin(vehicle.yaw) - cos(vehicle.yaw) * fitHeight / 3.0)
}

fun CanvasRenderingContext2D.vehicleView(vehicle: Vehicle): VehicleView {
    val vehicleView = VehicleView(vehicle)
    vehicleView.draw(this)
    return vehicleView
}
