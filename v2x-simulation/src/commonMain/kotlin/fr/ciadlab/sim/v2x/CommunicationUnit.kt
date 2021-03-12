package fr.ciadlab.sim.v2x

class CommunicationUnit(val onMessageReceived: MutableList<(V2XMessage)->Unit> = arrayListOf())
