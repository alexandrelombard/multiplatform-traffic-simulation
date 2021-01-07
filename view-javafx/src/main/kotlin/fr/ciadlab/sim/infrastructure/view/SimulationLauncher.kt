package fr.ciadlab.sim.infrastructure.view

import fr.ciadlab.sim.car.behavior.DriverBehavioralAction
import fr.ciadlab.sim.car.behavior.DriverBehavioralState
import fr.ciadlab.sim.car.behavior.reachGoalBehavior
import fr.ciadlab.sim.car.behavior.routing.OriginDestinationRouter
import fr.ciadlab.sim.car.perception.mapmatching.MapMatchingProvider
import fr.ciadlab.sim.car.perception.obstacles.RadarPerceptionProvider
import fr.ciadlab.sim.infrastructure.*
import fr.ciadlab.sim.infrastructure.IntersectionBuilder.ConnectedSide
import fr.ciadlab.sim.infrastructure.view.simulation.trafficSimulationView
import fr.ciadlab.sim.infrastructure.view.vehicle.vehicleView
import fr.ciadlab.sim.math.algebra.*
import fr.ciadlab.sim.math.geometry.*
import fr.ciadlab.sim.physics.Units.KilometersPerHour
import fr.ciadlab.sim.physics.Units.Milliseconds
import fr.ciadlab.sim.physics.unit
import fr.ciadlab.sim.traffic.*
import fr.ciadlab.sim.vehicle.Vehicle
import javafx.application.Platform
import javafx.event.EventHandler
import tornadofx.View
import tornadofx.stackpane
import tornadofx.group
import tornadofx.Stylesheet
import tornadofx.App
import tornadofx.launch
import kotlin.concurrent.fixedRateTimer
import kotlin.math.*
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
        val routes = hashMapOf<Vehicle, List<Road>?>()

        roadNetwork = simpleIntersectionRoadNetworkModel
        
        onSpawn.add { vehicle, _ ->
            // Compute a random route
            val router = OriginDestinationRouter(roadNetwork, MapMatchingProvider(roadNetwork))
            val route = router.findRoute(vehicle.position, this.exitAreas.random().position)
            routes[vehicle] = route
        }

        vehicleBehavior = { vehicle, deltaTime ->
            // Compute perceptions
            val radar = RadarPerceptionProvider()
            val radarData = radar.performRadarDetection(vehicle.position, vehicle.direction, this.vehicles)
            // Retrieve the computed route
            val route = routes[vehicle]
            val currentRoad = route?.minByOrNull { it.points.project(vehicle.position.toVector3D()).distance }
            // Execute the behavior
            val driverBehavioralState = DriverBehavioralState(
                currentRoad ?: roadNetworkModel.roads[0],
                0,      // FIXME
                listOf(),
                50.0 unit KilometersPerHour,
                route?.last()?.end() ?: roadNetworkModel.roads[0].end())

            println("${vehicle.direction}")

            vehicle.reachGoalBehavior(driverBehavioralState).apply(deltaTime)
        }

        vehicleUpdate = { vehicle, action, deltaTime ->
            vehicle.update(action.targetAcceleration, action.targetWheelAngle, deltaTime)
        }

//        spawner {
//            position = Vector2D(0.0, 0.0)
//            direction = Vector2D(1.0, 0.0)
//            generation = {
//                Vehicle(position, Vector2D(0.0, 0.0), 0.0, direction, 0.0, 3.8, 4.0)
//            }
//            strategy = { if(Random.nextFloat() < 0.01) { spawn() } }
//        }

        spawner {
            position = Vector2D(653.5, 400.0)
            direction = Vector2D(0.0, -1.0)
            generation = {
                Vehicle(position, Vector2D(0.0, 0.0), 0.0, Vector2D(0.0, -1.0), 0.0, 3.8, 4.0)
            }
            strategy = { if(Random.nextFloat() < 0.01) { spawn() } }
        }

        exitArea {
            radius = 5.0
            position = Vector2D(1000.0, 50.0)
        }
    }

    val eightShapedRoadNetworkModel = roadNetwork {
        val eightShapedRoad = road {
            points = listOf(
                Vector3D(0.0, 0.0, 0.0),
                Vector3D(100.0, 100.0, 0.0),
                *hermiteSpline(
                    Vector3D(100.0, 100.0, 0.0),
                    Vector3D(50.0, 50.0, 0.0),
                    Vector3D(150.0, 50.0, 0.0),
                    Vector3D(0.0, -100.0, 0.0),
                    Vector3D(100.0, 0.0, 0.0),
                    Vector3D(-50.0, 50.0, 0.0)
                ).toTypedArray(),
                Vector3D(0.0, 100.0, 0.0),
                *hermiteSpline(
                    Vector3D(0.0, 100.0, 0.0),
                    Vector3D(-50.0, 50.0, 0.0),
                    Vector3D(-50.0, 50.0, 0.0),
                    Vector3D(0.0, -100.0, 0.0),
                    Vector3D(0.0, 0.0, 0.0),
                    Vector3D(50.0, 50.0, 0.0)
                ).toTypedArray())
            oneWay = true
            forwardLanesCount = 1
            backwardLanesCount = 0
        }
    }

    private var dragging: Boolean = false
    private var dragOrigin = Pair(0.0, 0.0)

    private var vehicle =
        Vehicle(
            Vector2D(50.0, 50.0),
            Vector2D(50.0 unit KilometersPerHour, 0.0),
            0.0,
            Vector2D(1.0, 0.0),
            0.0, 3.5, 5.0)

    private val simulation = simpleIntersectionTrafficSimulation
    private val roadNetworkModel = simpleIntersectionRoadNetworkModel

    private val driverBehavioralState =
        DriverBehavioralState(
            currentRoad = roadNetworkModel.roads[0],
            currentLaneIndex = 0,
            maximumSpeed = 50.0 unit KilometersPerHour,
            goal = roadNetworkModel.roads[0].end(), leaders = listOf())

    init {
        // Close when the main stage is closed
        this.primaryStage.setOnCloseRequest { Platform.exit(); exitProcess(0) }

        // Simulation run loop
        fixedRateTimer(period = 50) {
            val period = 50.0 unit Milliseconds
//            vehicle = vehicle.update(0.0, 0.01, 0.05)
            val action = vehicle.reachGoalBehavior(driverBehavioralState).apply(period)
            vehicle = vehicle.update(action.targetAcceleration, action.targetWheelAngle, period)

            simulation.step(period)
        }
    }

    override val root = stackpane {
        scaleX = 5.0
        scaleY = 5.0

        group {
//            roadNetworkView(simpleIntersectionRoadNetworkModel) {
//                laneWidth = 3.5
//                roadNetwork.roads.forEach { roadView(it, debug = true) }
//                roadNetwork.intersections.forEach { intersectionView(it) }
//            }
            trafficSimulationView(simpleIntersectionTrafficSimulation) {
            }

            simpleIntersectionTrafficSimulation.vehicles.forEach {
                vehicleView(it)
            }

            vehicleView(vehicle)
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
