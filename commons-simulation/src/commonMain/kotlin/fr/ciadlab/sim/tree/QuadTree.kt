package fr.ciadlab.sim.tree

import fr.ciadlab.sim.AxisAlignedBoundingBox2D
import fr.ciadlab.sim.math.geometry.Vector2D

const val NORTH_EAST = 0
const val NORTH_WEST = 1
const val SOUTH_WEST = 2
const val SOUTH_EAST = 3
const val DEFAULT_CAPACITY = 10

/**
 * Quad-tree of objects with a 2D position
 * @param T the type of objects
 * @author Alexandre Lombard
 */
class QuadTree<T>(
    val boundingBox: AxisAlignedBoundingBox2D,
    val content: MutableList<T> = arrayListOf(),
    val children: MutableList<QuadTree<T>> = arrayListOf(),
    val parent: QuadTree<T>? = null,
    val capacity: Int = DEFAULT_CAPACITY,
    val position: (T)->Vector2D) {

    /**
     * Add a value to the quad-tree, eventually recreating children nodes if required
     * @return <code>true</code> if the value has been added, <code>false</code> if it is out of the scope of this tree
     */
    fun add(value: T): Boolean {
        val valuePosition = position(value)

        if(!this.boundingBox.contains(valuePosition)) {
            return false
        }

        // If there are children, try to add the point to the child
        if(children.isNotEmpty()) {
            // Look for the right child (avoid recursion)
            var currentChild = this.children[selectChild(valuePosition)]
            while(currentChild.children.isNotEmpty()) {
                currentChild = currentChild.children[currentChild.selectChild(valuePosition)]
            }
            currentChild.add(value)
        } else {
            // Otherwise, add it here
            this.content.add(value)

            // If we exceed the values, we split the current cell
            if(this.content.size >= this.capacity) {
                val childrenBoundingBoxes = boundingBox.divide()

                for(i in childrenBoundingBoxes.indices) {
                    children.add(
                        QuadTree(childrenBoundingBoxes[i], parent = this, capacity = capacity, position = position))
                }

                // We migrate "this" content to the children to avoid multiple references
                this.content.forEach { children[selectChild(position(it))].add(it) }

                this.content.clear()
            }
        }

        return true
    }

    /**
     * Removes a value from the quad-tree
     * @return <code>true</code> if the value has been successfully removed, <code>false</code> otherwise
     */
    fun remove(value: T): Boolean {
        if(this.isLeaf()) {
            return this.content.remove(value)
        }

        val valuePosition = position(value)
        var child = children[selectChild(valuePosition)]

        while (!child.isLeaf()) {
            child = child.children[child.selectChild(valuePosition)]
        }

        return child.remove(value)
    }

    /**
     * Fetches all the elements within the given bounding box
     * @return the elements inside the bounding box
     */
    fun fetchElements(boundingBox: AxisAlignedBoundingBox2D): List<T> {
        val results = ArrayList<T>()

        return results
    }

    /**
     * Tells whether this is a leaf or not
     * @return <code>true</code> if this is a leaf, <code>false</code> otherwise
     */
    fun isLeaf(): Boolean {
        return this.children.isEmpty()
    }

    /**
     * Computes and returns the number of leaves of the tree
     * @return the number of leaves of the tree
     */
    fun countLeaves(): Int {
        return if(this.children.isEmpty()) {
            1
        } else {
            this.children.map { it.countLeaves() }.sum()
        }
    }

    private fun selectChild(point: Vector2D): Int {
        if(point.x < boundingBox.center.x) {
            // West
            return if(point.y < boundingBox.center.y) {
                // North
                NORTH_WEST
            } else {
                // South
                SOUTH_WEST
            }
        } else {
            //East
            return if(point.y < boundingBox.center.y) {
                // North
                NORTH_EAST
            } else {
                // South
                SOUTH_EAST
            }
        }
    }
}
