package fr.ciadlab.sim.infrastructure.view.scenario

import fr.ciadlab.sim.infrastructure.IntersectionBuilder
import fr.ciadlab.sim.infrastructure.intersection
import fr.ciadlab.sim.infrastructure.road
import fr.ciadlab.sim.infrastructure.roadNetwork
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.math.algebra.Vector3D
import fr.ciadlab.sim.traffic.exitArea
import fr.ciadlab.sim.traffic.spawner
import fr.ciadlab.sim.traffic.trafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.random.Random

class SimpleIntersection2Lanes {
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
            withRoad(roadWest, IntersectionBuilder.ConnectedSide.DESTINATION)
            withRoad(roadEast, IntersectionBuilder.ConnectedSide.SOURCE)
            withRoad(roadSouth, IntersectionBuilder.ConnectedSide.DESTINATION)
            withRoad(roadNorth, IntersectionBuilder.ConnectedSide.SOURCE)
        }
    }

    val simulation = trafficSimulation<Vehicle> {
        val wheelBase = 3.8
        val length = 4.0

        roadNetwork = network

        spawner {
            position = Vector2D(-100.0, 0.0)
            direction = Vector2D(1.0, 0.0)
            generation = {
                Vehicle(position, Vector2D(0.0, 0.0), 0.0, direction, 0.0, wheelBase, length)
            }
            strategy = { if(Random.nextFloat() < 0.01) { spawn() } }
        }

        spawner {
            position = Vector2D(0.0, -100.0)
            direction = Vector2D(0.0, 1.0)
            generation = {
                Vehicle(position, Vector2D(0.0, 0.0), 0.0, Vector2D(0.0, 1.0), 0.0, wheelBase, length)
            }
            strategy = { if(Random.nextFloat() < 0.01) { spawn() } }
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
