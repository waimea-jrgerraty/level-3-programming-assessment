package map

import graph.Graph
import java.util.*

val map = Graph<Location>()

class Location(name: String) {
    lateinit var description: String
        internal set

    init {
        map.addNode(this)
    }

    // Internal (usable in Map.kt but not Main.kt) wrapper for addEdge
    internal fun connectToOther(other: Location) {
        map.addEdge(this, other)
    }
}

// Singleton that stores the entire game map, and some helper methods
object Map {
    // Initialise all locations
    val Balmoral = Location("Balmoral")
    val Northbury = Location("Northbury")

    init {
        // Establish connections
        Balmoral.connectToOther(Northbury)

        // Set up descriptions
        Balmoral.description =
            "Balmoral is a quaint, charming medieval town nestled in a lush valley, surrounded by towering forests and winding rivers. Cobblestone streets wind through the heart of the town, where stone cottages with thatched roofs line the way. The air is filled with the scent of fresh bread from local bakeries and the distant clang of blacksmiths shaping iron. A grand stone castle overlooks the town, its towering spires a reminder of the town's noble past. With a bustling market square and a vibrant community of artisans and traders, Balmoral exudes a timeless, peaceful atmosphere, as if caught in the gentle embrace of history."
        Northbury.description =
            "Northbury is a bustling medieval town nestled between rolling green hills and dense, ancient forests. Surrounded by sturdy stone walls, it thrives as a center of trade and craftsmanship, with merchants, blacksmiths, and farmers filling its cobbled streets. At its heart stands a grand market square, overlooked by a towering stone keep that serves as both a fortress and the seat of the local lord. The town’s people are hardy and industrious, bound together by tradition, faith, and a shared determination to weather both the harsh northern winters and the ever-present threats lurking beyond the town’s gates."
    }

    /**
     * @param location The location to check the connected locations for
     * @return A list of locations connected to `location`
     */
    fun getAvailableDestinations(location: Location): List<Location> =
        map.getConnectedNodes(location)

    /**
     * @param from The location to start at
     * @param to The destination
     * @return A list of locations to travel through to get to the destination. Or null if no path
     *   is found Find a path from one location to another
     */
    fun getDirections(from: Location, to: Location): List<Location>? {
        if (from == to) {
            return listOf(from)
        }

        // Do a Breadth First Search to find the shortest path to location if possible
        val queue: Queue<Location> = LinkedList()
        val visited = mutableSetOf<Location>()
        val parent = mutableMapOf<Location, Location?>()

        // Enqueue the starting node
        queue.add(from)
        visited.add(from)
        parent[from] = null

        while (queue.isNotEmpty()) {
            val vertex = queue.poll()
            for (neighbour in map.getConnectedNodes(vertex)) {
                if (neighbour !in visited) {
                    visited.add(neighbour)
                    parent[neighbour] = vertex
                    queue.add(vertex)
                    if (neighbour == to) {
                        // Reconstruct the path

                        val path = mutableListOf<Location>()
                        var current: Location? = to

                        // Navigate up the parent tree to get the path from the end to the start
                        while (current != null) {
                            path.add(current)
                            current = parent[current]
                        }

                        // We reconstruct from the end to the start, so reverse it to get it the right way around
                        path.reverse()

                        // Double check the first node is the start
                        return if (path.first() == from) path else null
                    }
                }
            }
        }
        return null
    }
}
