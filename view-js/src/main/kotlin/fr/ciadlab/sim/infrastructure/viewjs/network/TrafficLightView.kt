package fr.ciadlab.sim.infrastructure.viewjs.network

import fr.ciadlab.sim.infrastructure.intersection.IntersectionTrafficLight
import fr.ciadlab.sim.infrastructure.intersection.IntersectionTrafficLights
import fr.ciadlab.sim.infrastructure.intersection.TrafficLightState
import fr.ciadlab.sim.infrastructure.viewjs.canvas.Color
import fr.ciadlab.sim.infrastructure.viewjs.canvas.circle
import org.w3c.dom.CanvasRenderingContext2D

fun CanvasRenderingContext2D.trafficLightsView(intersectionTrafficLights: IntersectionTrafficLights) {
    intersectionTrafficLights.trafficLights.forEach {
        trafficLightView(it)
    }
}

fun CanvasRenderingContext2D.trafficLightView(trafficLight: IntersectionTrafficLight) {
    val offset = 5.0
    val state = trafficLight.state
    val referenceLaneConnector = trafficLight.laneConnectors.first()
    val reference = referenceLaneConnector.sourcePoint + referenceLaneConnector.sourceNormal * offset

    // Top circle
    circle {
        centerX = reference.x
        centerY = reference.y
        radius = 1.0
        strokeWidth = 0.5
        fill = if(state == TrafficLightState.RED) Color.RED else Color.BLACK
    }

    // Middle circle
    circle {
        centerX = reference.x
        centerY = reference.y + 2.5
        radius = 1.0
        strokeWidth = 0.5
        fill = if(state == TrafficLightState.YELLOW) Color.YELLOW else Color.BLACK
    }

    // Bottom circle
    circle {
        centerX = reference.x
        centerY = reference.y + 5.0
        radius = 1.0
        strokeWidth = 0.5
        fill = if(state == TrafficLightState.GREEN) Color.GREEN else Color.BLACK
    }
}
