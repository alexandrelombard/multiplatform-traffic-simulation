package fr.ciadlab.sim.infrastructure.view

import fr.ciadlab.sim.car.behavior.DriverBehavioralState
import fr.ciadlab.sim.car.behavior.lateral.lombardLateralControl
import fr.ciadlab.sim.car.behavior.lateral.purePursuit
import fr.ciadlab.sim.car.behavior.lateral.stanleyLateralControl
import fr.ciadlab.sim.car.behavior.reachGoalBehavior
import fr.ciadlab.sim.infrastructure.*
import fr.ciadlab.sim.infrastructure.IntersectionBuilder.ConnectedSide
import fr.ciadlab.sim.infrastructure.view.network.intersectionView
import fr.ciadlab.sim.infrastructure.view.network.roadNetworkView
import fr.ciadlab.sim.infrastructure.view.network.roadView
import fr.ciadlab.sim.infrastructure.view.simulation.spawnerView
import fr.ciadlab.sim.infrastructure.view.simulation.trafficSimulationView
import fr.ciadlab.sim.infrastructure.view.vehicle.vehicleView
import fr.ciadlab.sim.math.geometry.*
import fr.ciadlab.sim.physics.Units.KilometersPerHour
import fr.ciadlab.sim.physics.Units.Milliseconds
import fr.ciadlab.sim.physics.unit
import fr.ciadlab.sim.traffic.Spawner
import fr.ciadlab.sim.traffic.exitArea
import fr.ciadlab.sim.traffic.spawner
import fr.ciadlab.sim.traffic.trafficSimulation
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

    val simpleIntersectionTrafficSimulation = trafficSimulation {
        roadNetwork = simpleIntersectionRoadNetworkModel

        spawner<Vehicle> {
            position = Vector2D(0.0, 0.0)
            direction = Vector2D(1.0, 0.0)
            generation = {
                Vehicle(position, Vector2D(0.0, 0.0), 0.0, Vector2D(0.0, 0.0), 0.0, 5.0, 4.0)
            }
        }

        exitArea {
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

    private val driverBehavioralState =
        DriverBehavioralState(
            currentRoad = simpleIntersectionRoadNetworkModel.roads[0],
            currentLaneIndex = 0,
            maximumSpeed = 50.0 unit KilometersPerHour,
            goal = simpleIntersectionRoadNetworkModel.roads[0].end(), leaders = listOf())

    init {
        // Close when the main stage is closed
        this.primaryStage.setOnCloseRequest { Platform.exit(); exitProcess(0) }

        // Simulation run loop
        fixedRateTimer(period = 50) {
            val period = 50.0 unit Milliseconds
//            vehicle = vehicle.update(0.0, 0.01, 0.05)
            val action = vehicle.reachGoalBehavior(driverBehavioralState).apply(period)
            vehicle = vehicle.update(action.targetAcceleration, action.targetWheelAngle, period)
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
