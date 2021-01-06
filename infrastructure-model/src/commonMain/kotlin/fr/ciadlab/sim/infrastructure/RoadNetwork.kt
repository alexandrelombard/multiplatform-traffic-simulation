package fr.ciadlab.sim.infrastructure

data class RoadNetwork(
    val roads: List<Road> = arrayListOf(),
    val intersections: List<Intersection> = arrayListOf(),
    val trafficType: TrafficType = TrafficType.RIGHT_HAND
) {
    private val connections = hashMapOf<Road, MutableSet<Road>>()

    init {
        // Register the connections (note: the road network is immutable)
        intersections.flatMap { it.laneConnectors }.forEach {
            val connectedRoads = connections[it.sourceRoad]

            if(connectedRoads == null) {
                connections[it.sourceRoad] = hashSetOf(it.destinationRoad)
            } else {
                connectedRoads.add(it.destinationRoad)
            }
        }
    }

    /**
     * Gets the roads connected to the given one through an intersection
     * @param road the road
     * @return the list of roads connected to the given one
     */
    fun getConnectedRoads(road: Road): Collection<Road> {
        return connections[road] ?: hashSetOf()
    }
}

enum class TrafficType {
    RIGHT_HAND,
    LEFT_HAND
}

