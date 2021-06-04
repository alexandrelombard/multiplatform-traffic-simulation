package fr.ciadlab.sim.traffic.scenario

import fr.ciadlab.sim.car.behavior.DriverAction
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
import fr.ciadlab.sim.traffic.spawner.TimeAwareGenerationStrategy
import fr.ciadlab.sim.traffic.strategy
import fr.ciadlab.sim.traffic.trafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle
import kotlin.math.abs
import kotlin.random.Random

object HighwaySection2LanesWithForcedMerge {
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

        /** Store the maximum speed of the vehicles (for randomization of the traffic flow */
        val speedLimits = hashMapOf<Vehicle, Double>()

        /** Store the spawning time and exit time of the vehicle */
        val spawningTimes = hashMapOf<Vehicle, Double>()
        val exitTimes = hashMapOf<Vehicle, Double>()

        // region Statistics
        /** Average acceleration (pair of [samples count, average]) */
        val averageAcceleration = hashMapOf<Vehicle, Pair<Int, Double>>()
        /** Absolute difference between the effective speed and the maximum speed */
        val speedDifference = hashMapOf<Vehicle, Pair<Int, Double>>()
        // endregion

        fun averageTravelTime() = exitTimes.map { it.value - spawningTimes[it.key]!! }.average()

        roadNetwork = network

        // Add the obstacle
        val obstacleVehicle = Vehicle(Vector2D(500.0, 1.75), Vector2D.ZERO, 0.0, Vector2D.I, 0.0)
        vehicles += obstacleVehicle

        onSpawn.add { v, _ ->
            basicOnSpawn(v, routes)
            speedLimits[v] = Random.nextDouble(15.0, 30.0)
            spawningTimes[v] = simulationTime
        }

        onDestroy.add { v ->
            speedLimits.remove(v)
            exitTimes[v] = simulationTime
        }

        onAfterStep.add {
            // Compute statistics
            for(v in vehicles) {
                if(v != obstacleVehicle) {
                    // Average acceleration
                    averageAcceleration[v].let {
                        if(it != null) {
                            averageAcceleration[v] = Pair(it.first + 1, (it.second * it.first + v.acceleration) / (it.first + 1))
                        } else {
                            averageAcceleration[v] = Pair(1, v.acceleration)
                        }
                    }

                    // Speed difference
                    speedDifference[v].let {
                        if(it != null) {
                            speedDifference[v] = Pair(it.first + 1, (it.second * it.first + abs(speedLimits[v]!! - v.speed)) / (it.first + 1))
                        } else {
                            speedDifference[v] = Pair(1, speedLimits[v]!! - v.speed)
                        }
                    }
                }
            }

            // Output statistics
            println("${averageTravelTime()},${averageAcceleration.values.map { it.second }.average()},${speedDifference.values.map { it.second }.average()}")
        }

        vehicleBehavior = {vehicle, deltaTime ->
            if(vehicle != obstacleVehicle)
                basicVehicleBehavior(routes, vehicle, deltaTime, maximumSpeed = speedLimits[vehicle] ?: 22.5)
            else
                DriverAction(0.0, 0.0)
        }

        vehicleUpdate = { vehicle, action, deltaTime -> basicVehicleUpdate(vehicle, action, deltaTime) }

        spawner {
            /** Random (for spawner) */
            val randomVelocity = Random(0xff012a17)
            val randomMoment = Random(0xff012a17)

            position = Vector2D(0.0, 1.725)
            direction = Vector2D(1.0, 0.0)
            generation = {
                Vehicle(position, Vector2D(randomVelocity.nextDouble(10.0, 20.0), 0.0), 0.0, direction, 0.0, wheelBase, length)
            }
            strategy(TimeAwareGenerationStrategy(this@trafficSimulation))
        }

        exitArea {
            radius = 7.5
            position = Vector2D(1000.0, 0.0)
        }
    }
}
