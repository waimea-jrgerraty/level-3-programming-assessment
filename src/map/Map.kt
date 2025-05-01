/**
 * =====================================================================
 * This file hold all the map data, and serves as a wrapper for the map graph.
 * Contains a breadth-first search pathing algorithm to find the quickest route between two locations on the map.
 * Also contains a helper method to turn a path into a descriptive string.
 * =====================================================================
 */
package map

import graph.Graph
import sequencer.Sequencer
import java.util.*

val map = Graph<Location>()

class Location(val name: String) {
    lateinit var description: String
        internal set

    // Callback to handle things when the player enters this location
    var onVisited: (() -> Unit)? = null

    init {
        map.addNode(this)
    }

    /**
     * @return A list of locations connected to `location`
     */
    fun getAvailableDestinations(): List<Location> = map.getConnectedNodes(this)

    /**
     * Creates an undirected connection between this and another location
     *
     * @param other Other location to add an undirected connection between
     */
    internal fun connectToOther(other: Location) {
        map.addEdge(this, other)
    }
}

// Singleton that stores the entire game map, and some helper methods
object Map {
    // Initialise all locations
    val balmoral = Location("Balmoral")
    val northbury = Location("Northbury")
    val dunmarch = Location("Dunmarch")
    val stormhold = Location("Stormhold")
    val greenway = Location("Greenway")
    val mornhaven = Location("Mornhaven")
    val frostvale = Location("Frostvale")
    val westbrook = Location("Westbrook")
    val brackenridge = Location("Brackenridge")
    val ravenshollow = Location("Ravenshollow")
    val ironhold = Location("Ironhold")
    val whisperingGlade = Location("Whispering Glade")
    val silverbrook = Location("Silverbrook")
    val thornfall = Location("Thornfall")
    val hallowforge = Location("Hallowforge")
    val sunpeak = Location("Sunpeak")
    val mudholt = Location("Mudholt")
    val goldwater = Location("Goldwater")
    val elderstone = Location("Elderstone")
    val glimmerpeak = Location("Glimmerpeak")
    val dreadmarsh = Location("Dreadmarsh")
    val cinderholm = Location("Cinderholm")
    val brightwater = Location("Brightwater")

