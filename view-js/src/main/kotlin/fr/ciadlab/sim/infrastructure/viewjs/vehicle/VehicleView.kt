package fr.ciadlab.sim.infrastructure.viewjs.car

import fr.ciadlab.sim.infrastructure.viewjs.canvas.image
import fr.ciadlab.sim.infrastructure.viewjs.canvas.line
import fr.ciadlab.sim.vehicle.Vehicle
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Image
import kotlin.math.PI

private var carImage: Image? = null

private val imageBasePath = "assets/js/sim-view-js"

class CarView (carImage: Image) {

}

fun CanvasRenderingContext2D.carView(
    car: Vehicle) {
    if(carImage == null) {
        carImage = image("$imageBasePath/fr/ciadlab/sim/infrastructure/viewjs/car/car_up_right.png")
    }

    val safeCarImage = carImage
    if(safeCarImage != null) {
        this.save()
        this.translate(car.position.x, car.position.y)
        this.rotate(car.direction.alpha)
        val carLength = car.length
        val carWidth = car.length / 2.0
        this.drawImage(
            safeCarImage,
            -carLength / 2.0,
            -carWidth / 2.0,
            carLength,
            carWidth)
        this.restore()
    }
}