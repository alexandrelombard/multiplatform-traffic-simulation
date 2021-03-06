package traffic

import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.traffic.*
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.test.Test

class TrafficSimulationTest {

    @Test
    fun defineTrafficSimulation() {
        trafficSimulation<Vehicle> {
            spawner<Vehicle> {
                position = Vector2D(0.0, 0.0)
                generation {
                    Vehicle(
                        position, Vector2D(0.0, 0.0),
                        0.0, direction, 0.0, 3.5, 5.0)
                }
                onGeneration += {

                }
            }

            exitArea {
                position = Vector2D(100.0, 0.0)
            }

            roadNetwork {

            }
        }
    }

}
