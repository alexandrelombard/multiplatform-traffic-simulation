rootProject.name = "multiplatform-traffic-simulation"
// Commons
include("commons-math")
include("commons-utils")
include("commons-physics")
// Traffic simulation models
include("infrastructure-model")
include("car-model")
include("car-behavior")
include("traffic-simulation")
// View projects
include("view-javafx")
include("view-js")
