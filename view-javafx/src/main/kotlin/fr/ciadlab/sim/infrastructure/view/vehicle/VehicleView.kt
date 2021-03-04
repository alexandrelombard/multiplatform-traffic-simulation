package fr.ciadlab.sim.infrastructure.view.vehicle

import fr.ciadlab.sim.infrastructure.view.network.RoadNetworkView
import fr.ciadlab.sim.infrastructure.view.network.roadNetworkView
import fr.ciadlab.sim.infrastructure.view.simulation.SpawnerView
import fr.ciadlab.sim.vehicle.Vehicle
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import tornadofx.group
import tornadofx.imageview
import tornadofx.opcr
import kotlin.math.atan2

class VehicleView(var vehicle: Vehicle): Group() {
    val imageView: ImageView = imageview {
        image = Image("/car_up_right.png")

        x = vehicle.position.x
        y = vehicle.position.y
        fitWidth = vehicle.length
        fitHeight = 0.5 * vehicle.length
        rotate = Math.toDegrees(atan2(vehicle.direction.y, vehicle.direction.x))
    }

    fun update(vehicle: Vehicle) {
        imageView.x = vehicle.position.x - imageView.fitWidth / 2.0
        imageView.y = vehicle.position.y - imageView.fitHeight / 2.0
        imageView.rotate = Math.toDegrees(atan2(vehicle.direction.y, vehicle.direction.x))

        this.vehicle = vehicle
    }
}

fun Parent.vehicleView(vehicle: Vehicle, op: VehicleView.() -> Unit = {}): VehicleView =
    opcr(this, VehicleView(vehicle), op)
