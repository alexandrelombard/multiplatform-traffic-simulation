package fr.ciadlab.sim.infrastructure.view.network

import fr.ciadlab.sim.infrastructure.LaneConnector
import fr.ciadlab.sim.infrastructure.intersection.TrafficLightIntersectionManager
import fr.ciadlab.sim.infrastructure.intersection.TrafficLightState
import javafx.scene.paint.Color
import tornadofx.circle

fun RoadNetworkView.trafficLights(trafficLightIntersectionManager: TrafficLightIntersectionManager) {
    trafficLightIntersectionManager.trafficLights.forEach {
        // TODO Manage joint states: i.e. traffic lights for different connectors sharing the same state
        trafficLight(it.key, it.value)
    }
}

fun RoadNetworkView.trafficLight(laneConnector: LaneConnector, state: TrafficLightState) {
    val offset = 10.0
    val reference = laneConnector.sourcePoint + laneConnector.sourceNormal * offset

    // Top circle
    circle {
        centerX = reference.x
        centerY = reference.y
        radius = 2.0
        fill = if(state == TrafficLightState.RED) Color.RED else Color.BLACK
    }

    // Middle circle
    circle {
        centerX = reference.x
        centerY = reference.y + 5.0
        radius = 2.0
        fill = if(state == TrafficLightState.YELLOW) Color.YELLOW else Color.BLACK
    }

    // Bottom circle
    circle {
        centerX = reference.x
        centerY = reference.y + 10.0
        radius = 2.0
        fill = if(state == TrafficLightState.GREEN) Color.GREEN else Color.BLACK
    }
}
