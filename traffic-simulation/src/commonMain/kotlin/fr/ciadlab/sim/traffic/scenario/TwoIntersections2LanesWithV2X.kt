package fr.ciadlab.sim.traffic.scenario

import fr.ciadlab.sim.infrastructure.*
import fr.ciadlab.sim.infrastructure.view.basics.basicVehicleBehavior
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.math.algebra.Vector3D
import fr.ciadlab.sim.traffic.basics.basicOnSpawn
import fr.ciadlab.sim.traffic.basics.basicVehicleUpdate
import fr.ciadlab.sim.traffic.exitArea
import fr.ciadlab.sim.traffic.spawner
import fr.ciadlab.sim.traffic.spawner.TimeAwareGenerationStrategy
import fr.ciadlab.sim.traffic.strategy
import fr.ciadlab.sim.traffic.trafficSimulation
import fr.ciadlab.sim.utils.UUID
import fr.ciadlab.sim.v2x.V2XMessage
import fr.ciadlab.sim.v2x.intersection.roadSideUnit
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

        intersection {
            val westEast = laneConnector(roadWest, roadMiddle)
            val southNorth = laneConnector(roadSouthWest, roadNorthWest)

//            roadSideUnit {
//                protocol = intersectionManagement {
//                    policy = transparentIntersectionManager {
//
//                    }
//                }
//            }
        }

        intersection {
            val westEast = laneConnector(roadMiddle, roadEast)
            val southNorth = laneConnector(roadSouthEast, roadNorthEast)

            roadSideUnit {
                val messageQueue = arrayListOf<Pair<UUID, V2XMessage>>()
                communicationUnit.onMessageReceived += { id, message -> messageQueue.add(Pair(id, message)) }

                val authorizationList = arrayListOf<Pair<UUID, V2XMessage>>()

                protocol = {
                    // Read the message queue
                    val pendingMessages = arrayListOf<Pair<UUID, V2XMessage>>()
                    messageQueue.let {
                        pendingMessages.addAll(it)
                        it.clear()
                    }
                    // Update the internal list
                    pendingMessages.filter { it.second.d }
                    // Transmit the authorization list
                }

//                protocol = transparentIntersectionManager(communicationUnit, this@intersection.laneConnectors)
            }
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
            strategy(TimeAwareGenerationStrategy(this@trafficSimulation))
        }

        spawner {
            position = Vector2D(0.0, -100.0)
            direction = Vector2D(0.0, 1.0)
            generation = {
                Vehicle(position, Vector2D(0.0, 0.0), 0.0, direction, 0.0, wheelBase, length)
            }
            strategy(TimeAwareGenerationStrategy(this@trafficSimulation))
        }

        spawner {
            position = Vector2D(100.0, -100.0)
            direction = Vector2D(0.0, 1.0)
            generation = {
                Vehicle(position, Vector2D(0.0, 0.0), 0.0, direction, 0.0, wheelBase, length)
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
