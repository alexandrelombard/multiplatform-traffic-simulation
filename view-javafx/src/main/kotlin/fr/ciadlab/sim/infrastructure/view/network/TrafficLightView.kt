package fr.ciadlab.sim.infrastructure.view.network

import fr.ciadlab.sim.infrastructure.intersection.IntersectionTrafficLight
import fr.ciadlab.sim.infrastructure.intersection.IntersectionTrafficLights
import fr.ciadlab.sim.infrastructure.intersection.TrafficLightState
import fr.ciadlab.sim.infrastructure.view.vehicle.VehicleView
import fr.ciadlab.sim.vehicle.Vehicle
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.paint.Color
import tornadofx.Vector2D
import tornadofx.circle
import tornadofx.group
import tornadofx.opcr

fun RoadNetworkView.trafficLightsView(intersectionTrafficLights: IntersectionTrafficLights) {
    intersectionTrafficLights.trafficLights.forEach {
        // TODO Manage the state using the simulation time
        trafficLightView(it, intersectionTrafficLights.policy.currentState(it.laneConnectors.first(), 0.0))
    }
}

class TrafficLightView(val trafficLight: IntersectionTrafficLight, var state: TrafficLightState): Group() {
    val trafficLightView = group {
        // TODO Improve positioning of the traffic light relatively to the intersection
        val offset = 5.0
        val referenceLaneConnector = trafficLight.laneConnectors.first()
        val reference = referenceLaneConnector.sourcePoint + referenceLaneConnector.sourceNormal * offset

        // Top circle
        circle {
            centerX = reference.x
            centerY = reference.y
            radius = 1.0
            fill = if(state == TrafficLightState.RED) Color.RED else Color.BLACK
        }

        // Middle circle
        circle {
            centerX = reference.x
            centerY = reference.y + 2.5
            radius = 1.0
            fill = if(state == TrafficLightState.YELLOW) Color.YELLOW else Color.BLACK
        }

        // Bottom circle
        circle {
            centerX = reference.x
            centerY = reference.y + 5.0
            radius = 1.0
            fill = if(state == TrafficLightState.GREEN) Color.GREEN else Color.BLACK
        }
    }
}

//fun RoadNetworkView.trafficLightView(trafficLight: IntersectionTrafficLight, state: TrafficLightState) {
//    // TODO Improve positioning of the traffic light relatively to the intersection
//    val offset = 5.0
//    val referenceLaneConnector = trafficLight.laneConnectors.first()
//    val reference = referenceLaneConnector.sourcePoint + referenceLaneConnector.sourceNormal * offset
//
//    // Top circle
//    circle {
//        centerX = reference.x
//        centerY = reference.y
//        radius = 1.0
//        fill = if(state == TrafficLightState.RED) Color.RED else Color.BLACK
//    }
//
//    // Middle circle
//    circle {
//        centerX = reference.x
//        centerY = reference.y + 2.5
//        radius = 1.0
//        fill = if(state == TrafficLightState.YELLOW) Color.YELLOW else Color.BLACK
//    }
//
//    // Bottom circle
//    circle {
//        centerX = reference.x
//        centerY = reference.y + 5.0
//        radius = 1.0
//        fill = if(state == TrafficLightState.GREEN) Color.GREEN else Color.BLACK
//    }
//}

fun Parent.trafficLightView(trafficLight: IntersectionTrafficLight, state: TrafficLightState, op: TrafficLightView.() -> Unit = {}): TrafficLightView =
    opcr(this, TrafficLightView(trafficLight, state), op)
