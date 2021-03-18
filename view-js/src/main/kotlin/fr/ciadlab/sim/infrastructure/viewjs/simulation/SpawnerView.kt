package fr.ciadlab.sim.infrastructure.viewjs.simulation

import fr.ciadlab.sim.infrastructure.viewjs.canvas.Color
import fr.ciadlab.sim.infrastructure.viewjs.canvas.circle
import fr.ciadlab.sim.infrastructure.viewjs.canvas.line
import fr.ciadlab.sim.traffic.Spawner
import org.w3c.dom.CanvasRenderingContext2D

fun CanvasRenderingContext2D.spawnerView(spawner: Spawner<*>) {
    circle {
        centerX = spawner.position.x
        centerY = spawner.position.y
        fill = Color.BLACK
        radius = 5.0
    }

    line {
        startX = spawner.position.x
        startY = spawner.position.y
        endX = spawner.position.x + spawner.direction.x * 10.0
        endY = spawner.position.y + spawner.direction.y * 10.0
    }
}
