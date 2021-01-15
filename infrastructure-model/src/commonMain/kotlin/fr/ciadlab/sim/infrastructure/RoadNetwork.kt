package fr.ciadlab.sim.infrastructure

data class RoadNetwork(
    val roads: List<Road> = arrayListOf(),
    val intersections: List<Intersection> = arrayListOf(),
    val trafficType: TrafficType = TrafficType.RIGHT_HAND
) {
    /** Contains the links between roads, allows to know which roads are connected to a given one */
    private val connections = hashMapOf<Road, MutableSet<Road>>()
    /** Contains the links between roads and intersection, allows to know which intersections are connected to a road */
    private val roadIntersections = hashMapOf<Road, MutableSet<Intersection>>()

    init {
        intersections.flatMap { it.laneConnectors }.forEach {
            // Register the connections (note: the road network is immutable)
            val connectedRoads = connections[it.sourceRoad]

            if(connectedRoads == null) {
                connections[it.sourceRoad] = hashSetOf(it.destinationRoad)
            } else {
                connectedRoads.add(it.destinationRoad)
            }
        }

        intersections.forEach { intersection ->
            intersection.laneConnectors.forEach { laneConnector ->
                // Register the link between roads and intersections
                val connectedIntersections = roadIntersections[laneConnector.sourceRoad]

                if(connectedIntersections == null) {
                    roadIntersections[laneConnector.sourceRoad] = hashSetOf(intersection)
                } else {
                    connectedIntersections.add(intersection)
                }
            }
        }
    }

    /**
     * Gets the roads connected to the given one through an intersection
     * @param road the road
     * @return the collection of roads connected to the given one (an empty collection if the road is not known)
     */
    fun getConnectedRoads(road: Road): Collection<Road> {
        return connections[road] ?: hashSetOf()
    }

    /**
     * Gets the intersections connected to the given road
     * @param road the road
     * @return the collection of intersections connected to the road (an empty collection if the road is not known)
     */
    fun getIntersections(road: Road): Collection<Intersection> {
        return roadIntersections[road] ?: hashSetOf()
    }

    /**
     * Gets the intersection at the begin of the road
     * @param road the road
     * @return the intersection or <code>null</code> if there is no intersection at the end of the road
     */
    fun getBeginIntersection(road: Road): Intersection? {
        return roadIntersections[road]?.firstOrNull { it.connectedRoads[road] == IntersectionBuilder.ConnectedSide.SOURCE }
    }

    /**
     * Gets the intersection at the end of the road
     * @param road the road
     * @return the intersection or <code>null</code> if there is no intersection at the end of the road
     */
    fun getEndIntersection(road: Road): Intersection? {
        return roadIntersections[road]?.firstOrNull { it.connectedRoads[road] == IntersectionBuilder.ConnectedSide.DESTINATION }
    }

    /**
     * Returns <code>true</code> if the destination is at the begin side of the source, <code>false</code> for any other
     * case
     * @param source the source road
     * @param destination the destination road
     */
    fun isAtBegin(source: Road, destination: Road): Boolean {
        val sourceIntersections = roadIntersections[source]
        val destinationIntersections = roadIntersections[destination]

        if(sourceIntersections == null || destinationIntersections == null) {
            return false
        }

        val commonsIntersections = sourceIntersections.intersect(destinationIntersections)

        return commonsIntersections.any { it.connectedRoads[source] == IntersectionBuilder.ConnectedSide.SOURCE }
    }

    /**
     * Returns <code>true</code> if the destination is at the end side of the source, <code>false</code> for any other
     * case
     * @param source the source road
     * @param destination the destination road
     */
    fun isAtEnd(source: Road, destination: Road): Boolean {
        val sourceIntersections = roadIntersections[source]
        val destinationIntersections = roadIntersections[destination]

        if(sourceIntersections == null || destinationIntersections == null) {
            return false
        }

        val commonsIntersections = sourceIntersections.intersect(destinationIntersections)

        return commonsIntersections.any { it.connectedRoads[source] == IntersectionBuilder.ConnectedSide.DESTINATION }
    }
}

enum class TrafficType {
    RIGHT_HAND,
    LEFT_HAND
}

