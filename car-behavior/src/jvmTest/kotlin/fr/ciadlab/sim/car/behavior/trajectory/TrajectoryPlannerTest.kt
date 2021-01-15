package fr.ciadlab.sim.car.behavior.trajectory

import fr.ciadlab.sim.infrastructure.IntersectionBuilder
import fr.ciadlab.sim.infrastructure.intersection
import fr.ciadlab.sim.infrastructure.road
import fr.ciadlab.sim.infrastructure.roadNetwork
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.math.algebra.Vector3D
import fr.ciadlab.sim.math.geometry.hermiteSpline
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.test.Test

class TrajectoryPlannerTest {
    @Test
    fun testComputeTrajectory() {
        val roadNetwork = roadNetwork {
            val road1 = road {
                points =
                    hermiteSpline(
                        Vector3D(0.0, 0.0, 0.0),
                        Vector3D(100.0, 0.0, 0.0),
                        Vector3D(200.0, 100.0, 0.0),
                        Vector3D(100.0, 0.0, 0.0),
                        Vector3D(400.0, 0.0, 0.0),
                        Vector3D(100.0, 0.0, 0.0),
                        Vector3D(625.0, 50.0, 0.0),
                        Vector3D(100.0, 0.0, 0.0)
                    )
                oneWay = false
                forwardLanesCount = 3
                backwardLanesCount = 2
            }
            val road2 = road {
                points = listOf(
                    Vector3D(650.0, 75.0, 0.0),
                    Vector3D(650.0, 400.0, 0.0)
                )
                oneWay = false
                forwardLanesCount = 2
                backwardLanesCount = 2
            }
            val road3 = road {
                points = listOf(
                    Vector3D(675.0, 50.0, 0.0),
                    Vector3D(1000.0, 50.0, 0.0)
                )
                oneWay = false
                forwardLanesCount = 2
                backwardLanesCount = 2
            }

            intersection {
                withRoad(road1, IntersectionBuilder.ConnectedSide.DESTINATION)
                withRoad(road2, IntersectionBuilder.ConnectedSide.SOURCE)
                withRoad(road3, IntersectionBuilder.ConnectedSide.SOURCE)
            }
        }

        val vehicle = Vehicle(
            Vector2D(650.0, 100.0), Vector2D(0.0, -5.0), 0.0, Vector2D(0.0, -1.0), 0.0, 3.8, 4.0)

        val trajectoryPlanner = TrajectoryPlanner(roadNetwork)

//        trajectoryPlanner.computeTrajectory()
    }
}
