package fr.ciadlab.sim.v2x.intersection

import fr.ciadlab.sim.infrastructure.Intersection
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

    init {
        this.communicationUnit.onMessageReceived += { source, message ->
            val splitMessage = message.data.decodeToString().split(" ")
            if(splitMessage.isNotEmpty()) {
                val parsedMessage = TransparentIntersectionManagerMessage(
                    MessageType.valueOf(splitMessage[0]),
                    UUID.fromString(splitMessage[1]),
                    splitMessage[2].toDouble(),
                    splitMessage[3].toDouble())

                when(parsedMessage.type) {
                    MessageType.APPROACH -> {
                        // TODO
                    }
                    MessageType.UPDATE -> {
                        // TODO
                    }
                    MessageType.EXIT -> {
                        // TODO
                    }
                }
            }
        }
    }
}

enum class MessageType {
    APPROACH,
    UPDATE,
    EXIT
}

data class TransparentIntersectionManagerMessage(
    val type: MessageType, val identifier: UUID, val distance: Double, val speed: Double)
    : V2XMessage("${type.name} $identifier $distance $speed".encodeToByteArray())
