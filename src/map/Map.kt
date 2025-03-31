package map

import graph.Graph

val map = Graph<Location>()

class Location(name: String) {
    private var _description = "Description uninitialized!" // Backing field to hold description
    // Public description property, effectively a val outside Map.kt due to internal setter
    var description: String
        get() = _description
        internal set(value) {
            _description = value
        }

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
        Balmoral.description = """
            Balmoral is a quaint, charming medieval town nestled in a lush valley, surrounded by towering forests and winding rivers. 
            Cobblestone streets wind through the heart of the town, where stone cottages with thatched roofs line the way. 
            The air is filled with the scent of fresh bread from local bakeries and the distant clang of blacksmiths shaping iron. 
            A grand stone castle overlooks the town, its towering spires a reminder of the town's noble past. 
            With a bustling market square and a vibrant community of artisans and traders, Balmoral exudes a timeless, peaceful atmosphere, 
            as if caught in the gentle embrace of history.
        """.trimIndent()
    }

    /**
     * @param location The location to check the connected locations for
     * @return A list of locations connected to `location`
     */
    fun getAvailableDestinations(location: Location): List<Location> {
        return map.getConnectedNodes(location)
    }

    /**
     * @param from The location to start at
     * @param to The destination
     * @return A list of locations to travel through to get to the destination. Or null if no path is found
     * Find a path from one location to another
     */
    fun getDirections(from: Location, to: Location): List<Location>? {

    }
}
