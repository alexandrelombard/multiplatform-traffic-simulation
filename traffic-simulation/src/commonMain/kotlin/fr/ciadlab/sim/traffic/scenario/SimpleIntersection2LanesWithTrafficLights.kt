package fr.ciadlab.sim.traffic.scenario

import fr.ciadlab.sim.infrastructure.*
import fr.ciadlab.sim.infrastructure.intersection.TrafficLightState
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
import fr.ciadlab.sim.vehicle.Vehicle

object SimpleIntersection2LanesWithTrafficLights {
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
            val westEast = laneConnector(roadWest, roadEast)
            val southNorth = laneConnector(roadSouth, roadNorth)

            trafficLights {
                val lightWestEast = trafficLight {
                    connectors += westEast
                }

                val lightSouthNorth = trafficLight {
                    connectors += southNorth
                }

                policy = fixedPhasesPolicy {
                    phases(lightWestEast) {
                        phase(10.0, TrafficLightState.GREEN)
                        phase(5.0, TrafficLightState.YELLOW)
                        phase(15.0, TrafficLightState.RED)
                    }
                    phases(lightSouthNorth) {
                        phase(15.0, TrafficLightState.RED)
                        phase(10.0, TrafficLightState.GREEN)
                        phase(5.0, TrafficLightState.YELLOW)
                    }
                }
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
