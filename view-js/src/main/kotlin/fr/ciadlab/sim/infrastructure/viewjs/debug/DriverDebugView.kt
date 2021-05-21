package fr.ciadlab.sim.infrastructure.view.debug

import fr.ciadlab.sim.car.behavior.DriverDebugData
import fr.ciadlab.sim.infrastructure.viewjs.canvas.Color
import fr.ciadlab.sim.infrastructure.viewjs.canvas.line
import org.w3c.dom.CanvasRenderingContext2D

class DriverDebugView(var debugData: DriverDebugData?) {

    fun update(debugData: DriverDebugData?) {
        this.debugData = debugData
    }

    /**
     * Draw the debug data display on screen
     */
    fun draw(canvas: CanvasRenderingContext2D) {
        val debugData = debugData
        if(debugData?.vehiclePosition != null) {
            canvas.save()

            if(debugData.leaderPosition != null) {
                canvas.line {
                    startX = debugData.vehiclePosition?.x ?: 0.0
                    startY = debugData.vehiclePosition?.y ?: 0.0
                    endX = debugData.leaderPosition?.x ?: 0.0
                    endY = debugData.leaderPosition?.y ?: 0.0
                    strokeStyle = Color.RED
                    lineWidth = 0.5
                }
            }

            if(debugData.newLeaderPosition != null) {
                canvas.line {
                    startX = debugData.vehiclePosition?.x ?: 0.0
                    startY = debugData.vehiclePosition?.y ?: 0.0
                    endX = debugData.newLeaderPosition?.x ?: 0.0
                    endY = debugData.newLeaderPosition?.y ?: 0.0
                    strokeStyle = Color.rgb(255, 128, 0)
                    lineWidth = 0.5
                }
            }

            if(debugData.newFollowerPosition != null) {
                canvas.line {
                    startX = debugData.vehiclePosition?.x ?: 0.0
                    startY = debugData.vehiclePosition?.y ?: 0.0
                    endX = debugData.newFollowerPosition?.x ?: 0.0
                    endY = debugData.newFollowerPosition?.y ?: 0.0
                    strokeStyle = Color.rgb(128, 255, 0)
                    lineWidth = 0.5
                }
            }


            canvas.restore()
        }
    }

}

fun CanvasRenderingContext2D.driverDebugView(debugData: DriverDebugData?): DriverDebugView {
    val driverDebugView = DriverDebugView(debugData)
    driverDebugView.draw(this)
    return driverDebugView
}