    init {
        // Establish connections
        balmoral.connectToOther(northbury)
        balmoral.connectToOther(dunmarch)
        balmoral.connectToOther(greenway)
        balmoral.connectToOther(ironhold)
        balmoral.connectToOther(ravenshollow)
        balmoral.connectToOther(whisperingGlade)

        northbury.connectToOther(westbrook)
        northbury.connectToOther(mornhaven)
        northbury.connectToOther(frostvale)
        northbury.connectToOther(silverbrook)

        dunmarch.connectToOther(goldwater)
        dunmarch.connectToOther(ravenshollow)
        dunmarch.connectToOther(whisperingGlade)
        dunmarch.connectToOther(cinderholm)

        stormhold.connectToOther(greenway)
        stormhold.connectToOther(whisperingGlade)
        stormhold.connectToOther(mornhaven)
        stormhold.connectToOther(sunpeak)

        frostvale.connectToOther(ravenshollow)
        frostvale.connectToOther(whisperingGlade)
        frostvale.connectToOther(goldwater)

        brackenridge.connectToOther(silverbrook)
        brackenridge.connectToOther(westbrook)
        brackenridge.connectToOther(ravenshollow)

        ironhold.connectToOther(cinderholm)
        ironhold.connectToOther(brightwater)
        ironhold.connectToOther(westbrook)

        mudholt.connectToOther(ravenshollow)
        mudholt.connectToOther(dreadmarsh)
        mudholt.connectToOther(goldwater)

        hallowforge.connectToOther(ironhold)
        hallowforge.connectToOther(ravenshollow)
        hallowforge.connectToOther(dreadmarsh)

        sunpeak.connectToOther(brackenridge)
        sunpeak.connectToOther(ravenshollow)

        silverbrook.connectToOther(mornhaven)
        silverbrook.connectToOther(mudholt)

        glimmerpeak.connectToOther(frostvale)
        glimmerpeak.connectToOther(brackenridge)
        glimmerpeak.connectToOther(sunpeak)

        cinderholm.connectToOther(brightwater)

        brightwater.connectToOther(westbrook)
        brightwater.connectToOther(ravenshollow)

        dreadmarsh.connectToOther(whisperingGlade)
        dreadmarsh.connectToOther(ravenshollow)

        elderstone.connectToOther(greenway)
        elderstone.connectToOther(ironhold)
        elderstone.connectToOther(brackenridge)

        thornfall.connectToOther(mudholt)
        thornfall.connectToOther(westbrook)
        thornfall.connectToOther(whisperingGlade)

        // Set up descriptions
        balmoral.description =
            "Balmoral is a crumbling medieval township, marred by squalor and neglect. Its crooked timber buildings sag under the weight of age and rot, while muddy, refuse-choked streets wind between them. Once a modest trading post, it is now little more than a haven for brigands, drunks, and the desperate."

        northbury.description =
            "Northbury is a bustling medieval town nestled between rolling green hills and dense, ancient forests. Surrounded by sturdy stone walls, it thrives as a centre of trade and craftsmanship. Its cobbled streets lead to a grand keep, a symbol of unity and defence against the wilds beyond."

        dunmarch.description =
            "Dunmarch is a crumbling frontier town perched on the edge of the Mirefen — a vast, mist-choked swamp whispered to swallow whole caravans. Once a thriving trade post, it now clings to survival amidst failing crops, vanishing settlers, and an old barrow mound feared by locals."

        stormhold.description =
            "Stormhold clings to jagged cliffs where waves crash violently below. Built around a naval fortress, the city has withstood pirates and storms alike. Its people are disciplined and weathered, and its shipyards are famed across the realm for crafting vessels both swift and strong."

        greenway.description =
            "Greenway lies in a quiet valley surrounded by flowering meadows and wooded hills. Known for its skilled herbalists and the enchanted grove nearby, it's a place of peace and mystery. Locals speak in hushed tones of spirits guarding the ancient oak deep within the glade."

        mornhaven.description =
            "Mornhaven is a deep gorge city carved directly into sheer rock walls. Its layered streets and stone homes cling to the cliffs, connected by narrow bridges. Traders and miners call it home, and rumours abound of a great dragon that once ruled the gorge's shadows."

        frostvale.description =
            "Frostvale endures in a land of ice and wind by a frozen lake. Its stone huts huddle beneath snow-covered peaks. Fishers ply the icy waters, while old myths speak of a beast beneath the glacier. The sun rarely shines here, and the silence is deep and still."

        westbrook.description =
            "Westbrook thrives where two rivers meet, its markets crowded and its piers alive with trade. A melting pot of cultures and customs, it is loud, lively, and lawless in parts. Flooding is common, but so too is opportunity for those willing to take risks."

        brackenridge.description =
            "Brackenridge nestles among towering forest giants, hidden beneath layers of green. Its people are quiet hunters and skilled craftsmen of wood. Strange lights flicker in the trees at night, and old tales claim the forest walks after sundown."

        ravenshollow.description =
            "Ravenshollow broods atop a cliff above a black swamp. Its people are quiet and pale, and the church bells toll often—sometimes with no hand to ring them. The mist here never lifts, and visitors seldom stay longer than a night."

        ironhold.description =
            "Ironhold squats at the foot of Mount Korad, its forges belching smoke day and night. Armoursmiths and weaponsmiths labour endlessly under the watchful eye of the Iron Council. The city is stern and fortified, with laws enforced through fear and steel."

        whisperingGlade.description =
            "Whispering Glade surrounds a placid lake cradled by deep woods. Birdsong fills the air by day, but the trees whisper at night. Locals commune with nature, and many here are druids or mystics. It's a peaceful place—yet not entirely untouched by magic."

        silverbrook.description =
            "Silverbrook gleams with wealth drawn from the nearby mines. Built beside a shining stream, its stone walls and silver spires reflect its prosperity. Bandits lurk in the hills, and the town’s guards are ever-watchful of its vaults and shipments."

        thornfall.description =
            "Thornfall lies choked by brambles and thorns, a village overrun by nature. Its people once fled a plague, and those who returned found twisted vines and hollow homes. The new settlers live cautiously, always clearing away the creeping growth."

        hallowforge.description =
            "Hallowforge stands upon ancient stone, home to the last of the sacred forgemasters. Magic lingers in the anvils and steel here sings when struck. Below the forge runs a forgotten tunnel—sealed long ago after something stirred in the deep."

        sunpeak.description =
            "Sunpeak shines atop a high ridge where the sun lingers longest. Its vineyards stretch for miles, and its scholars gather rare tomes in a mountaintop library. The air is thin but clean, and many come here to seek wisdom—or peace."

        mudholt.description =
            "Mudholt slumps in the heart of the Fenmarch, its streets sodden and its huts perched on stilts. The people are mud-covered and grim, but proud. Strange lights flicker in the bog, and whispers say the mud remembers every footprint."

        goldwater.description =
            "Goldwater is flush with miners and mercenaries, its riverbed glittering with gold flakes. Taverns outnumber temples, and law is bought. Yet the promise of riches draws thousands, and the town teeters on the edge of chaos and fortune."

        elderstone.description =
            "Elderstone lies hidden beneath a forest canopy, its moss-covered walls older than memory. Ancient stones mark places of power, and pilgrims come seeking wisdom. Few are allowed past the gate, and fewer still leave unchanged."

        glimmerpeak.description =
            "Glimmerpeak clings to the mountains, its mines rich with glowing gems. The caverns hum softly, and some say the crystals pulse with thought. The miners guard their secrets, and the town is lit with soft light even in darkest night."

        dreadmarsh.description =
            "Dreadmarsh is a sprawl of shacks in a fetid bog. Its people are outcasts, trappers, and those hiding from the world. Giant insects buzz overhead, and twisted trees grow eyes. Few come willingly—fewer leave without scars."

        cinderholm.description =
            "Cinderholm sits beneath a blackened sky where the volcano once roared. Now just ash-covered ruins, some still dwell in its outskirts. Smoke rises from cracks in the ground, and old magic stirs beneath the crust."

        brightwater.description =
            "Brightwater flourishes where river meets sea, its harbours filled with sails and spice. A city of colour and crime, it thrives on trade and risk. Pirates drink in its taverns, and gold flows like the tide—easy come, easier go."

        // Setup onVisited
        balmoral.onVisited = {
            if (App.flags.contains("mainline_northbury_1") && !App.flags.contains("sidequest_balmoral_ratcatcher")) {
                Sequencer.sidequestBalmoralRatcatcher()
            }
        }
        northbury.onVisited = {
            if (!App.flags.contains("mainline_northbury_1")) {
                Sequencer.mainlineNorthbury1()
            }
        }
        dunmarch.onVisited = {
            if (App.flags.contains("mainline_northbury_1") && !App.flags.contains("main_dunmarch_barrow")) {
                Sequencer.mainlineDunmarchBarrow()
            } else if (App.flags.contains("mainline_thornfall_reckoning") && !App.flags.contains("mainline_finale_drowned_king")) {
                Sequencer.mainlineFinaleDrownedKing()
            }
        }
        stormhold.onVisited = {
            if (App.flags.contains("main_dunmarch_barrow") && !App.flags.contains("mainline_stormhold_1")) {
                Sequencer.mainlineStormhold1()
            }
        }
        frostvale.onVisited = {
            if (App.flags.contains("mainline_stormhold_1") && !App.flags.contains("mainline_frostvale_signal")) {
                Sequencer.mainlineFrostvaleSignal()
            }
        }
        sunpeak.onVisited = {
            if (App.flags.contains("mainline_frostvale_signal") && !App.flags.contains("mainline_sunpeak_council")) {
                Sequencer.mainlineSunpeakCouncil()
            } else if(App.flags.contains("mainline_glimmerpeak_vault") && !App.flags.contains("mainline_hallowforge_sanctified")) {
                Sequencer.mainlineSunpeakToHallowforge()
            }
        }
        glimmerpeak.onVisited = {
            if (App.flags.contains("mainline_sunpeak_council") && !App.flags.contains("mainline_glimmerpeak_vault")) {
                Sequencer.mainlineGlimmerpeakVault()
            }
        }
        ironhold.onVisited = {
            if (App.flags.contains("mainline_hallowforge_sanctified") && App.flags.contains("mainline_ironhold_sanctum")) {
                Sequencer.mainlineIronholdSanctum()
            }
        }
        cinderholm.onVisited = {
            if (App.flags.contains("mainline_ironhold_sanctum") && !App.flags.contains("mainline_cinderholm_ashwake")) {
                Sequencer.mainlineCinderholmAshwake()
            }
        }
        thornfall.onVisited = {
            if (App.flags.contains("mainline_cinderholm_ashwake") && !App.flags.contains("mainline_thornfall_reckoning")) {
                Sequencer.mainlineThornfallReckoning()
            }
        }
        whisperingGlade.onVisited = {
            if (App.flags.contains("mainline_thornfall_reckoning") && !App.flags.contains("sidequest_whispering_glade_golem")) {
                Sequencer.sidequestWhisperingGladeGolem()
            }
        }
    }

    /**
     * Breadth-first search pathfinding algorithm used to find the shortest path between two locations on the map
     *
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

    /**
     * Helper method that converts a generated path into a string of instructions to follow that path
     *
     * @param path A path returned by getDirections
     * @return A string giving easy to follow instructions to get from one location to another
     */
    fun describeDirections(path: List<Location>?): String {
        if (path.isNullOrEmpty()) {
            return "No path could be found to the destination."
        }

        if (path.size == 1) {
            return "You are already at your destination: ${path[0].name}."
        }

        val directions = StringBuilder("To get from ${path.first().name} to ${path.last().name}, follow these steps:\n")
        for (i in 1 until path.size) {
            directions.append(" - Go from ${path[i - 1].name} to ${path[i].name}.\n")
        }

        return directions.toString().trim()
    }
}
