package fr.ciadlab.sim.infrastructure.viewjs.simulation

import fr.ciadlab.sim.infrastructure.viewjs.canvas.Color
import fr.ciadlab.sim.infrastructure.viewjs.canvas.circle
import fr.ciadlab.sim.traffic.ExitArea
import org.w3c.dom.CanvasRenderingContext2D

fun CanvasRenderingContext2D.exitAreaView(exitArea: ExitArea) {
    circle {
        centerX = exitArea.position.x
        centerY = exitArea.position.y
        radius = exitArea.radius
        fill = Color.TRANSPARENT
        strokeWidth = 1.0
        stroke = Color.BLACK
    }
}
