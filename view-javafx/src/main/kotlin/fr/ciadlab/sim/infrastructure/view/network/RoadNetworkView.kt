package fr.ciadlab.sim.infrastructure.view.network

import fr.ciadlab.sim.infrastructure.RoadNetwork
import javafx.scene.Group
import javafx.scene.Parent
import tornadofx.opcr

class RoadNetworkView(var roadNetwork: RoadNetwork, var laneWidth: Double = 3.5) : Group()

fun Parent.roadNetworkView(roadNetwork: RoadNetwork, op : RoadNetworkView.() -> Unit = {}) =
        opcr(this, RoadNetworkView(roadNetwork), op)
