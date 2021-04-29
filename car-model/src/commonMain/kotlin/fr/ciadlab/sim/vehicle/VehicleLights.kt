package fr.ciadlab.sim.vehicle

/**
 * Represents all the lights of a vehicle
 * @author Alexandre Lombard
 */
data class VehicleLights(
    val leftBlinker: LightState = LightState.OFF,
    val rightBlinker: LightState = LightState.OFF,
    val brake: LightState = LightState.OFF,
    val rear: LightState = LightState.OFF,
    val lowBeam: LightState = LightState.OFF,
    val highBeam: LightState = LightState.OFF
)
