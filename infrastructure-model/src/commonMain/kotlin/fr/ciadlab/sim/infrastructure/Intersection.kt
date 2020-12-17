package fr.ciadlab.sim.infrastructure

/**
 * Data class representing an intersection as a list of lane connectors
 */
data class Intersection(
    val laneConnectors: List<LaneConnector> = arrayListOf()
)

/**
 * Utility class building an intersection by guessing the connectors from
 * the roads
 */
class IntersectionBuilder(val uturnsAllowed: Boolean = true) {

    val connectedRoads = hashSetOf<Pair<Road, ConnectedSide>>()
    val connectors = arrayListOf<LaneConnector>()

    fun addRoad(road: Road, connectedSide: ConnectedSide) : IntersectionBuilder {
        if(connectedRoads.isEmpty()) {
            // By default the allowed movements are U-turn
            if(uturnsAllowed) {
                uturn(road, connectedSide)
            }

            // We register the first connected road
            connectedRoads.add(Pair(road, connectedSide))
        } else {
            // We take every existing roads and we compute all the possible movements
            connectedRoads.forEach {
                movements(it.first, it.second, road, connectedSide)
                movements(road, connectedSide, it.first, it.second)
            }

            // We generate uturn movements
            if(uturnsAllowed) {
                uturn(road, connectedSide)
            }

            connectedRoads.add(Pair(road, connectedSide))
        }

        return this
    }

    /**
     * Computes all possible movements and register them as lane connectors
     */
    private fun movements(firstRoad: Road, firstConnectedSide: ConnectedSide,
                          secondRoad: Road, secondConnectedSide: ConnectedSide) {
        if(firstConnectedSide == ConnectedSide.DESTINATION && secondConnectedSide == ConnectedSide.SOURCE) {
            // Forward direction to forward direction
            firstRoad.forwardLanes.forEach { firstRoadLane ->
                secondRoad.forwardLanes.forEach { secondRoadLane ->
                    connectors.add(
                        LaneConnector(
                            firstRoad,
                            firstRoadLane,
                            secondRoad,
                            secondRoadLane
                        )
                    )
                }
            }
        } else if(firstConnectedSide == ConnectedSide.DESTINATION && secondConnectedSide == ConnectedSide.DESTINATION) {
            firstRoad.forwardLanes.forEach { firstRoadLane ->
                secondRoad.backwardLanes.forEach { secondRoadLane ->
                    connectors.add(
                        LaneConnector(
                            firstRoad,
                            firstRoadLane,
                            secondRoad,
                            secondRoadLane
                        )
                    )
                }
            }
        } else if(firstConnectedSide == ConnectedSide.SOURCE && secondConnectedSide == ConnectedSide.SOURCE) {
            firstRoad.backwardLanes.forEach { firstRoadLane ->
                secondRoad.forwardLanes.forEach { secondRoadLane ->
                    connectors.add(
                        LaneConnector(
                            firstRoad,
                            firstRoadLane,
                            secondRoad,
                            secondRoadLane
                        )
                    )
                }
            }
        } else if(firstConnectedSide == ConnectedSide.SOURCE && secondConnectedSide == ConnectedSide.DESTINATION) {
            firstRoad.backwardLanes.forEach { firstRoadLane ->
                secondRoad.backwardLanes.forEach { secondRoadLane ->
                    connectors.add(
                        LaneConnector(
                            firstRoad,
                            firstRoadLane,
                            secondRoad,
                            secondRoadLane
                        )
                    )
                }
            }
        }
    }

    /**
     * Generates the uturns movements
     */
    private fun uturn(road: Road, connectedSide: ConnectedSide) {
        // Only the leftest lane of the driving direction is allowed to perform a uturn
        if(connectedSide == ConnectedSide.DESTINATION) {
            road.backwardLanes.forEach {
                connectors.add(
                    LaneConnector(
                        road,
                        road.forwardLaneIndex,
                        road,
                        it
                    )
                )
            }
        } else {
            road.forwardLanes.forEach {
                connectors.add(
                    LaneConnector(
                        road,
                        road.forwardLaneIndex - 1,
                        road,
                        it
                    )
                )
            }
        }
    }

    enum class ConnectedSide {
        SOURCE,
        DESTINATION
    }
}