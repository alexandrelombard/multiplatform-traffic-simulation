package fr.ciadlab.sim.infrastructure.view.debug

import fr.ciadlab.sim.car.behavior.DriverBehavioralDebugData
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import tornadofx.group
import tornadofx.line
import tornadofx.opcr
import tornadofx.removeFromParent

class DriverDebugView(debugData: DriverBehavioralDebugData?): Group() {

    private var leaderLine: Line? = null
    private var newLeaderLine: Line? = null
    private var newFollowerLine: Line? = null

    init {
        update(debugData)
    }

    /**
     * Clear the debug data display
     */
    fun clear() {
        leaderLine?.removeFromParent()
        newLeaderLine?.removeFromParent()
        newFollowerLine?.removeFromParent()
    }

    /**
     * Update the debug data display on screen
     */
    fun update(debugData: DriverBehavioralDebugData?) {
        clear()

        if(debugData?.vehiclePosition != null) {
            group {
                if(debugData.leaderPosition != null) {
                    leaderLine = line {
                        startX = debugData.vehiclePosition?.x ?: 0.0
                        startY = debugData.vehiclePosition?.y ?: 0.0
                        endX = debugData.leaderPosition?.x ?: 0.0
                        endY = debugData.leaderPosition?.y ?: 0.0
                        stroke = Color.RED
                        strokeWidth = 0.5
                    }
                }

                if(debugData.newLeaderPosition != null) {
                    newLeaderLine = line {
                        startX = debugData.vehiclePosition?.x ?: 0.0
                        startY = debugData.vehiclePosition?.y ?: 0.0
                        endX = debugData.newLeaderPosition?.x ?: 0.0
                        endY = debugData.newLeaderPosition?.y ?: 0.0
                        stroke = Color.rgb(255, 128, 0)
                        strokeWidth = 0.5
                    }
                }

                if(debugData.newFollowerPosition != null) {
                    newFollowerLine = line {
                        startX = debugData.vehiclePosition?.x ?: 0.0
                        startY = debugData.vehiclePosition?.y ?: 0.0
                        endX = debugData.newFollowerPosition?.x ?: 0.0
                        endY = debugData.newFollowerPosition?.y ?: 0.0
                        stroke = Color.rgb(128, 255, 0)
                        strokeWidth = 0.5
                    }
                }
            }
        }
    }

}

fun Parent.driverDebugView(debugData: DriverBehavioralDebugData, op: DriverDebugView.() -> Unit = {}): DriverDebugView =
    opcr(this, DriverDebugView(debugData), op)
