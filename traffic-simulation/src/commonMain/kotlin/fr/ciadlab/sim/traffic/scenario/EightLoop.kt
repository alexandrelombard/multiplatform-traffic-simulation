package fr.ciadlab.sim.traffic.scenario

import fr.ciadlab.sim.infrastructure.road
import fr.ciadlab.sim.infrastructure.roadNetwork
import fr.ciadlab.sim.infrastructure.view.basics.basicVehicleBehavior
import fr.ciadlab.sim.math.algebra.Vector2D
import fr.ciadlab.sim.math.algebra.Vector3D
import fr.ciadlab.sim.math.geometry.hermiteSpline
import fr.ciadlab.sim.traffic.basics.basicVehicleUpdate
import fr.ciadlab.sim.traffic.trafficSimulation
import fr.ciadlab.sim.vehicle.Vehicle

object EightLoop {
    val network = roadNetwork {
        val eightShapedRoad = road {
            points = listOf(
                Vector3D(0.0, 0.0, 0.0),
                Vector3D(100.0, 100.0, 0.0),
                *hermiteSpline(
                    Vector3D(100.0, 100.0, 0.0),
                    Vector3D(50.0, 50.0, 0.0),
                    Vector3D(150.0, 50.0, 0.0),
                    Vector3D(0.0, -100.0, 0.0),
                    Vector3D(100.0, 0.0, 0.0),
                    Vector3D(-50.0, 50.0, 0.0)
                ).toTypedArray(),
                Vector3D(0.0, 100.0, 0.0),
                *hermiteSpline(
                    Vector3D(0.0, 100.0, 0.0),
                    Vector3D(-50.0, 50.0, 0.0),
                    Vector3D(-50.0, 50.0, 0.0),
                    Vector3D(0.0, -100.0, 0.0),
                    Vector3D(0.0, 0.0, 0.0),
                    Vector3D(50.0, 50.0, 0.0)
                ).toTypedArray())
            oneWay = true
            forwardLanesCount = 1
            backwardLanesCount = 0
        }
    }

    val simulation = trafficSimulation<Vehicle> {
        val wheelBase = 3.8
        val length = 4.0

        roadNetwork = network

        vehicles.add(
            Vehicle(
                Vector2D(0.0, 0.0), Vector2D.ZERO, 0.0, Vector2D.I, 0.0, wheelBase, length))

        vehicleBehavior = {vehicle, deltaTime -> basicVehicleBehavior(mapOf(), vehicle, deltaTime) }

        vehicleUpdate = { vehicle, action, deltaTime -> basicVehicleUpdate(vehicle, action, deltaTime) }
    }
}
