package fr.ciadlab.sim.v2x.intersection

import fr.ciadlab.sim.infrastructure.DslIntersection
import fr.ciadlab.sim.v2x.V2XCommunicationUnit

data class RoadSideUnitDsl(
    var protocol: (deltaTime: Double) -> Unit = {},
    var communicationUnit: V2XCommunicationUnit = V2XCommunicationUnit()
)

fun DslIntersection.roadSideUnit(op: RoadSideUnitDsl.() -> Unit): RoadSideUnitDsl {
    val roadSideUnitDsl = RoadSideUnitDsl()
    op.invoke(roadSideUnitDsl)

    return roadSideUnitDsl
}
