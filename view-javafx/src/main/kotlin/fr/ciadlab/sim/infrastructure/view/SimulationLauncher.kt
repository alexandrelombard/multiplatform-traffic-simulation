package fr.ciadlab.sim.infrastructure.view

import fr.ciadlab.sim.infrastructure.view.simulation.trafficSimulationView
import fr.ciadlab.sim.physics.Units.Milliseconds
import fr.ciadlab.sim.physics.unit
import fr.ciadlab.sim.traffic.scenario.HighwaySection2Lanes
import javafx.application.Platform
import javafx.event.EventHandler
import tornadofx.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.max
import kotlin.system.exitProcess

class SimulationView : View() {
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
//    private val roadNetworkModel = HighwaySectionSingleLane.network
//    private val simulation = HighwaySectionSingleLane.simulation

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
            trafficSimulationView(simulation) {
                debug = true
            }
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
