package fr.ciadlab.sim.infrastructure

import fr.ciadlab.sim.infrastructure.intersection.*
import fr.ciadlab.sim.infrastructure.v2x.IntersectionRoadSideUnit
import fr.ciadlab.sim.math.algebra.Vector3D

data class DslRoadNetwork(
    var roads: MutableList<Road> = arrayListOf(),
    var intersections: MutableList<Intersection> = arrayListOf(),
    var trafficLights: MutableList<IntersectionTrafficLights> = arrayListOf(),
    var intersectionRsu: MutableList<IntersectionRoadSideUnit> = arrayListOf(),
    var trafficType: TrafficType = TrafficType.RIGHT_HAND)

fun roadNetwork(op: DslRoadNetwork.() -> Unit): RoadNetwork {
    val dslRoadNetwork = DslRoadNetwork()
    op.invoke(dslRoadNetwork)
    return RoadNetwork(
        dslRoadNetwork.roads,
        dslRoadNetwork.intersections,
        dslRoadNetwork.trafficLights,
        dslRoadNetwork.intersectionRsu,
        dslRoadNetwork.trafficType)
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

    /** The list of roads connected to the intersection along with the connected side */
    val connectedRoads: MutableMap<Road, IntersectionBuilder.ConnectedSide> = hashMapOf()

    fun withRoad(connectedRoad: Road, connectedSide: IntersectionBuilder.ConnectedSide) {
        this.builder.addRoad(connectedRoad, connectedSide)
        this.connectedRoads[connectedRoad] = connectedSide
        this.laneConnectors.addAll(this.builder.connectors)
    }
}

fun DslRoadNetwork.intersection(op: DslIntersection.() -> Unit): Intersection {
    val dslIntersection = DslIntersection()
    op.invoke(dslIntersection)
    val intersection = Intersection(dslIntersection.laneConnectors.toList(), dslIntersection.connectedRoads)
    this.intersections.add(intersection)
    return intersection
}

data class DslLaneConnector(
    val sourceRoad: Road,
    val destinationRoad: Road,
    var sourceLane: Int = 0,
    var destinationLane: Int = 0)

fun DslIntersection.laneConnector(
    sourceRoad: Road,
    destinationRoad: Road,
    op: DslLaneConnector.() -> Unit = {}): LaneConnector {
    val dslLaneConnector = DslLaneConnector(sourceRoad, destinationRoad)
    op.invoke(dslLaneConnector)

    val laneConnector = LaneConnector(
        dslLaneConnector.sourceRoad, dslLaneConnector.sourceLane,
        dslLaneConnector.destinationRoad, dslLaneConnector.destinationLane)

    this.laneConnectors.add(laneConnector)

    // Register the connected roads (source and destination)
    this.connectedRoads[sourceRoad] =
        if(sourceRoad.isForwardLane(dslLaneConnector.sourceLane)) IntersectionBuilder.ConnectedSide.DESTINATION
        else IntersectionBuilder.ConnectedSide.SOURCE
    this.connectedRoads[destinationRoad] =
        if(destinationRoad.isForwardLane(dslLaneConnector.destinationLane)) IntersectionBuilder.ConnectedSide.SOURCE
        else IntersectionBuilder.ConnectedSide.DESTINATION

    return laneConnector
}

// region Traffic lights

data class DslIntersectionTrafficLights(
    var trafficLights: MutableSet<IntersectionTrafficLight> = hashSetOf(),
    var policy: TrafficLightPolicy = TrafficLightPolicy {_, _ -> TrafficLightState.UNKNOWN}
)

fun DslRoadNetwork.trafficLights(
    op: DslIntersectionTrafficLights.() -> Unit = {}): IntersectionTrafficLights {
    val dslIntersectionTrafficLights = DslIntersectionTrafficLights()
    op.invoke(dslIntersectionTrafficLights)

    // TODO Check conflicts (several traffic lights addressing a single lane connector)

    // Register the traffic lights to the network
    val intersectionTrafficLights =
        IntersectionTrafficLights(dslIntersectionTrafficLights.trafficLights, dslIntersectionTrafficLights.policy)
    this.trafficLights.add(intersectionTrafficLights)

    return intersectionTrafficLights
}

data class DslIntersectionTrafficLight(
    val connectors: MutableList<LaneConnector> = arrayListOf())

fun DslIntersectionTrafficLights.trafficLight(
    op: DslIntersectionTrafficLight.() -> Unit = {}): IntersectionTrafficLight {
    val dslIntersectionTrafficLight = DslIntersectionTrafficLight()
    op.invoke(dslIntersectionTrafficLight)

    val trafficLight = IntersectionTrafficLight(dslIntersectionTrafficLight.connectors, TrafficLightState.UNKNOWN)
    this.trafficLights.add(trafficLight)

    return trafficLight
}

// region Phases and scheduling policy

data class DslFixedPhasesTrafficLights(
    val phases: MutableMap<IntersectionTrafficLight, MutableList<TrafficLightFixedPhase>> = hashMapOf())

data class DslFixedPhases(
    val trafficLight: IntersectionTrafficLight, val phases: MutableList<TrafficLightFixedPhase> = arrayListOf())

data class DslFixedPhase(val phase: TrafficLightFixedPhase)

fun DslIntersectionTrafficLights.fixedPhasesPolicy(
    op: DslFixedPhasesTrafficLights.() -> Unit = {}): TrafficLightPolicy {
    val dslFixedPhasesTrafficLights = DslFixedPhasesTrafficLights()
    op.invoke(dslFixedPhasesTrafficLights)

    // Build policy from dslFixedPhasesTrafficLights
    val trafficLights = dslFixedPhasesTrafficLights.phases
    val policyContent = hashMapOf<LaneConnector, List<TrafficLightFixedPhase>>()

    trafficLights.forEach { entry ->
        entry.key.laneConnectors.forEach {
            policyContent[it] = entry.value
        }
    }

    val policy = FixedPhasesTrafficLightPolicy(policyContent)
    return policy
}

fun DslFixedPhasesTrafficLights.phases(
    trafficLight: IntersectionTrafficLight,
    op: DslFixedPhases.() -> Unit = {}) : DslFixedPhases {
    val dslFixedPhases = DslFixedPhases(trafficLight)
    op.invoke(dslFixedPhases)

    // Add the phase to the DslFixedPhasesTrafficLights
    val trafficLightPhases = this.phases[trafficLight] ?: arrayListOf()
    trafficLightPhases.addAll(dslFixedPhases.phases)
    this.phases[trafficLight] = trafficLightPhases

    return dslFixedPhases
}

fun DslFixedPhases.phase(duration: Double, state: TrafficLightState): DslFixedPhase {
    val dslFixedPhase = DslFixedPhase(TrafficLightFixedPhase(duration, state))
    this.phases.add(dslFixedPhase.phase)
    return dslFixedPhase
}

// endregion

// endregion
