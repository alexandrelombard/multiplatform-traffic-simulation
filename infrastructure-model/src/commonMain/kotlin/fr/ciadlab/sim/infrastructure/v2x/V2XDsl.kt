package fr.ciadlab.sim.infrastructure.v2x

import fr.ciadlab.sim.infrastructure.DslRoadNetwork
import fr.ciadlab.sim.infrastructure.Intersection
import fr.ciadlab.sim.v2x.V2XCommunicationUnit

data class RoadSideUnitDsl(
    val intersection: Intersection,
    var protocol: (deltaTime: Double) -> Unit = {},
    var communicationUnit: V2XCommunicationUnit = V2XCommunicationUnit()
)

fun DslRoadNetwork.roadSideUnit(intersection: Intersection, op: RoadSideUnitDsl.() -> Unit): RoadSideUnitDsl {
    val roadSideUnitDsl = RoadSideUnitDsl(intersection)
    op.invoke(roadSideUnitDsl)

    val rsu = IntersectionRoadSideUnit(
        intersection, protocol = roadSideUnitDsl.protocol, communicationUnit = roadSideUnitDsl.communicationUnit)

    this.intersectionRsu.add(rsu)

    return roadSideUnitDsl
}
