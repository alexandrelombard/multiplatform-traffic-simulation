package fr.ciadlab.sim.infrastructure.view.network

import fr.ciadlab.sim.infrastructure.intersection.IntersectionTrafficLight
import fr.ciadlab.sim.infrastructure.intersection.IntersectionTrafficLights
import fr.ciadlab.sim.infrastructure.intersection.TrafficLightState
import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import tornadofx.circle
import tornadofx.group
import tornadofx.opcr

fun RoadNetworkView.trafficLightsView(intersectionTrafficLights: IntersectionTrafficLights) {
    Platform.runLater {
        intersectionTrafficLights.trafficLights.forEach {
            // TODO Platform.runLater is required but should not be
            trafficLightView(it)
        }
    }
}

class TrafficLightView(var trafficLight: IntersectionTrafficLight): Group() {
    private lateinit var topCircle: Circle
    private lateinit var middleCircle: Circle
    private lateinit var bottomCircle: Circle

    init {
        trafficLight.onUpdate += {
            trafficLight = it
            Platform.runLater {
                applyColor(trafficLight.state)
            }
        }
    }

    val trafficLightView = group {
        // TODO Improve positioning of the traffic light relatively to the intersection
        val offset = 5.0
        val referenceLaneConnector = trafficLight.laneConnectors.first()
        val reference = referenceLaneConnector.sourcePoint + referenceLaneConnector.sourceNormal * offset

        // Top circle
        topCircle = circle {
            centerX = reference.x
            centerY = reference.y
            radius = 1.0
        }

        // Middle circle
        middleCircle = circle {
            centerX = reference.x
            centerY = reference.y + 2.5
            radius = 1.0
        }

        // Bottom circle
        bottomCircle = circle {
            centerX = reference.x
            centerY = reference.y + 5.0
            radius = 1.0
        }

        applyColor(trafficLight.state)
    }

    private fun applyColor(state: TrafficLightState) {
        this.topCircle.fill = if(state == TrafficLightState.RED) Color.RED else Color.BLACK
        this.middleCircle.fill = if(state == TrafficLightState.YELLOW) Color.YELLOW else Color.BLACK
        this.bottomCircle.fill = if(state == TrafficLightState.GREEN) Color.GREEN else Color.BLACK
    }
}

fun Parent.trafficLightView(trafficLight: IntersectionTrafficLight, op: TrafficLightView.() -> Unit = {}): TrafficLightView =
    opcr(this, TrafficLightView(trafficLight), op)
