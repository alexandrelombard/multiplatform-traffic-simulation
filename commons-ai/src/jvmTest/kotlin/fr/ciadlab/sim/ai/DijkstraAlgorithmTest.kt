package fr.ciadlab.sim.ai

import org.junit.Test

class DijkstraAlgorithmTest {
    @Test
    fun findShortestPath() {
        val graph = Node("A", hashMapOf(
            Node(
                "B", hashMapOf(
                    Node("D", hashMapOf()) to 5.0,
                    Node("E", hashMapOf(Node("D", hashMapOf()) to 1.0)) to 2.0
                )
            ) to 1.0,
            Node(
                "C", hashMapOf(
                    Node("D", hashMapOf()) to 3.0
                )
            ) to 6.0
        ))

        val origin = graph
        val destination = Node("D", hashMapOf())

        val path = DijkstraAlgorithm.findShortestPath(
            origin, destination, availableNodes = { it.connectedNodes.keys }, distance = {a, b -> a.connectedNodes[b]!! })

        path!!.forEach {
            println(it.name)
        }
    }
}

/**
 * Basic representation of a graph
 */
data class Node(val name: String, val connectedNodes: Map<Node, Double>)
