package fr.ciadlab.sim.infrastructure.viewjs.controllers

import fr.ciadlab.sim.car.behavior.DriverBehavioralAction
import fr.ciadlab.sim.car.behavior.DriverBehavioralState
import fr.ciadlab.sim.car.behavior.reachGoalBehavior
import fr.ciadlab.sim.infrastructure.viewjs.canvas.Color
import fr.ciadlab.sim.infrastructure.viewjs.canvas.clear
import fr.ciadlab.sim.infrastructure.viewjs.canvas.context2D
import fr.ciadlab.sim.infrastructure.viewjs.canvas.line
import fr.ciadlab.sim.infrastructure.viewjs.car.carView
import fr.ciadlab.sim.infrastructure.viewjs.network.background
import fr.ciadlab.sim.infrastructure.viewjs.network.intersectionView
import fr.ciadlab.sim.infrastructure.viewjs.network.road
import fr.ciadlab.sim.infrastructure.viewjs.network.roadNetworkView
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.physics.Units
import fr.ciadlab.sim.physics.Units.Milliseconds
import fr.ciadlab.sim.physics.unit
import fr.ciadlab.sim.vehicle.Vehicle
import fr.ciadlab.sim.vehicle.withSimulatedDirectionError
import fr.ciadlab.sim.vehicle.withSimulatedPositionError
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

enum class LongitudinalControlModel {
    /** IDM */
    INTELLIGENT_DRIVER_MODEL,
    /** RT_ACC */
    RT_ACC
}

/**
 * Simulation controller for the webview of the simulations at http://alexandrelombard.github.io
 * @author Alexandre Lombard
 */
class LongitudinalControlWebviewSimulationController {
    private val simulationStepTime = 10.0 unit Milliseconds

    private var currentScaleFactor = 1.0

    var onStatsReceived: ((Double, String, Double, Double) -> Unit)? = null

    // region Parameters
    var longitudinalControlModel = LongitudinalControlModel.INTELLIGENT_DRIVER_MODEL
    var simulatedPositionError = false
    var simulatedPositionErrorRadius = 0.1
    var simulatedDirectionError = false
    var simulatedDirectionErrorRange = 0.1
    var simulatedLatency = false
    var simulatedLatencyDelay = 400.0 unit Milliseconds
    var customCommand: ((vehicle: Vehicle, driverBehavioralState: DriverBehavioralState) -> DriverBehavioralAction)? = null
    // endregion

    private var lastCommandTime = 0.0

    /**
     * Loads the simulation view in the given canvas
     */
    @JsName("load")
    fun load(canvasId: String) {
        val canvas =
                document.getElementById(canvasId) as HTMLCanvasElement

        val context = canvas.getContext("2d") as CanvasRenderingContext2D

        canvas.onwheel = {
            val scaleFactor = if (it.deltaY < 0.0) 1.1 else 0.9
            currentScaleFactor *= scaleFactor
            canvas.context2D().scale(scaleFactor, scaleFactor)
            it.preventDefault()
        }

        var drag = false
        var previousX = 0.0
        var previousY = 0.0
        canvas.onmousedown = {
            drag = true
            previousX = it.pageX
            previousY = it.pageY
            it
        }

        canvas.onmouseup = {
            drag = false
            it
        }

        canvas.onmousemove = {
            if(drag) {
                val deltaX = (it.pageX - previousX) / currentScaleFactor
                val deltaY = (it.pageY - previousY) / currentScaleFactor
                canvas.context2D().translate(deltaX, deltaY)
                previousX = it.pageX
                previousY = it.pageY
            }
        }

        canvas.context2D().translate((canvas.width - 200.0) / 2.0, (canvas.height - 200.0) / 2.0)
        canvas.context2D().scale(2.5, 2.5)

        val network = circleShapedRoadNetworkModel

        val driverBehavioralState =
            DriverBehavioralState(
                currentRoad = network.roads[0],
                currentLaneIndex = 0,
                travelForward = true,
                maximumSpeed = 50.0 unit Units.KilometersPerHour,
                leaders = arrayListOf(),
                goal = network.roads[0].end())

        var vehicles = (0..10).map {
            Vehicle(
                Vector2D(100.0 + 10.0 * it, 100.0),
                Vector2D(50.0 unit Units.KilometersPerHour, 0.0),
                0.0,
                Vector2D(1.0, 0.0),
                0.0,
                3.5,
                4.0)
        }.toList()

        // Drawing loop
        window.setInterval({
            context.clear(canvas)
            roadNetworkView(network, canvas) {
                background(Color.rgb(230, 230, 230))

                roadNetwork.roads.forEach { road(it) }
                roadNetwork.intersections.forEach { intersectionView(it) }
            }

            vehicles.forEach {
                val vehicle = it

                context.line {
                    startX = vehicle.position.x
                    startY = vehicle.position.y
                    endX = vehicle.position.x + vehicle.direction.x * 10.0
                    endY = vehicle.position.y + vehicle.direction.y * 10.0
                    strokeStyle = Color.RED
                }

                context.carView(vehicle)
            }
        }, 20)

        // Simulation loop
        var stepCount = 0
        window.setInterval({
            // Compute statistics and propagate them
            val statsHandler = onStatsReceived
            if(statsHandler != null) {
                // TODO
            }

            // Update model

                // Generating the perceptions
            val actions = vehicles.map {
                var perceivedVehicle = it
                if(simulatedPositionError) perceivedVehicle = perceivedVehicle.withSimulatedPositionError(simulatedPositionErrorRadius)
                if(simulatedDirectionError) perceivedVehicle = perceivedVehicle.withSimulatedDirectionError(simulatedDirectionErrorRange)

                // Computing the action
                val currentCustomCommand = customCommand
                val driverAction =
                    if(currentCustomCommand != null)
                        currentCustomCommand.invoke(perceivedVehicle, driverBehavioralState)
                    else
                        when(longitudinalControlModel) {
                            LongitudinalControlModel.INTELLIGENT_DRIVER_MODEL ->
                                perceivedVehicle
                                    .reachGoalBehavior(driverBehavioralState, longitudinalControl = {a, b -> 0.0}) // FIXME
                                    .apply(simulationStepTime)
                            else ->
                                perceivedVehicle
                                    .reachGoalBehavior(driverBehavioralState, longitudinalControl = {a, b -> 0.0})  // FIXME
                                    .apply(simulationStepTime)
                        }
                driverAction
            }

            vehicles = vehicles.mapIndexed { index, vehicle ->
                // Action applied immediately
                vehicle.update(actions[index].targetAcceleration, actions[index].targetWheelAngle, 10.0 unit Milliseconds)
            }.toList()

            this.lastCommandTime = stepCount * simulationStepTime

            // Update the step count
            stepCount++
        }, 10)
    }
}
