package fr.ciadlab.sim.infrastructure.viewjs.controllers

import fr.ciadlab.sim.infrastructure.road
import fr.ciadlab.sim.infrastructure.roadNetwork
import fr.ciadlab.sim.math.algebra.Vector3D
import fr.ciadlab.sim.math.geometry.hermiteSpline

val circleShapedRoadNetworkModel = roadNetwork {
    val circleShapedRoad = road {
        points = listOf(
            Vector3D(0.0, 0.0, 0.0),
            Vector3D(100.0, 0.0, 0.0),
            *hermiteSpline(
                Vector3D(100.0, 0.0, 0.0),
                Vector3D(150.0, 50.0, 0.0),
                Vector3D(100.0, 100.0, 0.0),
                steps = 30
            ).toTypedArray(),
            Vector3D(100.0, 100.0, 0.0),
            Vector3D(0.0, 100.0, 0.0),
            *hermiteSpline(
                Vector3D(0.0, 100.0, 0.0),
                Vector3D(-50.0, 50.0, 0.0),
                Vector3D(0.0, 0.0, 0.0),
                steps = 30
            ).toTypedArray())
        oneWay = true
        forwardLanesCount = 1
        backwardLanesCount = 0
    }
}

val eightShapedRoadNetworkModel = roadNetwork {
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
                Vector3D(-50.0, 50.0, 0.0),
                steps = 30
            ).toTypedArray(),
            Vector3D(0.0, 100.0, 0.0),
            *hermiteSpline(
                Vector3D(0.0, 100.0, 0.0),
                Vector3D(-50.0, 50.0, 0.0),
                Vector3D(-50.0, 50.0, 0.0),
                Vector3D(0.0, -100.0, 0.0),
                Vector3D(0.0, 0.0, 0.0),
                Vector3D(50.0, 50.0, 0.0),
                steps = 30
            ).toTypedArray())
        oneWay = true
        forwardLanesCount = 1
        backwardLanesCount = 0
    }
}
