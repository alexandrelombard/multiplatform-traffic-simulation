package fr.ciadlab.sim.infrastructure.viewjs.controllers

import fr.ciadlab.sim.car.behavior.DriverBehavioralAction
import fr.ciadlab.sim.car.behavior.DriverBehavioralState
import fr.ciadlab.sim.car.behavior.default.ReachGoalBehavior.Companion.constantSpeedControl
import fr.ciadlab.sim.car.behavior.default.ReachGoalBehavior.Companion.purePursuitLateralControl
import fr.ciadlab.sim.car.behavior.default.ReachGoalBehavior.Companion.stanleyLateralControl
import fr.ciadlab.sim.car.behavior.default.reachGoalBehavior
import fr.ciadlab.sim.infrastructure.offset
import fr.ciadlab.sim.infrastructure.viewjs.canvas.Color
import fr.ciadlab.sim.infrastructure.viewjs.canvas.clear
import fr.ciadlab.sim.infrastructure.viewjs.canvas.context2D
import fr.ciadlab.sim.infrastructure.viewjs.canvas.line
import fr.ciadlab.sim.infrastructure.viewjs.car.vehicleView
import fr.ciadlab.sim.infrastructure.viewjs.network.background
import fr.ciadlab.sim.infrastructure.viewjs.network.intersectionView
import fr.ciadlab.sim.infrastructure.viewjs.network.roadNetworkView
import fr.ciadlab.sim.infrastructure.viewjs.network.roadView
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.math.algebra.project
import fr.ciadlab.sim.math.algebra.toVector3D
import fr.ciadlab.sim.physics.Units
import fr.ciadlab.sim.physics.Units.Milliseconds
import fr.ciadlab.sim.physics.unit
import fr.ciadlab.sim.vehicle.Vehicle
import fr.ciadlab.sim.vehicle.withSimulatedDirectionError
import fr.ciadlab.sim.vehicle.withSimulatedPositionError
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlinx.browser.document
import kotlinx.browser.window

enum class LateralControlModel {
    /** Traditional pure-pursuit with look-ahead */
    PURE_PURSUIT,
    /** Stanley's command (without look-ahead, as in the article) */
    STANLEY,
    /** Curvature following from our article */
    CURVATURE_FOLLOWING
}

/**
 * Simulation controller for the webview of the simulations at http://alexandrelombard.github.io
 * @author Alexandre Lombard
 */
class LateralControlWebviewSimulationController {
    private val simulationStepTime = 10.0 unit Milliseconds

    private var currentScaleFactor = 1.0

    var onStatsReceived: ((Double, String, Double, Double) -> Unit)? = null

    // region Parameters
    var lateralControlModel = LateralControlModel.CURVATURE_FOLLOWING
    var simulatedPositionError = false
    var simulatedPositionErrorRadius = 0.1
    var simulatedDirectionError = false
    var simulatedDirectionErrorRange = 0.1
    var simulatedLatency = false
    var simulatedLatencyDelay = 400.0 unit Milliseconds
    var forcedSpeed: Double? = null
    var customCommand: ((vehicle: Vehicle, driverBehavioralState: DriverBehavioralState) -> DriverBehavioralAction)? = null
    // endregion

    private var lastCommand = DriverBehavioralAction(0.0, 0.0)
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

        val network = eightShapedRoadNetworkModel

        val driverBehavioralState =
            DriverBehavioralState(
                currentRoad = network.roads[0],
                currentLaneIndex = 0,
                travelForward = true,
                maximumSpeed = 50.0 unit Units.KilometersPerHour,
                leaders = arrayListOf(),
                goal = network.roads[0].end())

        var vehicle =
            Vehicle(
                Vector2D(100.0, 100.0),
                Vector2D(50.0 unit Units.KilometersPerHour, 0.0),
                0.0,
                Vector2D(1.0, 0.0),
                0.0,
                3.5,
                4.0)

