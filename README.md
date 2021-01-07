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

![Simulator screenshot](https://raw.githubusercontent.com/alexandrelombard/multiplatform-traffic-simulation/master/images/img.png)

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
