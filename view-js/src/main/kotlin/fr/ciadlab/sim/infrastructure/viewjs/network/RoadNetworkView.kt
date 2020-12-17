package fr.ciadlab.sim.infrastructure.viewjs.network

import fr.ciadlab.sim.infrastructure.RoadNetwork
import fr.ciadlab.sim.infrastructure.viewjs.canvas.Color
import fr.ciadlab.sim.infrastructure.viewjs.canvas.context2D
import org.w3c.dom.HTMLCanvasElement

class RoadNetworkView(
    var roadNetwork: RoadNetwork,
    val canvas: HTMLCanvasElement,
    var laneWidth: Double = 3.5,
    var backgroundColor: Color = Color.WHITE) {
}

fun roadNetworkView(
    roadNetwork: RoadNetwork,
    canvas: HTMLCanvasElement,
    op : RoadNetworkView.() -> Unit): RoadNetworkView {
    val view = RoadNetworkView(roadNetwork, canvas)
    val context = canvas.context2D()

    context.save()
    op.invoke(view)
    context.restore()

    return view
}

fun RoadNetworkView.background(color: String = this.backgroundColor.toString()) {
    // Draw background
    val context = canvas.context2D()
    context.save()
    context.fillStyle = color
    context.fillRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
    context.restore()
}

fun RoadNetworkView.background(color: Color = this.backgroundColor) {
    background(color.toString())
}