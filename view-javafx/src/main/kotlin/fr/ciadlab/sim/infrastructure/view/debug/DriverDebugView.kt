package fr.ciadlab.sim.infrastructure.view.debug

import fr.ciadlab.sim.car.behavior.DriverBehavioralDebugData
import javafx.scene.Group
import javafx.scene.paint.Color
import tornadofx.group
import tornadofx.line

class DriverDebugView(val debugData: DriverBehavioralDebugData): Group() {

    init {
        if(debugData.vehiclePosition != null) {
            group {
                if(debugData.leaderPosition != null) {
                    line {
                        startX = debugData.vehiclePosition?.x ?: 0.0
                        startY = debugData.vehiclePosition?.y ?: 0.0
                        endX = debugData.leaderPosition?.x ?: 0.0
                        endY = debugData.leaderPosition?.y ?: 0.0
                        strokeWidth = 1.0
                        stroke = Color.RED
                    }
                }

                if(debugData.newLeaderPosition != null) {
                    line {
                        startX = debugData.vehiclePosition?.x ?: 0.0
                        startY = debugData.vehiclePosition?.y ?: 0.0
                        endX = debugData.newLeaderPosition?.x ?: 0.0
                        endY = debugData.newLeaderPosition?.y ?: 0.0
                        stroke = Color.rgb(255, 128, 0)
                    }
                }

                if(debugData.newFollowerPosition != null) {
                    line {
                        startX = debugData.vehiclePosition?.x ?: 0.0
                        startY = debugData.vehiclePosition?.y ?: 0.0
                        endX = debugData.newFollowerPosition?.x ?: 0.0
                        endY = debugData.newFollowerPosition?.y ?: 0.0
                        stroke = Color.rgb(128, 255, 0)
                    }
                }
            }
        }
    }

}
