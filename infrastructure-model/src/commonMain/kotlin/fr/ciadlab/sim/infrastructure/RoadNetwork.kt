package fr.ciadlab.sim.infrastructure

data class RoadNetwork(
    val roads: List<Road> = arrayListOf(),
    val intersections: List<Intersection> = arrayListOf(),
    val trafficType: TrafficType = TrafficType.RIGHT_HAND
)

enum class TrafficType {
    RIGHT_HAND,
    LEFT_HAND
}

