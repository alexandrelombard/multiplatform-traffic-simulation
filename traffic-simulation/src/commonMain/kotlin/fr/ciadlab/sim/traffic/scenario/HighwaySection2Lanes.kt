package fr.ciadlab.sim.traffic.scenario

import fr.ciadlab.sim.infrastructure.Road
import fr.ciadlab.sim.infrastructure.road
import fr.ciadlab.sim.infrastructure.roadNetwork
import fr.ciadlab.sim.infrastructure.view.basics.basicVehicleBehavior
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.math.algebra.Vector3D
import fr.ciadlab.sim.traffic.basics.basicOnSpawn
import fr.ciadlab.sim.traffic.basics.basicVehicleUpdate
import fr.ciadlab.sim.traffic.exitArea
import fr.ciadlab.sim.traffic.spawner
import fr.ciadlab.sim.traffic.trafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.random.Random

object HighwaySection2Lanes {
    val network = roadNetwork {
        val road = road {
            points = listOf(Vector3D(0.0, 0.0, 0.0), Vector3D(1000.0, 0.0, 0.0))
            oneWay = true
            forwardLanesCount = 2
            backwardLanesCount = 0
        }
    }

    val simulation = trafficSimulation<Vehicle> {
        val wheelBase = 3.8
        val length = 4.0

        /** Store the routes of the vehicles */
        val routes = hashMapOf<Vehicle, List<Pair<Road, Boolean>>?>()

        roadNetwork = network

        onSpawn.add { v, _ -> basicOnSpawn(v, routes) }

        vehicleBehavior = {vehicle, deltaTime -> basicVehicleBehavior(routes, vehicle, deltaTime, maximumSpeed = 20.0) }

        vehicleUpdate = { vehicle, action, deltaTime -> basicVehicleUpdate(vehicle, action, deltaTime) }

        spawner {
            position = Vector2D(0.0, 0.0)
            direction = Vector2D(1.0, 0.0)
            generation = {
                Vehicle(position, Vector2D(Random.nextDouble(10.0, 20.0), 0.0), 0.0, direction, 0.0, wheelBase, length)
            }
            strategy = { if(Random.nextFloat() < 0.3 * it) { spawn() } }
        }

        exitArea {
            radius = 7.5
            position = Vector2D(1000.0, 0.0)
        }
    }
}
