package fr.ciadlab.sim.v2x

import fr.ciadlab.sim.entity.Identifiable
import fr.ciadlab.sim.utils.UUID

/**
 * Represents a simulated communication unit
 * @author Alexandre Lombard
 */
data class V2XCommunicationUnit(
    val environment: V2XEnvironment = GLOBAL_V2X_ENVIRONMENT,
    override val identifier: UUID = UUID.randomUUID(),
    val onMessageReceived: MutableList<(UUID, V2XMessage)->Unit> = arrayListOf()) : Identifiable
{
    init {
        environment.registerUnit(this)
    }

    fun fireMessageReceived(source: UUID, message: V2XMessage) {
        onMessageReceived.forEach {
            it(source, message)
        }
    }
}
