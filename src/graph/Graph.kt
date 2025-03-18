/**
 * =====================================================================
 * Undirected graph library (used for the map)
 * Implementation based off https://github.com/DmitryTsyvtsyn/Kotlin-Algorithms-and-Design-Patterns/blob/develop/src/main/kotlin/structures/Graph.kt
 * =====================================================================
 */

package graph

class Graph<T> {
    private val data = linkedMapOf<Node<T>, MutableList<Node<T>>>()

    data class Node<T>(val value: T)
}
