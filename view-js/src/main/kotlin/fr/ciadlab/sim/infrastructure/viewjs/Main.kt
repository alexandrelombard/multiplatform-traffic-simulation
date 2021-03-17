package fr.ciadlab.sim.infrastructure.viewjs

import fr.ciadlab.sim.car.behavior.DriverBehavioralState
import fr.ciadlab.sim.car.behavior.default.reachGoalBehavior
import fr.ciadlab.sim.infrastructure.IntersectionBuilder
import fr.ciadlab.sim.infrastructure.intersection
import fr.ciadlab.sim.infrastructure.road
import fr.ciadlab.sim.infrastructure.roadNetwork
import fr.ciadlab.sim.infrastructure.viewjs.canvas.Color
import fr.ciadlab.sim.infrastructure.viewjs.canvas.clear
import fr.ciadlab.sim.infrastructure.viewjs.canvas.context2D
import fr.ciadlab.sim.infrastructure.viewjs.car.vehicleView
import fr.ciadlab.sim.infrastructure.viewjs.controllers.LateralControlWebviewSimulationController
import fr.ciadlab.sim.infrastructure.viewjs.network.background
import fr.ciadlab.sim.infrastructure.viewjs.network.intersectionView
import fr.ciadlab.sim.infrastructure.viewjs.network.roadNetworkView
import fr.ciadlab.sim.infrastructure.viewjs.network.roadView
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.math.algebra.Vector3D
import fr.ciadlab.sim.math.geometry.hermiteSpline
import fr.ciadlab.sim.physics.Units.KilometersPerHour
import fr.ciadlab.sim.physics.Units.Milliseconds
import fr.ciadlab.sim.physics.unit
import fr.ciadlab.sim.vehicle.Vehicle
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

private var dragging: Boolean = false
private var dragOrigin = Pair(0.0, 0.0)
private var currentScaleFactor = 1.0

//fun main(args: Array<String>) {
//    loadSimViewJs(args)
//}

class Loader {
    fun loadSimViewJs2() {
        loadSimViewJs(arrayOf())
    }
}

fun main() {
    // Empty but necessary for export of all functions
    // Removing this empty main will prevent all other functions from being
    // in the final webpack bundle.
    // Required functions should be referred to in this main function.
    if(false) {
        loadSimViewJs(arrayOf())
        LateralControlWebviewSimulationController()
    }
}

fun generateBaseCanvas(args: Array<String>): HTMLCanvasElement {
    val canvasProvided = args.isNotEmpty()

    val canvas =
        if(!canvasProvided)
            document.createElement("canvas") as HTMLCanvasElement
        else
            document.getElementById(args[0]) as HTMLCanvasElement

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
            val deltaX = (it.pageX - previousX) / (2.0 * currentScaleFactor)
            val deltaY = (it.pageY - previousY) / (2.0 * currentScaleFactor)
            canvas.context2D().translate(deltaX , deltaY)
            previousX = it.pageX
            previousY = it.pageY
        }
    }

    if(!canvasProvided) {
        canvas.width  = window.innerWidth
        canvas.height = window.innerHeight

        window.onresize = {
            canvas.width  = window.innerWidth
            canvas.height = window.innerHeight
            it
        }

        document.body!!.appendChild(canvas)
    }

    return canvas
}

@JsName("loadSimViewJs")
fun loadSimViewJs(args: Array<String>) {
    val canvas = generateBaseCanvas(args)
    val context = canvas.getContext("2d") as CanvasRenderingContext2D

    val roadNetworkModel = roadNetwork {
        val road1 = road {
            points = listOf(
                Vector3D(0.0, 0.0, 0.0),
                Vector3D(200.0, 100.0, 0.0),
                Vector3D(400.0, 0.0, 0.0),
                Vector3D(600.0, 50.0, 0.0))
            oneWay = false
            forwardLanesCount = 3
            backwardLanesCount = 2
        }
        val road2 = road {
            points = listOf(
                Vector3D(650.0, 150.0, 0.0),
                Vector3D(650.0, 400.0, 0.0)
            )
            oneWay = false
            forwardLanesCount = 2
            backwardLanesCount = 2
        }
        val road3 = road {
            points = listOf(
                Vector3D(700.0, 50.0, 0.0),
                Vector3D(1000.0, 50.0, 0.0)
            )
            oneWay = false
            forwardLanesCount = 2
            backwardLanesCount = 2
        }

        intersection {
            withRoad(road1, IntersectionBuilder.ConnectedSide.DESTINATION)
            withRoad(road2, IntersectionBuilder.ConnectedSide.SOURCE)
            withRoad(road3, IntersectionBuilder.ConnectedSide.SOURCE)
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
                    Vector3D(-50.0, 50.0, 0.0),
                    steps = 30
                ).toTypedArray(),
                Vector3D(0.0, 100.0, 0.0),
                *hermiteSpline(
                    Vector3D(0.0, 100.0, 0.0),
                    Vector3D(-50.0, 50.0, 0.0),
                    Vector3D(-50.0, 50.0, 0.0),
                    Vector3D(0.0, -100.0, 0.0),
                    Vector3D(0.0, 0.0, 0.0),
                    Vector3D(50.0, 50.0, 0.0),
                    steps = 30
                ).toTypedArray())
            oneWay = true
            forwardLanesCount = 1
            backwardLanesCount = 0
        }
    }

    val driverBehavioralState =
        DriverBehavioralState(
            currentRoad = eightShapedRoadNetworkModel.roads[0],
            currentLaneIndex = 0,
            travelForward = true,
            maximumSpeed = 50.0 unit KilometersPerHour,
            goal = eightShapedRoadNetworkModel.roads[0].end(),
            leaders = arrayListOf())

    var vehicle =
        Vehicle(
            Vector2D(100.0, 100.0),
            Vector2D(50.0 unit KilometersPerHour, 0.0),
            0.0,
            Vector2D(1.0, 0.0),
            0.0,
            3.5,
            4.0)

    // Drawing loop
    window.setInterval({
        context.clear(canvas)
        roadNetworkView(eightShapedRoadNetworkModel, canvas) {
            background(Color.rgb(230, 230, 230))

            roadNetwork.roads.forEach { roadView(it) }
            roadNetwork.intersections.forEach { intersectionView(it) }
        }
        context.vehicleView(vehicle)
    }, 20)

    // Simulation loop
    window.setInterval({
//        vehicle = vehicle.update(0.0, 0.0, 0.01)
        val driverAction = vehicle.reachGoalBehavior(driverBehavioralState).apply(10.0 unit Milliseconds)
        vehicle = vehicle.update(driverAction.targetAcceleration, driverAction.targetWheelAngle, 10.0 unit Milliseconds)
    }, 10)
}
