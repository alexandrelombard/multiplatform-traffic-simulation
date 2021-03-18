package fr.ciadlab.sim.v2x.intersection

import fr.ciadlab.sim.infrastructure.LaneConnector
import fr.ciadlab.sim.utils.UUID
import fr.ciadlab.sim.v2x.V2XCommunicationUnit
import fr.ciadlab.sim.v2x.V2XMessage

/**
 * Manage the right-of-way at an intersection by storing an ordered list of vehicles
 * @author Alexandre Lombard
 */
data class TransparentIntersectionManager(
    val laneConnectors: List<LaneConnector>,
    val orderList: MutableList<TransparentIntersectionManagerMessage> = arrayListOf(),
    val communicationUnit: V2XCommunicationUnit = V2XCommunicationUnit()
) {
    val messageQueue = arrayListOf<Pair<UUID, V2XMessage>>()

    init {
        communicationUnit.onMessageReceived += { id, message -> messageQueue.add(Pair(id, message)) }
    }

    fun execute() {
        // Fetch the pending messages
        val pendingMessages = arrayListOf<Pair<UUID, V2XMessage>>()
        messageQueue.let {
            pendingMessages.addAll(it)
            it.clear()
        }
        val parsedMessages = pendingMessages.map {
            val splitMessage = it.second.data.decodeToString().split(" ")
            TransparentIntersectionManagerMessage(
                MessageType.valueOf(splitMessage[0]),
                UUID.fromString(splitMessage[1]),
                splitMessage[2].toDouble(),
                splitMessage[3].toDouble())
        }
        // Remove the ones who left the intersection
        parsedMessages.filter { it.type == MessageType.EXIT }.forEach { message ->
            orderList.removeAll { it.identifier == message.identifier }
        }
        // Update the "update" messages
        parsedMessages.filter { it.type == MessageType.UPDATE }.forEach { message ->
            val index = orderList.indexOfFirst { it.identifier == message.identifier }
            if(index != -1) {
                orderList[index] = message
            }
        }
        // Accept the new ones
        parsedMessages.filter { it.type == MessageType.APPROACH }.forEach { message ->
            if(!orderList.any { it.identifier == message.identifier }) {

            }
        }
        // Transmit the authorization list

    }
}

fun transparentIntersectionManager(communicationUnit: V2XCommunicationUnit, laneConnectors: List<LaneConnector>):
        (Double)->Unit {
    val messageQueue = arrayListOf<Pair<UUID, V2XMessage>>()
    communicationUnit.onMessageReceived += { id, message -> messageQueue.add(Pair(id, message)) }

    val authorizationList = arrayListOf<Pair<UUID, V2XMessage>>()

    return { deltaTime ->
        // Read the message queue

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
