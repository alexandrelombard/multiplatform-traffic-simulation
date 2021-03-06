rootProject.name = "multiplatform-traffic-simulation"
// Commons
include("commons-math")
include("commons-utils")
include("commons-physics")
include("commons-ai")
include("commons-simulation")
// Traffic simulation models
include("infrastructure-model")
include("car-model")
include("car-behavior")
include("v2x-simulation")
include("v2x-traffic-management")
include("traffic-simulation")
// View projects
include("view-javafx")
include("view-js")
