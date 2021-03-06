package fr.ciadlab.sim.traffic.scenario

import fr.ciadlab.sim.infrastructure.*
import fr.ciadlab.sim.infrastructure.v2x.roadSideUnit
import fr.ciadlab.sim.infrastructure.view.basics.basicV2XVehicleBehavior
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.math.algebra.Vector3D
import fr.ciadlab.sim.traffic.basics.basicOnSpawn
import fr.ciadlab.sim.traffic.basics.basicVehicleUpdate
import fr.ciadlab.sim.traffic.exitArea
import fr.ciadlab.sim.traffic.spawner
import fr.ciadlab.sim.traffic.spawner.TimeAwareGenerationStrategy
import fr.ciadlab.sim.traffic.strategy
import fr.ciadlab.sim.traffic.trafficSimulation
import fr.ciadlab.sim.v2x.V2XCommunicationUnit
import fr.ciadlab.sim.v2x.intersection.transparentIntersectionManager
import fr.ciadlab.sim.vehicle.Vehicle

object TwoIntersections2LanesWithV2X {
    val network = roadNetwork {
        val roadWest = road {
            points = listOf(Vector3D(-100.0, 0.0, 0.0), Vector3D(-10.0, 0.0, 0.0))
            oneWay = true
            forwardLanesCount = 1
            backwardLanesCount = 0
        }

        val roadMiddle = road {
            points = listOf(Vector3D(10.0, 0.0, 0.0), Vector3D(90.0, 0.0, 0.0))
            oneWay = true
            forwardLanesCount = 1
            backwardLanesCount = 0
        }

        val roadEast = road {
            points = listOf(Vector3D(110.0, 0.0, 0.0), Vector3D(200.0, 0.0, 0.0))
            oneWay = true
            forwardLanesCount = 1
            backwardLanesCount = 0
        }

        val roadSouthWest = road {
            points = listOf(Vector3D(0.0, -100.0, 0.0), Vector3D(0.0, -10.0, 0.0))
            oneWay = true
            forwardLanesCount = 1
            backwardLanesCount = 0
        }

        val roadSouthEast = road {
            points = listOf(Vector3D(100.0, -100.0, 0.0), Vector3D(100.0, -10.0, 0.0))
            oneWay = true
            forwardLanesCount = 1
            backwardLanesCount = 0
        }

        val roadNorthWest = road {
            points = listOf(Vector3D(0.0, 10.0, 0.0), Vector3D(0.0, 100.0, 0.0))
            oneWay = true
            forwardLanesCount = 1
            backwardLanesCount = 0
        }

        val roadNorthEast = road {
            points = listOf(Vector3D(100.0, 10.0, 0.0), Vector3D(100.0, 100.0, 0.0))
            oneWay = true
            forwardLanesCount = 1
            backwardLanesCount = 0
        }

        val firstIntersection = intersection {
            laneConnector(roadWest, roadMiddle)
            laneConnector(roadSouthWest, roadNorthWest)
        }

        roadSideUnit(firstIntersection) {
            protocol = transparentIntersectionManager(communicationUnit, intersection.laneConnectors)
        }

        val secondIntersection = intersection {
            laneConnector(roadMiddle, roadEast)
            laneConnector(roadSouthEast, roadNorthEast)
        }

        roadSideUnit(secondIntersection) {
            protocol = transparentIntersectionManager(communicationUnit, intersection.laneConnectors)
        }
    }

    val simulation = trafficSimulation<Vehicle> {
        val wheelBase = 3.8
        val length = 4.0

        /** Store the routes of the vehicles */
        val routes = hashMapOf<Vehicle, List<Pair<Road, Boolean>>?>()

        /** Store the communication units of the vehicles */
        val onBoardUnits = hashMapOf<Vehicle, V2XCommunicationUnit>()

        roadNetwork = network

        onSpawn += { v, _ ->
            basicOnSpawn(v, routes)
            onBoardUnits[v] = V2XCommunicationUnit()
        }
        onDestroy += { onBoardUnits.remove(it) }

        vehicleBehavior = {vehicle, deltaTime ->
            basicV2XVehicleBehavior(routes, vehicle, onBoardUnits[vehicle]!!, deltaTime)
        }

        vehicleUpdate = { vehicle, action, deltaTime -> basicVehicleUpdate(vehicle, action, deltaTime) }

        spawner {
            position = Vector2D(-100.0, 0.0)
            direction = Vector2D(1.0, 0.0)
            generation = {
                val vehicle = Vehicle(position, Vector2D(0.0, 0.0), 0.0, direction, 0.0, wheelBase, length)
                onBoardUnits[vehicle] = V2XCommunicationUnit()
                vehicle
            }
            strategy(TimeAwareGenerationStrategy(this@trafficSimulation))
        }

        spawner {
            position = Vector2D(0.0, -100.0)
            direction = Vector2D(0.0, 1.0)
            generation = {
                val vehicle = Vehicle(position, Vector2D(0.0, 0.0), 0.0, direction, 0.0, wheelBase, length)
                onBoardUnits[vehicle] = V2XCommunicationUnit()
                vehicle
            }
            strategy(TimeAwareGenerationStrategy(this@trafficSimulation))
        }

        spawner {
            position = Vector2D(100.0, -100.0)
            direction = Vector2D(0.0, 1.0)
            generation = {
                val vehicle = Vehicle(position, Vector2D(0.0, 0.0), 0.0, direction, 0.0, wheelBase, length)
                onBoardUnits[vehicle] = V2XCommunicationUnit()
                vehicle
            }
            strategy(TimeAwareGenerationStrategy(this@trafficSimulation))
        }

        exitArea {
            radius = 7.5
            position = Vector2D(200.0, 0.0)
        }

        exitArea {
            radius = 7.5
            position = Vector2D(0.0, 100.0)
        }

        exitArea {
            radius = 7.5
            position = Vector2D(100.0, 100.0)
        }
    }
}
