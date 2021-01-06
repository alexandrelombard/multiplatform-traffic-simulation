package fr.ciadlab.sim.ai

/**
 * Implementation of the Dijkstra algorithm to look for the shortest path between two vertices of a graph
 * @author Alexandre Lombard
 */
class DijkstraAlgorithm<Node> {
    /**
     * Finds the shortest path as a list of nodes
     * @param origin the source node
     * @param destination the destination node
     * @param availableNodes a function returning the available nodes from a given one
     * @param distance a function giving the distance between two nodes
     * @return the shortest path as a list of nodes
     */
    fun findShortestPath(
        origin: Node,
        destination: Node,
        availableNodes: (Node)->Collection<Node>,
        distance: (Node, Node)->Double): List<Node> {
        // Map of distances (from origin)
        val distances = hashMapOf<Node, Double>()
        // Set of explored nodes
        val exploredNodes = hashSetOf<Node>()

        val toExplore = arrayListOf<Pair<Node, Node>>()

        distances[origin] = 0.0

        toExplore.addAll(availableNodes(origin).map { Pair(it, origin) })

        while(!toExplore.isEmpty()) {
            val currentNode = toExplore.removeFirst()
            val sourceDistance = distances[currentNode.second]
            val currentNodeDistance = distance(currentNode.second, currentNode.first) + sourceDistance!!
        }


        TODO("Not yet implemented")
    }
}
