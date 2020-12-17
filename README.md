# MTS

Multiplatform Traffic Simulation environment. Intended to train Machine Learning models.

The algorithms and models included in this repository have a live demo, just here:

http://alexandrelombard.github.io

## Modules

### Commons

- commons-utils: utility classes for multiplatform projects
- commons-math: general utilities for geometry
- commons-physics: general utilities for physics

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
