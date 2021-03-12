package fr.ciadlab.sim.v2x.intersection

import fr.ciadlab.sim.infrastructure.Intersection
import fr.ciadlab.sim.infrastructure.LaneConnector
import fr.ciadlab.sim.utils.UUID
import fr.ciadlab.sim.v2x.V2XCommunicationUnit
import fr.ciadlab.sim.v2x.V2XMessage

/**
 * Manage the right-of-way at an intersection by storing an ordered list of vehicles
 * @author Alexandre Lombard
 */
data class TransparentIntersectionManager(
    val intersection: Intersection,
    val orderList: MutableList<UUID> = arrayListOf(),
    val communicationUnit: V2XCommunicationUnit = V2XCommunicationUnit()
) {



}

enum class MessageType {
    APPROACH,
    UPDATE,
    EXIT
}

data class ApproachMessage(
    val identifier: UUID, val distance: Double, val speed: Double, val laneConnector: LaneConnector)
    : V2XMessage("${MessageType.APPROACH.name} $identifier $distance $speed".encodeToByteArray())

data class UpdateMessage(
    val identifier: UUID, val distance: Double, val speed: Double, val laneConnector: LaneConnector)
    : V2XMessage("${MessageType.UPDATE.name} $identifier $distance $speed".encodeToByteArray())

data class ExitMessage(
    val identifier: UUID, val distance: Double, val speed: Double, val laneConnector: LaneConnector)
    : V2XMessage("${MessageType.EXIT.name} $identifier $distance $speed".encodeToByteArray())
