package fr.ciadlab.sim.car.behavior

interface DriverBehavior {
    fun apply(deltaTime: Double): DriverAction

    fun and(driverBehavior: DriverBehavior): DriverBehavior {
        val t = this
        val o = driverBehavior
        return object: DriverBehavior {
            override fun apply(deltaTime: Double): DriverAction {
                return t.apply(deltaTime).and(o.apply(deltaTime))
            }
        }
    }
}
