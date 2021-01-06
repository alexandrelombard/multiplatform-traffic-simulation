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
     * @return the shortest path as a list of nodes or <code>null</code> if there is no path
     */
    fun findShortestPath(
        origin: Node,
        destination: Node,
        availableNodes: (Node)->Collection<Node>,
        distance: (Node, Node)->Double): List<Node>? {
        if(origin == destination) {
            return arrayListOf(origin)
        }

        // Map of steps (from origin)
        val steps = hashMapOf<Node, PathStep<Node>>()
        // Set of explored nodes
        val exploredNodes = hashSetOf<Node>()

        val toExplore = arrayListOf(origin)

        steps[origin] = PathStep(origin, origin, 0.0)

        while(!toExplore.isEmpty()) {
            val currentNode = toExplore.minByOrNull { steps[it]!!.distance }!!      // FIXME This is slow
            val sourceDistance = steps[currentNode]!!.distance
            val neighbours = availableNodes(currentNode).filter { !exploredNodes.contains(it) }

            toExplore.remove(currentNode)
            exploredNodes.add(currentNode)

            neighbours.forEach {
                val totalDistance = sourceDistance + distance(currentNode, it)
                val currentStep = steps[it]
                // Update the distance
                if(currentStep == null || currentStep.distance > totalDistance) {
                    steps[it] = PathStep(it, currentNode, totalDistance)
                }
            }

            toExplore.addAll(neighbours)
        }

        if(!steps.containsKey(destination)) {
            return null
        }

        val path = arrayListOf(destination)
        while(path.first() != origin) {
            path.add(0, steps[path.first()]!!.source)
        }

        return path
    }
}

/**
 * Represents a pair of nodes, associating a node with its source, along with the total distance
 */
internal data class PathStep<Node>(val node: Node, val source: Node, val distance: Double)
