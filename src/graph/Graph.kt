/**
 * =====================================================================
 * Undirected graph library (used for the map)
 * Implementation based off https://github.com/DmitryTsyvtsyn/Kotlin-Algorithms-and-Design-Patterns/blob/develop/src/main/kotlin/structures/Graph.kt
 * =====================================================================
 */

package graph

import java.util.LinkedHashSet
import java.util.LinkedList

/**
 * Represents an undirected graph data structure where nodes can be connected to each other by edges.
 * This implementation allows adding/removing nodes and edges, and performing depth-first and breadth-first traversal.
 * The graph is generic, allowing for the storage of any type of data as node values.
 *
 * @param T the type of the value stored in each node of the graph.
 */
class Graph<T> {
    // The type of value this graph uses is generic, so you can make a graph of anything
    // Our graph is a linked map of 'Nodes' which hold the value and a mutable list of edges, connections to other nodes
    private val data = linkedMapOf<Node<T>, MutableList<Node<T>>>()

    /**
     * Adds a new node to the graph with the given value.
     * If the node already exists, it will not be added.
     *
     * @param node the value of the node to be added to the graph.
     */
    fun addNode(node: T) {
        data.putIfAbsent(Node(node), mutableListOf())
    }

    /**
     * Removes a node and its associated edges from the graph.
     * All edges connecting to this node are also removed.
     *
     * @param node the value of the node to be removed.
     */
    fun removeNode(node: T) {
        val toRemove = Node(node)
        // Iterate through all values to remove this node from their edges
        data.values.forEach { list ->
            list.remove(toRemove)
        }
        // Remove this node from the graph
        data.remove(toRemove)
    }

    /**
     * Adds an edge between two nodes. If either node does not exist in the graph, it will be added.
     * The graph is undirected, meaning the edge will be bidirectional (both nodes will have a connection to each other).
     * This means the order of fromNode and toNode does not matter.
     *
     * @param fromNode the value of the starting node.
     * @param toNode the value of the destination node.
     */
    fun addEdge(
        fromNode: T,
        toNode: T,
    ) {
        // Create an edge between the two nodes
        val node1 = Node(fromNode)
        val node2 = Node(toNode)
        data[node1]?.add(node2)
        data[node2]?.add(node1)
    }

    /**
     * Removes an edge between two nodes if it exists.
     * The edge is bidirectional, so the connection is removed from both nodes.
     * This means the order of fromNode and toNode does not matter.
     *
     * @param fromNode the value of the starting node.
     * @param toNode the value of the destination node.
     */
    fun removeEdge(
        fromNode: T,
        toNode: T,
    ) {
        // Remove an edge between the two nodes (if it exists)
        val node1 = Node(fromNode)
        val node2 = Node(toNode)
        data[node1]?.remove(node2)
        data[node2]?.remove(node1)
    }

    /**
     * Returns a list of values of nodes connected to the given node.
     * If the node has no connections or does not exist, an empty list is returned.
     *
     * @param node the value of the node whose connected nodes are to be retrieved.
     * @return a list of node values connected to the input node.
     */
    fun getConnectedNodes(node: T): List<T> {
        // Returns a list of nodes connected to the input node
        // We have to map it.value since nodes have their real value stored in the value member
        // If the given node is not in the graph or has no connections, return an empty list
        return data[Node(node)]?.map { it.value } ?: emptyList()
    }

    /**
     * Performs a depth-first traversal of the entire graph starting from an arbitrary node.
     * This method explores nodes as deeply as possible before backtracking.
     * The traversal is performed in a recursive manner.
     *
     * @return a list of node values encountered during the depth-first traversal.
     */
    fun depthFirstTraverse(): List<T> {
        // Traverse the graph depth first and return a list of all nodes
        // Searches "down" nodes as far as possible instead of neighbouring nodes
        val head = data.keys.firstOrNull() ?: return emptyList() // If graph is empty, return early with an empty list

        // We use a linked hash set to store visited nodes, as it maintains order
        val visited = LinkedHashSet<T>()
        val queue = LinkedList<Node<T>>()
        queue.push(head) // enqueue the first node
        while (queue.isNotEmpty()) {
            val node = queue.pollFirst() // dequeue
            // Check if the current node has been visited yet
            if (!visited.contains(node.value)) {
                // If not, add it to visited and enqueue all edges of the node
                visited.add(node.value)
                queue.addAll(data[node] ?: emptyList())
            }
        }
        return visited.toList()
    }

    /**
     * Performs a breadth-first traversal of the entire graph starting from an arbitrary node.
     * This method explores all neighbors of a node before moving on to their neighbors.
     * The traversal is performed in a level-wise manner.
     *
     * @return a list of node values encountered during the breadth-first traversal.
     */
    fun breadthFirstTraverse(): List<T> {
        // Traverse the graph breadth first and return a list of all nodes
        // Searches neighbouring nodes to the current node, instead of exploring "down"
        val head = data.keys.firstOrNull() ?: return emptyList() // If graph is empty, return early with an empty list

        // We use a linked hash set to store visited nodes, as it maintains order
        val visited = LinkedHashSet<T>()
        val queue = LinkedList<Node<T>>()
        queue.push(head) // enqueue the first node
        visited.add(head.value) // The first node must be marked as visited here
        while (queue.isNotEmpty()) {
            val node = queue.pollFirst() // dequeue
            // Check all connected nodes
            data[node]?.forEach { connectedNode ->
                // Check if the current node has been visited yet
                if (!visited.contains(connectedNode.value)) {
                    // If not, add it to visited and enqueue all edges of the node
                    visited.add(connectedNode.value)
                    queue.addAll(data[connectedNode] ?: emptyList())
                }
            }
        }
        return visited.toList()
    }

    // A simple data class to hold node values
    data class Node<T>(
        val value: T,
    )
}
