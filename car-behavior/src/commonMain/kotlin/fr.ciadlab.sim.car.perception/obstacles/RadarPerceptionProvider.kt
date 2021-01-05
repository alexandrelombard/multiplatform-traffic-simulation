package fr.ciadlab.sim.car.perception.obstacles

import fr.ciadlab.sim.math.geometry.Vector2D
import fr.ciadlab.sim.physics.Units.Degrees
import fr.ciadlab.sim.physics.unit
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.math.abs

/**
 * Simple radar perception provider, not relying on any optimization. It can be improved using a prior filtering
 * on the list of vehicles being fed to the main function
 * @author Alexandre Lombard
 */
class RadarPerceptionProvider(
    val range: Double = 150.0,
    val fieldOfView: Double = 45.0 unit Degrees) {

    fun performRadarDetection(sourcePosition: Vector2D, direction: Vector2D, vehicles: List<Vehicle>): List<Vehicle> {
        return vehicles.filter {
            // Filter according to the distance
            (it.position - sourcePosition).norm < range
        }.filter {
            // Filter according to the field of view
            abs((it.position - sourcePosition).angle(direction)) < fieldOfView
        }
    }

}