        // Drawing loop
        window.setInterval({
            context.clear(canvas)
            roadNetworkView(network, canvas) {
                background(Color.rgb(230, 230, 230))

                roadNetwork.roads.forEach { roadView(it) }
                roadNetwork.intersections.forEach { intersectionView(it) }
            }

            context.line {
                startX = vehicle.position.x
                startY = vehicle.position.y
                endX = vehicle.position.x + vehicle.direction.x * 10.0
                endY = vehicle.position.y + vehicle.direction.y * 10.0
                strokeStyle = Color.RED
            }

            context.vehicleView(vehicle)
        }, 20)

        // Simulation loop
        var stepCount = 0
        window.setInterval({
            // Compute statistics and propagate them
            val statsHandler = onStatsReceived
            if(statsHandler != null) {
                // We get the lane
                val laneWidth = 3.5
                val laneOffset = driverBehavioralState.currentRoad.laneOffset(driverBehavioralState.currentLaneIndex)
                val lane = driverBehavioralState.currentRoad.points.offset(laneOffset * laneWidth)

                // We compute the parameters
                val frontAxlePosition = (vehicle.position + Vector2D(vehicle.wheelBase / 2.0, vehicle.direction)).toVector3D()
                val projectionData = lane.project(frontAxlePosition)
                val distance = projectionData.distance
                val polylineSegment = (projectionData.segmentEnd - projectionData.segmentBegin)
                val side = (projectionData.segmentEnd - projectionData.segmentBegin).xy.angle((frontAxlePosition - projectionData.segmentBegin).xy)
                val left = side > 0.0
                val angleError = polylineSegment.xy.angle(vehicle.direction)
                val lateralError = distance * if (left) 1 else -1

                val controlName = if(this.customCommand == null) this.lateralControlModel.name else "Custom command"
                statsHandler.invoke(stepCount * simulationStepTime, controlName, lateralError, angleError)
            }

            // Update model
                // Generating the perceptions
            var perceivedVehicle = vehicle
            if(simulatedPositionError) perceivedVehicle = perceivedVehicle.withSimulatedPositionError(simulatedPositionErrorRadius)
            if(simulatedDirectionError) perceivedVehicle = perceivedVehicle.withSimulatedDirectionError(simulatedDirectionErrorRange)

                // Computing the action
            val currentCustomCommand = customCommand
            val driverAction =
                if(currentCustomCommand != null)
                    currentCustomCommand.invoke(perceivedVehicle, driverBehavioralState)
                else
                    when(lateralControlModel) {
                        LateralControlModel.PURE_PURSUIT ->
                            perceivedVehicle
                                .reachGoalBehavior(
                                    driverBehavioralState,
                                    longitudinalControl = ::constantSpeedControl,
                                    lateralControl = ::purePursuitLateralControl)
                                .apply(simulationStepTime)
                        LateralControlModel.STANLEY ->
                            perceivedVehicle
                                .reachGoalBehavior(
                                    driverBehavioralState,
                                    longitudinalControl = :: constantSpeedControl,
                                    lateralControl = ::stanleyLateralControl)
                                .apply(simulationStepTime)
                        else ->
                            perceivedVehicle.
                                reachGoalBehavior(driverBehavioralState, longitudinalControl = :: constantSpeedControl)
                                .apply(simulationStepTime)
                    }

                // Update speed if required
            val speed = this.forcedSpeed
            if(speed != null) {
                vehicle = vehicle.changeSpeed(speed)
            }

                // Applying the action
            if(!simulatedLatency) {
                // Action applied immediately
                vehicle = vehicle.update(driverAction.targetAcceleration, driverAction.targetWheelAngle, 10.0 unit Milliseconds)

                this.lastCommand = driverAction
                this.lastCommandTime = stepCount * simulationStepTime
            } else {
                // Action applied with delay
                if(this.lastCommandTime + stepCount * simulationStepTime > this.simulatedLatencyDelay) {
                    vehicle = vehicle.update(this.lastCommand.targetAcceleration, this.lastCommand.targetWheelAngle, 10.0 unit Milliseconds)

                    this.lastCommand = driverAction
                    this.lastCommandTime = stepCount * simulationStepTime
                } else {
                    vehicle = vehicle.update(this.lastCommand.targetAcceleration, this.lastCommand.targetWheelAngle, 10.0 unit Milliseconds)
                }
            }

            // Update the step count
            stepCount++
        }, 10)
    }
}
