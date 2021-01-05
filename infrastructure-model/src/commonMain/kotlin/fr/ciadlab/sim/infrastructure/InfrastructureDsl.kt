package fr.ciadlab.sim.infrastructure

import fr.ciadlab.sim.math.algebra.Vector3D

data class DslRoadNetwork(
    var roads: MutableList<Road> = arrayListOf(),
    var intersections: MutableList<Intersection> = arrayListOf(),
    var trafficType: TrafficType = TrafficType.RIGHT_HAND)

fun roadNetwork(op: DslRoadNetwork.() -> Unit): RoadNetwork {
    val dslRoadNetwork = DslRoadNetwork()
    op.invoke(dslRoadNetwork)
    return RoadNetwork(dslRoadNetwork.roads, dslRoadNetwork.intersections, dslRoadNetwork.trafficType)
}

data class DslRoad(
    var points: List<Vector3D> = arrayListOf(),
    var oneWay: Boolean = false,
    var forwardLanesCount: Int = 2,
    var backwardLanesCount: Int = 2
)

fun DslRoadNetwork.road(op: DslRoad.() -> Unit): Road {
    val dslRoad = DslRoad()
    op.invoke(dslRoad)
    val road = Road(dslRoad.points, dslRoad.oneWay, dslRoad.forwardLanesCount, dslRoad.backwardLanesCount)
    this.roads.add(road)
    return road
}

data class DslIntersection(
    val laneConnectors: MutableSet<LaneConnector> = mutableSetOf(),
    private val builder: IntersectionBuilder = IntersectionBuilder()) {
    fun withRoad(connectedRoad: Road, connectedSide: IntersectionBuilder.ConnectedSide) {
        this.builder.addRoad(connectedRoad, connectedSide)
        this.laneConnectors.addAll(this.builder.connectors)
    }
}

fun DslRoadNetwork.intersection(op: DslIntersection.() -> Unit): Intersection {
    val dslIntersection = DslIntersection()
    op.invoke(dslIntersection)
    val intersection = Intersection(dslIntersection.laneConnectors.toList())
    this.intersections.add(intersection)
    return intersection
}

data class DslLaneConnector(
    val sourceRoad: Road,
    val destinationRoad: Road,
    var sourceLane: Int = 0,
    var destinationLane: Int = 0)

fun DslRoadNetwork.laneConnector(
    sourceRoad: Road,
    destinationRoad: Road,
    op: DslLaneConnector.() -> Unit): LaneConnector {
    val dslLaneConnector = DslLaneConnector(sourceRoad, destinationRoad)
    op.invoke(dslLaneConnector)
    return LaneConnector(
        dslLaneConnector.sourceRoad, dslLaneConnector.sourceLane,
        dslLaneConnector.destinationRoad, dslLaneConnector.destinationLane)
}
