package fr.ciadlab.sim.infrastructure.view.vehicle

import fr.ciadlab.sim.vehicle.Vehicle
import javafx.scene.Parent
import javafx.scene.image.Image
import tornadofx.imageview
import kotlin.math.atan2

class VehicleView(val vehicle: Vehicle) {

}

fun Parent.vehicleView(vehicle: Vehicle): Parent {
    val imageView = imageview {
       image = Image("/car_up_right.png")

        x = vehicle.position.x
        y = vehicle.position.y
        fitWidth = vehicle.length
        fitHeight = 0.5 * vehicle.length
        rotate = Math.toDegrees(atan2(vehicle.direction.y, vehicle.direction.x))
    }

    vehicle.onUpdate += {
        imageView.x = it.position.x - imageView.fitWidth / 2.0
        imageView.y = it.position.y - imageView.fitHeight / 2.0
        imageView.rotate = Math.toDegrees(atan2(it.direction.y, it.direction.x))
    }

    return this
}
