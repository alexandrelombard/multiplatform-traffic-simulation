package fr.ciadlab.sim.infrastructure.view

import fr.ciadlab.sim.car.behavior.DriverBehavioralState
import fr.ciadlab.sim.car.behavior.default.reachGoalBehavior
import fr.ciadlab.sim.car.behavior.routing.OriginDestinationRouter
import fr.ciadlab.sim.car.perception.mapmatching.MapMatchingProvider
import fr.ciadlab.sim.car.perception.obstacles.RadarPerceptionProvider
import fr.ciadlab.sim.infrastructure.IntersectionBuilder.ConnectedSide
import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.infrastructure.intersection
import fr.ciadlab.sim.infrastructure.road
import fr.ciadlab.sim.infrastructure.roadNetwork
import fr.ciadlab.sim.infrastructure.view.simulation.trafficSimulationView
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.math.algebra.Vector3D
import fr.ciadlab.sim.math.algebra.project
import fr.ciadlab.sim.math.algebra.toVector3D
import fr.ciadlab.sim.math.geometry.hermiteSpline
import fr.ciadlab.sim.physics.Units.KilometersPerHour
import fr.ciadlab.sim.physics.Units.Milliseconds
import fr.ciadlab.sim.physics.unit
import fr.ciadlab.sim.traffic.exitArea
import fr.ciadlab.sim.traffic.scenario.HighwaySection2Lanes
import fr.ciadlab.sim.traffic.spawner
import fr.ciadlab.sim.traffic.trafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle
import javafx.application.Platform
import javafx.event.EventHandler
import tornadofx.*
import kotlin.collections.set
import kotlin.concurrent.fixedRateTimer
import kotlin.math.max
import kotlin.random.Random
import kotlin.system.exitProcess

class SimulationView : View() {
    val simpleIntersectionRoadNetworkModel = roadNetwork {
        val road1 = road {
            points =
                    hermiteSpline(
                        Vector3D(0.0, 0.0, 0.0),
                        Vector3D(100.0, 0.0, 0.0),
                        Vector3D(200.0, 100.0, 0.0),
                        Vector3D(100.0, 0.0, 0.0),
                        Vector3D(400.0, 0.0, 0.0),
                        Vector3D(100.0, 0.0, 0.0),
                        Vector3D(625.0, 50.0, 0.0),
                        Vector3D(100.0, 0.0, 0.0))
//                    listOf(
//                        Vector3D(0.0, 0.0, 0.0),
//                        Vector3D(200.0, 100.0, 0.0),
//                        Vector3D(400.0, 0.0, 0.0),
//                        Vector3D(600.0, 50.0, 0.0))
            oneWay = false
            forwardLanesCount = 3
            backwardLanesCount = 2
        }
        val road2 = road {
            points = listOf(
                Vector3D(650.0, 75.0, 0.0),
                Vector3D(650.0, 400.0, 0.0)
            )
            oneWay = false
            forwardLanesCount = 2
            backwardLanesCount = 2
        }
        val road3 = road {
            points = listOf(
                Vector3D(675.0, 50.0, 0.0),
                Vector3D(1000.0, 50.0, 0.0)
            )
            oneWay = false
            forwardLanesCount = 2
            backwardLanesCount = 2
        }

        intersection {
            withRoad(road1, ConnectedSide.DESTINATION)
            withRoad(road2, ConnectedSide.SOURCE)
            withRoad(road3, ConnectedSide.SOURCE)
        }
    }

