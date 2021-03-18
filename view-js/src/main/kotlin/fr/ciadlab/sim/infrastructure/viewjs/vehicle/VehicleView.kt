package fr.ciadlab.sim.infrastructure.viewjs.car

import fr.ciadlab.sim.infrastructure.viewjs.canvas.image
import fr.ciadlab.sim.vehicle.Vehicle
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Image
import kotlin.math.PI

private var carImage: Image? = null

//private val imageBasePath = "assets/js/sim-view-js"
private val imageBasePath = "./build/distributions/fr/ciadlab/sim/infrastructure/viewjs/car"

class VehicleView (var vehicle: Vehicle) {
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
        canvas.restore()
    }

    companion object {
        var image: Image = image("$imageBasePath/car_up_right.png")
    }

    private fun toDegrees(radian: Double) = radian / PI * 180.0
}

//fun CanvasRenderingContext2D.vehicleView(
//    car: Vehicle): VehicleView {
//    if(carImage == null) {
//        carImage = image("$imageBasePath/fr/ciadlab/sim/infrastructure/viewjs/car/car_up_right.png")
//    }
//
//    val safeCarImage = carImage
//    if(safeCarImage != null) {
//        this.save()
//        this.translate(car.position.x, car.position.y)
//        this.rotate(car.direction.alpha)
//        val carLength = car.length
//        val carWidth = car.length / 2.0
//        this.drawImage(
//            safeCarImage,
//            -carLength / 2.0,
//            -carWidth / 2.0,
//            carLength,
//            carWidth)
//        this.restore()
//    }
//}

fun CanvasRenderingContext2D.vehicleView(vehicle: Vehicle): VehicleView {
    val vehicleView = VehicleView(vehicle)
    vehicleView.draw(this)
    return vehicleView
}
