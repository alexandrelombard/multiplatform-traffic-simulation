package fr.ciadlab.sim.infrastructure.view.simulation

import fr.ciadlab.sim.traffic.ExitArea
import fr.ciadlab.sim.traffic.Spawner
import javafx.scene.Parent
import javafx.scene.paint.Color
import tornadofx.circle

class ExitAreaView (val exitArea: ExitArea)

fun Parent.exitAreaView(exitArea: ExitArea, op : ExitAreaView.() -> Unit = {}): Parent {
    circle {
        centerX = exitArea.position.x
        centerY = exitArea.position.y
        radius = exitArea.radius
        fill = Color.TRANSPARENT
        strokeWidth = 1.0
        stroke = Color.BLACK
    }
    return this
}
