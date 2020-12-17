package fr.ciadlab.sim.car.perception

import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.infrastructure.RoadNetwork
import fr.ciadlab.sim.math.geometry.Vector2D
import fr.ciadlab.sim.math.geometry.Vector3D
import fr.ciadlab.sim.math.geometry.project
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.tan

/**
 * Class providing the perception of the road network and of the other vehicles on the given road network.
 * @author Alexandre Lombard
 */
class RoadNetworkPerceptionProvider {

    private val networkGridSizeX = 5
    private val networkGridSizeY = 5

    private val vehiclesGridSizeX = 10
    private val vehiclesGridSizeY = 10

    private val networkBounds: AxisAlignedBoundingBox
    private val networkGrid: Grid2D<List<Road>>

    private val vehiclesGrid: Grid2D<List<Vehicle>>

    val roadNetwork: RoadNetwork
    val vehicles: List<Vehicle>

    /**
     * Constructor which uses the optimized data structure of an older perception provider.
     * This should only be used if the road network is static (i.e. roads are not updated)
     */
    constructor(roadNetworkPerceptionProvider: RoadNetworkPerceptionProvider, vehicles: List<Vehicle>) {
        this.roadNetwork = roadNetworkPerceptionProvider.roadNetwork
        this.vehicles = vehicles

        this.networkBounds = roadNetworkPerceptionProvider.networkBounds
        this.networkGrid = roadNetworkPerceptionProvider.networkGrid
    }

    /**
     * Constructor which will internally initializes all required data structure
     * to optimize the research on the road network
     */
    constructor(roadNetwork: RoadNetwork, vehicles: List<Vehicle>) {
        this.roadNetwork = roadNetwork
        this.vehicles = vehicles

        // Compute the bounds of the network
        var minX: Double = Double.MAX_VALUE
        var minY: Double = Double.MAX_VALUE
        var maxX: Double = Double.MIN_VALUE
        var maxY: Double = Double.MIN_VALUE
        roadNetwork.roads.flatMap { it.points }.forEach {
            if(it.x < minX) minX = it.x
            if(it.y < minY) minY = it.y
            if(it.x > maxX) maxX = it.x
            if(it.y > maxY) maxY = it.y
        }
        this.networkBounds = AxisAlignedBoundingBox(minX, minY, maxX, maxY)

        // Generate a grid covering the road network, the roads will be stored in the cells of the grid
        this.networkGrid = Grid2D(networkGridSizeX, networkGridSizeY) { x, y ->
            val cellBounds = AxisAlignedBoundingBox(
                x * (networkBounds.width / networkGridSizeX),
                y * (networkBounds.height / networkGridSizeY),
                (x + 1) * (networkBounds.width / networkGridSizeX),
                (y + 1) * (networkBounds.height / networkGridSizeY))

            roadNetwork.roads.filter { it.points.any { cellBounds.contains(it.x, it.y) } }
        }
    }

    init {
        // Generate a grid of vehicles
        this.vehiclesGrid = generateVehiclesGrid()
    }

    /**
     * Computes the vehicles perceived by the given one
     * @param vehicle the perceiving vehicle
     * @return the perceived vehicles (not including self)
     */
    fun computePerceivedVehicles(vehicle: Vehicle): List<Vehicle> {
//        // Compute the position of the vehicle in the network grid and in the vehicle grid
//        val networkGridPosition = networkGridPosition(vehicle.position)
//        val vehicleGridPosition = vehiclesGridPosition(vehicle.position)
//
//        // Compute the position of the vehicle on the network
//        val candidateRoads = networkGrid[networkGridPosition.x, networkGridPosition.y]  // This phase will speed-up the look up but isn't sure
//        val closestRoad = getClosestRoad(vehicle.position, candidateRoads)
//
//        // Finding candidates for perceived vehicles
//        val perceivedCells = getVehiclesFrustumCells(vehicle.position, vehicle.direction, PI / 6.0, 100.0)
//        val perceivedVehicles = perceivedCells.flatMap { vehiclesGrid[it.x, it.y] }
//
//        return perceivedVehicles

        return this.vehicles    // FIXME Not optimized version
    }

    /**
     * Gets the closest road relative to a given 2D position
     * @param v the position
     * @param candidateRoads a list of candidate roads
     * @return the closest road to the point v in the list of candidate roads
     */
    private fun getClosestRoad(v: Vector2D, candidateRoads: List<Road>): Road {
        var minDistance = Double.MAX_VALUE
        var closestRoad: Road = candidateRoads[0]
        candidateRoads.forEach {
            val projectionData = it.points.project(Vector3D(v.x, v.y, 0.0))
            if(projectionData.distance < minDistance) {
                minDistance = projectionData.distance
                closestRoad = it
            }
        }
        return closestRoad
    }

    /**
     * Organize the vehicles in the grid (the bounds of the vehicle grid are the same as the bounds of the
     * network grid)
     */
    private fun generateVehiclesGrid(): Grid2D<List<Vehicle>> {
        val grid = Grid2D(vehiclesGridSizeX, vehiclesGridSizeY) { _, _ -> arrayListOf<Vehicle>() }
        vehicles.forEach {
            val (x, y) = vehiclesGridPosition(it.position)
            grid[x, y].add(it)
        }
        // This last line is only for the conversion of a mutable list to an immutable list
        return Grid2D(vehiclesGridSizeX, vehiclesGridSizeY) { x, y -> grid[x, y]}
    }

    // region Grid positions
    private data class GridPosition(val x: Int, val y: Int)
    private fun networkGridPosition(v: Vector2D) =
        GridPosition(
            min(networkGridSizeX - 1, max(0, ((v.x - this.networkBounds.minX) / this.networkBounds.width * networkGridSizeX).toInt())),
            min(networkGridSizeY, max(0, ((v.y - this.networkBounds.minY) / this.networkBounds.height * networkGridSizeY).toInt())))
    private fun vehiclesGridPosition(v: Vector2D) =
        GridPosition(
            min(vehiclesGridSizeX - 1, max(0, ((v.x - this.networkBounds.minX) / this.networkBounds.width * vehiclesGridSizeX).toInt())),
            min(vehiclesGridSizeY, max(0, ((v.y - this.networkBounds.minY) / this.networkBounds.height * vehiclesGridSizeY).toInt())))
    private fun getVehiclesFrustumCells(p: Vector2D, direction: Vector2D, fov: Double, range: Double): Set<GridPosition> {
        val gridPositions = hashSetOf<GridPosition>()
        // Compute the limits of the frustum
        val normal = Vector2D(-direction.y, direction.x)
        val normalOffset = tan(fov / 2.0) * range
        val firstBound = p + direction * range + normal * normalOffset
        val secondBound = p + direction * range - normal * normalOffset
        // Compute the perceived cells
        val startCell = vehiclesGridPosition(p)
        val firstBoundCell = vehiclesGridPosition(firstBound)
        val secondBoundCell = vehiclesGridPosition(secondBound)
        // gridPositions.add()  // TODO
        return gridPositions
    }
    // endregion
}