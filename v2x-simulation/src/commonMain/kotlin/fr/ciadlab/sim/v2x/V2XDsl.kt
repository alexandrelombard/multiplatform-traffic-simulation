package fr.ciadlab.sim.v2x

data class RoadSideUnitDsl(
    var protocol: (deltaTime: Double) -> Unit = {},
    var communicationUnit: V2XCommunicationUnit = V2XCommunicationUnit()
)

fun roadSideUnit(op: RoadSideUnitDsl.() -> Unit): RoadSideUnitDsl {
    val roadSideUnitDsl = RoadSideUnitDsl()
    op.invoke(roadSideUnitDsl)

    return roadSideUnitDsl
}
