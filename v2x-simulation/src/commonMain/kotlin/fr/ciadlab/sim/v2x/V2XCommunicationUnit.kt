package fr.ciadlab.sim.v2x

import fr.ciadlab.sim.entity.Identifiable
import fr.ciadlab.sim.utils.UUID

/**
 * Represents a simulated communication unit
 * @author Alexandre Lombard
 */
data class V2XCommunicationUnit(
    private val environment: V2XEnvironment = GLOBAL_V2X_ENVIRONMENT,
    override val identifier: UUID = UUID.randomUUID(),
    val onMessageReceived: MutableList<(UUID, V2XMessage)->Unit> = arrayListOf()) : Identifiable
{
    init {
        environment.registerUnit(this)
    }

    /**
     * Send a message
     * @param uuid the destination
     * @param message the message to send
     */
    fun unicast(uuid: UUID, message: V2XMessage) {
        environment.unicast(identifier, uuid, message)
    }

    /**
     * Broadcast a message
     * @param message the message to broadcast
     */
    fun broadcast(message: V2XMessage) {
        environment.broadcast(identifier, message)
    }

    fun fireMessageReceived(source: UUID, message: V2XMessage) {
        onMessageReceived.forEach {
            it(source, message)
        }
    }
}
