package fr.ciadlab.sim.infrastructure.viewjs.controllers

import fr.ciadlab.sim.infrastructure.viewjs.canvas.clear
import fr.ciadlab.sim.infrastructure.viewjs.generateBaseCanvas
import fr.ciadlab.sim.infrastructure.viewjs.simulation.trafficSimulationView
import fr.ciadlab.sim.physics.Units
import fr.ciadlab.sim.physics.unit
import fr.ciadlab.sim.traffic.scenario.TwoIntersections2LanesWithTrafficLights
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D

class TwoIntersectionsWebviewSimulationController {
    private val simulationStepTime = 10.0 unit Units.Milliseconds

    // region Parameters
    // endregion

    /**
     * Loads the simulation view in the given canvas
     */
    @JsName("load")
    fun load(canvasId: String) {
        val canvas = generateBaseCanvas(arrayOf(canvasId))
        val context = canvas.getContext("2d") as CanvasRenderingContext2D

        val roadNetworkModel = TwoIntersections2LanesWithTrafficLights.network
        val simulation = TwoIntersections2LanesWithTrafficLights.simulation

        // Drawing loop
        window.setInterval({
            context.clear(canvas)

            context.trafficSimulationView(simulation)
        }, 20)

        // Simulation loop
        window.setInterval({
            simulation.step(simulationStepTime)
        }, 10)
    }
}
