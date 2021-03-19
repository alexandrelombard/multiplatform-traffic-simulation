package fr.ciadlab.sim.infrastructure.v2x

import fr.ciadlab.sim.infrastructure.Intersection
import fr.ciadlab.sim.v2x.V2XCommunicationUnit

data class IntersectionRoadSideUnit(
    val intersection: Intersection,
    val protocol: (deltaTime: Double) -> Unit = {},
    val communicationUnit: V2XCommunicationUnit = V2XCommunicationUnit())
