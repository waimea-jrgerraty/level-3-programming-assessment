package map

import graph.Graph
import java.util.*

val map = Graph<Location>()

class Location(name: String) {
    lateinit var description: String
        internal set

    val sublocations = ArrayList<Sublocation>()

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
    val balmoral = Location("Balmoral")
    val northbury = Location("Northbury")

    init {
        // Establish connections
        balmoral.connectToOther(northbury)

        // Set up descriptions
        balmoral.description =
            "Barmoral is a crumbling medieval township, marred by squalor and neglect. Its crooked timber buildings sag under the weight of age and rot, while muddy, refuse-choked streets wind between them. Once a modest trading post, it is now little more than a haven for brigands, drunks, and the desperate, where law is a rumour and decay clings to every stone."
        northbury.description =
            "Northbury is a bustling medieval town nestled between rolling green hills and dense, ancient forests. Surrounded by sturdy stone walls, it thrives as a center of trade and craftsmanship, with merchants, blacksmiths, and farmers filling its cobbled streets. At its heart stands a grand market square, overlooked by a towering stone keep that serves as both a fortress and the seat of the local lord. The town’s people are hardy and industrious, bound together by tradition, faith, and a shared determination to weather both the harsh northern winters and the ever-present threats lurking beyond the town’s gates."

        // Add sublocations
        val bmBlacksmith = Shop("Blacksmith")
        bmBlacksmith.description = "A crowded, rundown blacksmith that you wonder how it hasn't burnt down yet. Most of the equipment you can see inside is dented up and rusty."
        bmBlacksmith.addItem(ShopItem("Rusty Shiv", 1.99))
        balmoral.sublocations.add(bmBlacksmith)
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
