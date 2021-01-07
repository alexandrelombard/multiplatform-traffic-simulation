package fr.ciadlab.sim.infrastructure.view.simulation

import fr.ciadlab.sim.infrastructure.view.network.RoadNetworkView
import fr.ciadlab.sim.traffic.Spawner
import javafx.scene.Group
import javafx.scene.Parent
import tornadofx.circle
import tornadofx.line

class SpawnerView (val spawner: Spawner<*>)

fun Parent.spawnerView(spawner: Spawner<*>, op : SpawnerView.() -> Unit = {}): Parent {
    circle {
        centerX = spawner.position.x
        centerY = spawner.position.y
        radius = 5.0
    }

    line {
        startX = spawner.position.x
        startY = spawner.position.y
        endX = spawner.position.x + spawner.direction.x * 10.0
        endY = spawner.position.y + spawner.direction.y * 10.0
    }
    return this
}
