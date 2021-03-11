package fr.ciadlab.sim.infrastructure.view.scenario

import fr.ciadlab.sim.infrastructure.*
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

object SimpleIntersection2Lanes {
    val network = roadNetwork {
        val roadWest = road {
            points = listOf(Vector3D(-100.0, 0.0, 0.0), Vector3D(-10.0, 0.0, 0.0))
            oneWay = true
            forwardLanesCount = 1
            backwardLanesCount = 0
        }

        val roadEast = road {
            points = listOf(Vector3D(10.0, 0.0, 0.0), Vector3D(100.0, 0.0, 0.0))
            oneWay = true
            forwardLanesCount = 1
            backwardLanesCount = 0
        }

        val roadSouth = road {
            points = listOf(Vector3D(0.0, -100.0, 0.0), Vector3D(0.0, -10.0, 0.0))
            oneWay = true
            forwardLanesCount = 1
            backwardLanesCount = 0
        }

        val roadNorth = road {
            points = listOf(Vector3D(0.0, 10.0, 0.0), Vector3D(0.0, 100.0, 0.0))
            oneWay = true
            forwardLanesCount = 1
            backwardLanesCount = 0
        }

        intersection {
            laneConnector(roadWest, roadEast)
            laneConnector(roadSouth, roadNorth)
        }
    }

    val simulation = trafficSimulation<Vehicle> {
        val wheelBase = 3.8
        val length = 4.0

        /** Store the routes of the vehicles */
        val routes = hashMapOf<Vehicle, List<Pair<Road, Boolean>>?>()

        roadNetwork = network

        onSpawn.add { v, _ -> basicOnSpawn(v, routes) }

        vehicleBehavior = {vehicle, deltaTime -> basicVehicleBehavior(routes, vehicle, deltaTime) }

        vehicleUpdate = { vehicle, action, deltaTime -> basicVehicleUpdate(vehicle, action, deltaTime) }

        spawner {
            position = Vector2D(-100.0, 0.0)
            direction = Vector2D(1.0, 0.0)
            generation = {
                Vehicle(position, Vector2D(0.0, 0.0), 0.0, direction, 0.0, wheelBase, length)
            }
            strategy = { if(Random.nextFloat() < 0.2 * it) { spawn() } }
        }

        spawner {
            position = Vector2D(0.0, -100.0)
            direction = Vector2D(0.0, 1.0)
            generation = {
                Vehicle(position, Vector2D(0.0, 0.0), 0.0, Vector2D(0.0, 1.0), 0.0, wheelBase, length)
            }
            strategy = { if(Random.nextFloat() < 0.2 * it) { spawn() } }
        }

        exitArea {
            radius = 7.5
            position = Vector2D(100.0, 0.0)
        }

        exitArea {
            radius = 7.5
            position = Vector2D(0.0, 100.0)
        }
    }
}
