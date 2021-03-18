# MTS

Multiplatform Traffic Simulation environment. Intended to train Machine Learning models.

Why another traffic simulation platform?
This one is:
- Multiplatform: can be run on the JVM, or can be compiled into Javascript to run in your browser; being written in Kotlin,
  it could theoretically be compiled to native code
- Provide a simulation DSL: this allows to describe the simulation environments and scenario with a convenient language
- It is designed following several Functional Programming principles, making the simulation *almost* thread-safe

The algorithms and models included in this repository have a live demo, just here:

http://alexandrelombard.github.io

Here's a quick example of an intersection, and the syntax of the code (a Kotlin DSL) used to generate it:

![Simulator screenshot](https://raw.githubusercontent.com/alexandrelombard/multiplatform-traffic-simulation/master/images/img.png)

```kotlin
roadNetwork {
    val road1 = road {
        points =
                hermiteSpline(
                    Vector3D(0.0, 0.0, 0.0),
                    Vector3D(100.0, 0.0, 0.0),
                    Vector3D(200.0, 100.0, 0.0),
                    Vector3D(100.0, 0.0, 0.0),
                    Vector3D(400.0, 0.0, 0.0),
                    Vector3D(100.0, 0.0, 0.0),
                    Vector3D(625.0, 50.0, 0.0),
                    Vector3D(100.0, 0.0, 0.0))
        oneWay = false
        forwardLanesCount = 3
        backwardLanesCount = 2
    }
    val road2 = road {
        points = listOf(
            Vector3D(650.0, 75.0, 0.0),
            Vector3D(650.0, 400.0, 0.0)
        )
        oneWay = false
        forwardLanesCount = 2
        backwardLanesCount = 2
    }
    val road3 = road {
        points = listOf(
            Vector3D(675.0, 50.0, 0.0),
            Vector3D(1000.0, 50.0, 0.0)
        )
        oneWay = false
        forwardLanesCount = 2
        backwardLanesCount = 2
    }

    intersection {
        withRoad(road1, ConnectedSide.DESTINATION)
        withRoad(road2, ConnectedSide.SOURCE)
        withRoad(road3, ConnectedSide.SOURCE)
    }
}
```

The language used to describe the road network and the simulation allows to easily create roads, intersections,
traffic lights, etc. and to place spawners, exit areas and other simulation elements.

It relies on a rather simple model for the behavior of the vehicle, but it proposes advanced models for lateral control,
longitudinal control, and more generally for the behavior of the driver.

## Modules

### Commons

- commons-utils: utility classes for multiplatform projects
- commons-math: general utilities for geometry
- commons-physics: general utilities for physics
- commons-simulation: general utilities for simulation (kd-tree, etc.)
- commons-ai: common artificial intelligence algorithms (A*, Dijkstra, etc.)

### Traffic simulation

- car-model: models of car for simulation
- car-behavior: algorithms for the control of the car
- infrastructure-model: environmental model of the infrastructure
- traffic-simulation: DSL for the generation of traffic simulation environments

### View

- view-javafx: viewer for simulations using JavaFX
- view-js: viewer for simulations using a JS canvas, to embed in a web-page

## Try it

Clone the project, let gradle configure it for you, then go to the view-javafx module and run the main function of
_SimulationLauncher.kt_

Remark: Java 15 required.

## Build notes

First, change the vehicleView image directory to the appropriate one.

Then, to produce a runnable JS file, in the *view-js* module, run the following:

```
gradle browserDevelopmentWebpack
```
