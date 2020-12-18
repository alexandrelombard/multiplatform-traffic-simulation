package fr.ciadlab.sim.infrastructure.view.simulation

import fr.ciadlab.sim.traffic.ExitArea
import fr.ciadlab.sim.traffic.Spawner
import javafx.scene.Parent
import tornadofx.circle

class ExitAreaView (val exitArea: ExitArea)

fun Parent.exitAreaView(exitArea: ExitArea, op : ExitAreaView.() -> Unit = {}) {
    circle {
        centerX = exitArea.position.x
        centerY = exitArea.position.y
        radius = 5.0
    }
}
