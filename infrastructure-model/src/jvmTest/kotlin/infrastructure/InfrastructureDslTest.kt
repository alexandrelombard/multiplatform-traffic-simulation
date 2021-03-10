package infrastructure

import fr.ciadlab.sim.infrastructure.IntersectionBuilder
import fr.ciadlab.sim.infrastructure.intersection
import fr.ciadlab.sim.infrastructure.road
import fr.ciadlab.sim.infrastructure.roadNetwork
import fr.ciadlab.sim.math.algebra.Vector3D

class InfrastructureDslTest {

    fun defineInfrastructure() {
        roadNetwork {
            val roadA = road {
                points = listOf(Vector3D(0.0, 0.0, 0.0), Vector3D(100.0, 0.0, 0.0))
            }
            val roadB = road {
                points = listOf()
            }

            intersection {
                withRoad(roadA, IntersectionBuilder.ConnectedSide.DESTINATION)
                withRoad(roadB, IntersectionBuilder.ConnectedSide.SOURCE)
            }
        }
    }

}
