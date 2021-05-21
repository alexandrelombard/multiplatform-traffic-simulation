package fr.ciadlab.sim.car.perception.obstacles

import fr.ciadlab.sim.car.behavior.DriverState
import fr.ciadlab.sim.math.algebra.AffineSpace2D
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.math.algebra.VectorSpace2D
import fr.ciadlab.sim.physics.Units.Degrees
import fr.ciadlab.sim.physics.unit
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.math.abs

/** Minimum distance to the obstacle */
internal const val EPSILON = 1e-3

/**
 * Simple radar perception provider, not relying on any optimization. It can be improved using a prior filtering
 * on the list of vehicles being fed to the main function
 * @author Alexandre Lombard
 */
class RadarPerceptionProvider(

    val range: Double = 150.0,
    val fieldOfView: Double = 45.0 unit Degrees) {

    /**
     * Performs the radar detection and returns the coordinates of obstacles relatively to the radar position and
     * orientation. The direction of the radar is the y-axis of the frame of the radar (right-handed frame).
     */
    fun performRadarDetection(sourcePosition: Vector2D, direction: Vector2D, vehicles: Collection<Vehicle>): List<ObstacleData> {
        return vehicles.filter {
            // Filter according to the distance
            val distance = (it.position - sourcePosition).norm
            distance > EPSILON && distance < range
        }.filter {
            // Filter according to the field of view
            abs((it.position - sourcePosition).angle(direction)) < fieldOfView
        }.map {
            // Convert to radar data
            val xAxis = Vector2D(direction.y, -direction.x).normalize()
            val vectorSpace = VectorSpace2D(xAxis)
            val affineSpace = AffineSpace2D(sourcePosition, xAxis)

            ObstacleData(affineSpace.fromDefault(it.position), vectorSpace.fromDefault(it.velocity))
        }
    }

    companion object {
        /**
         * Finds the leader among the perceived vehicles, in the given lane
         */
        fun findLeader(driverState: DriverState, vehicle: Vehicle, lane: Int): ObstacleData? {
            val perceivedLeaders = driverState.perceivedVehicles
                .filter { it.obstacleRelativePosition.y > 0.0 }         // Ignore vehicle behind
                .filter { abs(it.obstacleRelativePosition.x) < 10.0 }   // Ignore vehicle far in the lateral direction
            val laneLeaders = perceivedLeaders
                .filter {
                    driverState.currentRoad.findLane(vehicle.frame.toDefault(it.obstacleRelativePosition)) == lane
                }

            return laneLeaders.minByOrNull { it.obstacleRelativePosition.y }
        }

        /**
         * Finds the follower among the perceived vehicles, in the given lane
         */
        fun findFollower(driverState: DriverState, vehicle: Vehicle, lane: Int): ObstacleData? {
            val perceivedFollowers = driverState.perceivedVehicles
                .filter { it.obstacleRelativePosition.y < vehicle.length }  // Ignore vehicle before
                .filter { abs(it.obstacleRelativePosition.x) < 10.0 }       // Ignore vehicle far in the lateral direction
            val laneFollowers = perceivedFollowers
                .filter {
                    driverState.currentRoad.findLane(vehicle.frame.toDefault(it.obstacleRelativePosition)) == lane
                }

            return laneFollowers.maxByOrNull { it.obstacleRelativePosition.y }
        }
    }

}