    val simpleIntersectionTrafficSimulation = trafficSimulation<Vehicle> {
        /** Store the routes of the vehicles */
        val routes = hashMapOf<Vehicle, List<Pair<Road, Boolean>>?>()

        roadNetwork = simpleIntersectionRoadNetworkModel
        
        onSpawn.add { vehicle, _ ->
            // Compute a route from the current position to a random exit area
            val destination = this.exitAreas.random().position
            val router = OriginDestinationRouter(roadNetwork, MapMatchingProvider(roadNetwork))
            val route = router.findRoute(vehicle.position, destination)

            if(route != null) {
                // Define the directions of the roads of the route
                val directions = route.mapIndexed { index, road ->
                    if(index < route.size - 1) {
                        // If we are before the last, we are forward if the next road is at the end of the current
                        roadNetwork.isAtEnd(road, route[index + 1])
                    } else {
                        // If we are the last, we are forward if the destination is closer to the end of the road
                        destination.toVector3D().distance(road.end()) < destination.toVector3D().distance(road.begin())
                    }
                }

                // Store it
                routes[vehicle] = route.mapIndexed { index, road -> Pair(road, directions[index]) }
            }
        }

        vehicleBehavior = { vehicle, deltaTime ->
            // Compute perceptions
            val radar = RadarPerceptionProvider()
            val radarData = radar.performRadarDetection(vehicle.position, vehicle.direction, this.vehicles)
            // Retrieve the computed route and the current road
            val route = routes[vehicle]
            val currentRoad = route?.minByOrNull { it.first.points.project(vehicle.position.toVector3D()).distance }

            // Execute the behavior
            val driverBehavioralState = DriverBehavioralState(
                currentRoad?.first ?: roadNetworkModel.roads[0],
                0,      // FIXME
                currentRoad?.second ?: true,
                radarData,
                50.0 unit KilometersPerHour,
                route?.last()?.first?.end() ?: roadNetworkModel.roads[0].end())

             vehicle.reachGoalBehavior(driverBehavioralState).apply(deltaTime)
        }

        vehicleUpdate = { vehicle, action, deltaTime ->
            vehicle.update(action.targetAcceleration, action.targetWheelAngle, deltaTime)
        }

        spawner {
            position = Vector2D(0.0, 0.0)
            direction = Vector2D(1.0, 0.0)
            generation = {
                Vehicle(position, Vector2D(0.0, 0.0), 0.0, direction, 0.0, 3.8, 4.0)
            }
            strategy = { if(Random.nextFloat() < 0.01) { spawn() } }
        }

        spawner {
            position = Vector2D(653.5, 400.0)
            direction = Vector2D(0.0, -1.0)
            generation = {
                Vehicle(position, Vector2D(0.0, 0.0), 0.0, Vector2D(0.0, -1.0), 0.0, 3.8, 4.0)
            }
            strategy = { if(Random.nextFloat() < 0.01) { spawn() } }
        }

        exitArea {
            radius = 7.5
            position = Vector2D(1000.0, 50.0)
        }
    }

    private var dragging: Boolean = false
    private var dragOrigin = Pair(0.0, 0.0)

//    private val simulation = simpleIntersectionTrafficSimulation
//    private val roadNetworkModel = simpleIntersectionRoadNetworkModel
//    private val simulation = SimpleIntersection2LanesWithTrafficLights.simulation
//    private val roadNetworkModel = SimpleIntersection2LanesWithTrafficLights.network
//    private val simulation = SimpleIntersection2LanesWithV2X.simulation
//    private val roadNetworkModel = SimpleIntersection2LanesWithV2X.network
//    private val simulation = TwoIntersections2LanesWithTrafficLights.simulation
//    private val roadNetworkModel = TwoIntersections2LanesWithTrafficLights.network
//    private val simulation = TwoIntersections2LanesWithV2X.simulation
//    private val roadNetworkModel = TwoIntersections2LanesWithV2X.network
    private val roadNetworkModel = HighwaySection2Lanes.network
    private val simulation = HighwaySection2Lanes.simulation

    init {
        // Close when the main stage is closed
        this.primaryStage.setOnCloseRequest { Platform.exit(); exitProcess(0) }

        // Simulation run loop
        fixedRateTimer(period = 10) {
            val timeScale = 1.0
            val period = (10.0 unit Milliseconds) * timeScale

            simulation.step(period)
        }
    }

    override val root = stackpane {
        scaleX = 5.0
        scaleY = 5.0

        group {
            trafficSimulationView(simulation)
        }

        onMousePressed = EventHandler { dragOrigin = Pair(it.x, it.y) }
        onMouseReleased = EventHandler { dragOrigin = Pair(it.x, it.y) }
        onMouseDragged = EventHandler {
            this.translateX += it.x - dragOrigin.first
            this.translateY += it.y - dragOrigin.second
        }

        onScroll = EventHandler {
            val zoomFactor = if(it.deltaY > 0) 1.05 else 0.95
            this.scaleX = max(0.6, this.scaleX * zoomFactor)
            this.scaleY = max(0.6, this.scaleY * zoomFactor)
        }
    }
}

class Styles : Stylesheet()
class TestApp : App(SimulationView::class, Styles::class)

fun main(args: Array<String>) {
    launch<TestApp>(args)
}
