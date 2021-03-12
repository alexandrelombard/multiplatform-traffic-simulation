package fr.ciadlab.sim.v2x

import fr.ciadlab.sim.utils.UUID

/**
 * Singleton containing reference to all potential V2X communication units
 * @author Alexandre Lombard
 */
class V2XEnvironment {
    // TODO Make it thread-safe

    private val repository: MutableMap<UUID, V2XCommunicationUnit> = hashMapOf()

    /**
     * Register a communication unit
     * @param communicationUnit the communication unit to register
     */
    fun registerUnit(communicationUnit: V2XCommunicationUnit) {
        repository[communicationUnit.identifier] = communicationUnit

        // Also register to the global environment
        if(this != GLOBAL_V2X_ENVIRONMENT) {
            GLOBAL_V2X_ENVIRONMENT.registerUnit(communicationUnit)
        }
    }

    /**
     * Sends a message to a single address
     */
    fun unicast(source: UUID, destination: UUID, message: V2XMessage) {
        repository[destination]?.fireMessageReceived(source, message)
    }

    fun multicast(source: UUID, destination: List<UUID>, message: V2XMessage) {
        destination.forEach {
            unicast(source, it, message)
        }
    }

    /**
     * Sends a message to all registered communication units
     */
    fun broadcast(source: UUID, message: V2XMessage) {
        repository.forEach { it.value.fireMessageReceived(source, message) }
    }

}

/**
 * Global V2X environment storing references on all communication units
 */
val GLOBAL_V2X_ENVIRONMENT = V2XEnvironment()
