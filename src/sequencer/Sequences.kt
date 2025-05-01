/**
 * =====================================================================
 * The sequencer contains the combat system and all scripted sequences.
 * =====================================================================
 */
package sequencer

import App
import map.Map
import java.util.*
import kotlin.system.exitProcess
import sleep

data class Attack(
    val name: String = "",
    val verb: String,
    val damage: Double,
    val cooldown: UByte = 0u,
)

class Enemy {
    lateinit var name: String
    lateinit var description: String
    var maxHealth: Double = 0.0
        set(value) {
            field = value
            health = value // Makes it so we only have to set maxHealth when initializing our enemy
        }
    var health: Double = 0.0

    val attacks = mutableListOf<Attack>()

    /**
     * Picks a random attack
     * (NOTE: enemies do not support attack cooldown)
     *
     * @return One of the enemy's attacks chosen at random
     */
    fun doTurn(): Attack = attacks.random()
}

// class instead of object because we need to pass in App
object Sequencer {
    /**
     * Starts a combat sequence between the player and a created Enemy
     *
     * @param enemy The enemy the player will fight
     * @throws exitProcess This function may stop the application of the user dies in combat
     */
    private fun combat(enemy: Enemy) {
            App.displayText = """
            ${App.name} versus ${enemy.name} - Begin
            ${enemy.name}: ${enemy.description}
            ${enemy.name}'s Health: ${enemy.health}/${enemy.maxHealth}
            
            ${App.name}'s turn:
            
            
        """.trimIndent()
        App.rerender()

        var turnNumber = 1
        while (true) {
            val move = App.getCombatAction()
            enemy.health = (enemy.health - move.damage).coerceAtLeast(0.0)
            App.primaryWeapon.cooldown[move] = move.cooldown
            App.displayText += "You ${move.verb}, dealing ${move.damage} damage.\n\n"
            App.rerender()
            sleep(1.0)

            if (enemy.health == 0.0) {
                break
            }

            App.displayText += "${enemy.name} [${enemy.health}/${enemy.maxHealth}]\n"
            App.rerender()
            val enemyMove = enemy.doTurn()
            App.takeDamage(enemyMove.damage)
            App.displayText += "${enemy.name} ${enemyMove.verb}, dealing ${enemyMove.damage} damage"
            App.rerender()
            sleep(3.0)

            if (App.health == 0.0) {
                App.displayTextAndReRender(
                    "Tragically, you have passed away due to your wounds in battle. Unfortunately, you do not magically come back to life in this world, so this is goodbye.",
                )
                sleep(3.0)
                exitProcess(0)
            }

            turnNumber++
            App.displayText = """
                ${App.name} versus ${enemy.name} - Turn $turnNumber
                ${enemy.name}'s Health: ${enemy.health}/${enemy.maxHealth}
                
                ${App.name}'s turn:
                
                
                """.trimIndent()

            // Lower the cooldown
            for ((key, value) in App.primaryWeapon.cooldown.toMap()) {
                val newValue = if (value > 0u) value - 1u else 0u
                if (newValue == 0u) {
                    App.primaryWeapon.cooldown.remove(key)
                } else {
                    App.primaryWeapon.cooldown[key] = newValue.toUByte()
                }
            }

            App.rerender()
        }

        App.giveExp(enemy.maxHealth)
        App.displayTextAndReRender("You have successfully defeated ${enemy.name}!")
        App.heal(Double.POSITIVE_INFINITY) // Fill the players health after they win
        sleep(3.0)
    }

    // Main story

    fun onboarding() {
        App.inSequence = true
        App.displayTextAndReRender("\"You there! What is your name!\" A guard shouts out, chasing after you.")
        App.name = App.getTextInput()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        App.displayTextAndReRender("\"${App.name} then, come with me.\" The guard says, leaving you no option but to follow.")
        App.displayTextAndReRender("You are dragged through the muddy streets of Balmoral, the stench of rot and failure heavy in the air.")
        App.displayTextAndReRender("The guard leads you into a crumbling stone keep, its once-proud banners now little more than tattered rags.")
        App.displayTextAndReRender("\"The captain will want a word with you,\" the guard grunts, shoving you into a dimly-lit hall.")
        App.displayTextAndReRender("A grizzled man with a face like a butchered hog glowers at you from behind a broken table.")
        App.displayTextAndReRender("\"Name's Captain Rourke. You look barely worth the lice on your scalp. Can you fight, ${App.name}?\"")
        App.getTextInput()

        App.displayTextAndReRender("\"Really?\" Rourke spits on the floor. \"We'll see about that.\"")
        App.displayTextAndReRender("Without warning, he tosses you a battered weapon. \"Prove to me that you can swing that without crying.\"")
        App.displayTextAndReRender("Captain Rourke squares off against you with his own — slightly less battered — weapon in hand")

        val rourke = Enemy()
        rourke.name = "Captain Rourke"
        rourke.description = "Captain of the Balmoral Guards. A man with a face like a butchered hog."
        rourke.maxHealth = 100.0
        rourke.attacks.add(Attack("", "swings his sword at you", 7.0))
        combat(rourke)

        App.displayTextAndReRender("\"Ah... yer tougher than you look young lad,\" Captain Rourke feebly snorts at you.")
        App.displayTextAndReRender("\"Well, whenever you're up for it, head to Northbury. Your talents will be wasted here, so be off,\" orders Rourke.")
        App.storyDestination = Map.northbury

        App.flags.add("onboarding")
        App.inSequence = false
        App.rerender()
    }

    fun mainlineNorthbury1() {
        App.inSequence = true
        App.storyDestination = null

        App.displayTextAndReRender("The road to Northbury is long and harsh.")
        App.displayTextAndReRender("Along the way, you notice something glistening embedded within a pine stump.")
        App.displayTextAndReRender("Upon closer inspection, it appears to be a lumber axe.")
        App.displayTextAndReRender("With no-one else in sight, you figure it's better than your sad excuse of a sword, so you take it.")
        App.primaryWeapon = Dictionary.northburyAxe
        App.displayTextAndReRender("Carrying your new weapon — you reach Northbury with only a few cuts and bruises.")
        App.displayTextAndReRender("The village is small, nestled between windswept hills and dark woods.")
        App.displayTextAndReRender("An old woman selling dried herbs beckons to you from her stall. \"You, stranger! You’ve the look of someone Captain Rourke would send.\"")
        App.displayTextAndReRender("\"My grandson, Arlen, went missing two nights past. He was dared by some village fools to enter the Hollow — a cursed grove east of here.\"")
        App.displayTextAndReRender("\"They say spirits dwell there, but I think it’s just wolves. Either way, he’s not come back. Please, find him.\"")
        App.displayTextAndReRender("You make your way to the edge of the Hollow, its trees blackened and twisted as though burned from the inside.")
        App.displayTextAndReRender("A faint whimper draws your attention — Arlen is cowering beneath a half-collapsed shrine, surrounded by the carcasses of small animals.")
        App.displayTextAndReRender("Before you can reach him, a skeletal figure wrapped in torn priest robes rises from the shadows.")

        val spirit = Enemy()
        spirit.name = "Wailing Cleric"
        spirit.description = "A tormented spirit bound to the ruined shrine of the Hollow."
        spirit.maxHealth = 85.0
        spirit.attacks.add(Attack("", "unleashes a piercing wail", 6.5))
        spirit.attacks.add(Attack("", "lashes out with spectral hands", 9.0))
        combat(spirit)

        App.displayTextAndReRender("The spirit dissipates into ash, and Arlen rushes to your side, shaking.")
        App.displayTextAndReRender("\"Thank you... I thought I was going to die here,\" he stammers.")
        App.displayTextAndReRender("You guide him back to Northbury, where the old woman weeps with relief.")
        App.displayTextAndReRender("\"Bless you, truly. Northbury owes you a debt.\" She pauses. \"If you're serious about helping people, you should travel to Dunmarch. They’ve had worse trouble than I can speak of.\"")
        App.displayTextAndReRender(Map.describeDirections(Map.getDirections(Map.northbury, Map.dunmarch)))
        sleep(2.0)
        App.storyDestination = Map.dunmarch

        App.flags.add("mainline_northbury_1")
        App.inSequence = false
        App.rerender()
    }

    fun mainlineDunmarchBarrow() {
        App.inSequence = true
        App.storyDestination = null
        App.displayTextAndReRender("Dunmarch welcomes you with cold stares and shuttered homes.")
        App.displayTextAndReRender("The mayor, a gaunt woman with ash-grey hair, greets you grimly. \"You’re the one from Northbury, yes? Good. We’ve lost six folk this month.\"")
        App.displayTextAndReRender("\"They vanish in the night. Always near the old barrow. And always after hearing... voices.\"")
        App.displayTextAndReRender("Locals avoid the barrow, but one old shepherd agrees to guide you halfway. \"No farther,\" he says, teeth chattering.")
        App.displayTextAndReRender("The hill is hollow beneath your feet. You find the entrance, overgrown and sealed by stones scratched with runes.")
        App.displayTextAndReRender("Inside, the air is cold and dry. Whispers echo in a tongue you do not know. Shapes move in the torchlight.")

        val wight = Enemy()
        wight.name = "Barrow Wight"
        wight.description =
            "A restless guardian of ancient rites, awakened by the stirrings of dark forces beneath Dunmarch."
        wight.maxHealth = 120.0
        wight.attacks.add(Attack("", "brushes your skin with a deathly chill", 10.0))
        wight.attacks.add(Attack("", "binds you in spectral roots", 12.0))
        combat(wight)

        App.displayTextAndReRender("As the wight crumbles, its last words hiss into your mind: *He stirs. The Drowned King wakes.*")
        sleep(1.0)

        App.displayTextAndReRender("You return to Dunmarch. The mayor listens, pale and silent. \"I feared this... There are older evils in Mirefen than we know.\"")

        App.displayTextAndReRender("A day after the barrow, a child runs through the rain to find you. \"Please... the mayor says to come quick.\"")
        App.displayTextAndReRender("At town hall, the mayor stands with a ragged map in hand. \"We've found something. An old ledger — mentions a place called Caer Detha. It's tied to the Drowned King.\"")
        App.displayTextAndReRender("\"We think it's in the Mirefen. Buried in muck and time, but still standing. If you’re willing, find it. And stop whatever is waking down there.\"")
        App.displayTextAndReRender("A trapper named Rilk guides you as far as the treeline. Beyond that, it’s just mist, bog, and the sound of things moving in water that shouldn't.")
        App.displayTextAndReRender("You wade through the Mirefen for hours, the fog thick enough to choke. Finally, dark stone rises from the swamp — Caer Detha.")

        App.displayTextAndReRender("Rotten banners cling to spiked towers. The gates groan open, untouched for centuries.")
        App.displayTextAndReRender("Inside, shadows twist unnaturally. The air is heavy with whispers. Something old is watching.")

        val drownedPriest = Enemy()
        drownedPriest.name = "Priest of the Drowned King"
        drownedPriest.description =
            "An ancient fanatic, half-flesh and half-brine, still preaching from his flooded altar."
        drownedPriest.maxHealth = 140.0
        drownedPriest.attacks.add(Attack("", "chants in a drowned tongue, causing the air to ripple", 11.0))
        drownedPriest.attacks.add(Attack("", "lashes you with tendrils of black water", 13.5))
        combat(drownedPriest)

        App.displayTextAndReRender("As the priest collapses, you glimpse a relic clutched in his hand — a jagged crown shaped from barnacles and bone.")
        App.displayTextAndReRender("Your fingers brush it... and a vision sears your mind: a throne beneath the sea, and eyes opening in the dark.")
        App.displayTextAndReRender("You stagger back to Dunmarch. The mayor waits. \"You found something, didn’t you? Gods help us... we're not ready.\"")
        App.displayTextAndReRender("\"We'll need reinforcements. Seek out Lord-Captian Vaerin from Stormhold, I'm sure he'll understand.\"")
        App.displayTextAndReRender(Map.describeDirections(Map.getDirections(Map.dunmarch, Map.stormhold)))
        sleep(2.0)
        App.storyDestination = Map.stormhold

        App.flags.add("main_dunmarch_barrow")
        App.inSequence = false
        App.rerender()
    }

    fun mainlineStormhold1() {
        App.inSequence = true
        App.storyDestination = null
        App.displayTextAndReRender("Stormhold rises before you — a wall of black stone perched over roaring waves, banners whipping in the salt wind.")
        App.displayTextAndReRender("Inside, armoured men drill in formation, their movements sharp and mechanical. A chill runs through you that has nothing to do with the sea breeze.")
        App.displayTextAndReRender("A guard halts you at the gates. \"State your business.\"")
        App.displayTextAndReRender("You deliver the message from Dunmarch, sealed with the mayor’s mark.")
        App.displayTextAndReRender("After a pause, he nods. \"The Lord-Captain will see you, but make no demands. His patience is thin.\"")

        App.displayTextAndReRender("Lord-Captain Vaerin, a broad-shouldered man with silver hair and a scar that bisects his brow, meets your gaze with visible disdain.")
        App.displayTextAndReRender("\"Dunmarch? Mirefen’s corpse of a town? Why should I send my men to die in a swamp?\"")
        App.displayTextAndReRender("He stands. \"Convince me. Our recruits are green and we need leaders. There’s a deserter in the old lighthouse — a skilled killer, but a coward. Bring him back alive.\"")

        App.displayTextAndReRender("The lighthouse looms on a cliff edge north of the docks, its beacon long extinguished.")
        App.displayTextAndReRender("Inside, you hear shifting metal. The deserter waits, cloaked in shadows.")

        val deserter = Enemy()
        deserter.name = "Vannic the Deserter"
        deserter.description = "A former sergeant of Stormhold, now wanted for abandonment. Paranoid, but dangerous."
        deserter.maxHealth = 95.0
        deserter.attacks.add(Attack("", "slashes with a jagged sabre", 7.5))
        deserter.attacks.add(Attack("", "throws blinding powder", 5.0))
        combat(deserter)

        App.displayTextAndReRender("Bleeding and cornered, Vannic snarls, \"Finish it — or drag me back in chains.\"")

        App.displayTextAndReRender("You return to Stormhold, Vannic in tow.")
        App.displayTextAndReRender("After defeating Vannic, he dropped his sabre, which you replace your axe with.")
        App.primaryWeapon = Dictionary.sabre

        App.displayTextAndReRender("Vaerin studies you for a long moment. \"You followed orders. You succeeded where others failed.\"")
        App.displayTextAndReRender("\"You’ll have your reinforcements. But know this — if the Drowned King rises, it won’t be steel that stops him.\"")
        App.displayTextAndReRender("\"At any rate, the reinforcements will take too long to get there at this rate,\" he mutters. \"But there may be a faster way.\"")
        App.displayTextAndReRender("\"Frostvale's watchtower went dark weeks ago. They maintain the northern signal lines — if we can restore it, we might alert other border garrisons.\"")
        App.displayTextAndReRender(Map.describeDirections(Map.getDirections(Map.stormhold, Map.frostvale)))
        App.storyDestination = Map.frostvale

        App.flags.add("mainline_stormhold_1")
        App.inSequence = false
        App.rerender()
    }

    fun mainlineFrostvaleSignal() {
        App.inSequence = true
        App.storyDestination = null
        App.displayTextAndReRender("You trudge through snow-laden forests and jagged passes. Frostmere is a windswept ruin perched over frozen cliffs.")
        App.displayTextAndReRender("Icicles hang like spears from broken beams. The tower’s brazier is cold and blackened.")
        App.displayTextAndReRender("Inside, frozen corpses in guard cloaks lie in unnatural poses — jaws stretched wide in silent screams.")
        App.displayTextAndReRender("A low growl rumbles through the frost-crusted beams...")

        val frostWraith = Enemy()
        frostWraith.name = "Frostwoken Beast"
        frostWraith.description =
            "A creature of cold and death, born from the broken pact between Frostmere’s sentinels and the spirits of the ice."
        frostWraith.maxHealth = 110.0
        frostWraith.attacks.add(Attack("", "slashes with frost-bitten claws", 9.0))
        frostWraith.attacks.add(Attack("", "lets out a chilling roar", 11.0))
        combat(frostWraith)

        App.displayTextAndReRender("The beast’s form dissipates into a cloud of ice and whispers. You ignite the brazier with your torch.")
        App.displayTextAndReRender("A long-dormant mirror flashes once, then pulses with light. Somewhere, someone is watching.")

        App.currentLocation = Map.stormhold
        App.displayTextAndReRender("You return to Stormhold. Vaerin nods solemnly.")
        App.displayTextAndReRender("\"That’ll buy us time. Whatever’s coming, they’ll know it’s not just Dunmarch in danger.\"")
        App.displayTextAndReRender("\"Next, you should head for Sunpeak. We're going to need more firepower if we are to deal with the Drowned King.\"")
        App.displayTextAndReRender(Map.describeDirections(Map.getDirections(Map.stormhold, Map.sunpeak)))
        App.storyDestination = Map.sunpeak

        App.flags.add("mainline_frostvale_signal")
        App.inSequence = false
        App.rerender()
    }

    fun mainlineSunpeakCouncil() {
        App.inSequence = true
        App.storyDestination = null
        App.displayTextAndReRender("Your arrival in Sunpeak is met with suspicion. Its gates open slowly, groaning like ancient stone guardians.")
        App.displayTextAndReRender("Guards in bronze-gilded helms escort you through sun-drenched plazas toward the High Spire, the seat of the Solar Council.")

        App.displayTextAndReRender("A cloaked aide intercepts you. \"You're expected, but do not expect agreement. The Council does not concern itself with lowland panic.\"")

        App.displayTextAndReRender("Inside the spire, robed councillors sit around a glowing sundial. The air is dry and silent.")
        App.displayTextAndReRender("\"You claim the dead stir in the Mirefen? That ancient rites are broken? We hear such tales every season,\" one scoffs.")

        App.displayTextAndReRender("Before you can respond, a booming tremor shakes the chamber. Dust falls from the ceiling.")
        App.displayTextAndReRender("A scout bursts through the door, bloodied and panicked. \"My lords — something tore through our western observatory. The priests are dead. The wards are broken!\"")

        App.displayTextAndReRender("You are ordered to ascend to the observatory and determine what happened.")

        App.displayTextAndReRender("You climb the sun-scorched spiral path to the upper peak. The observatory dome is split open, its golden mirrors shattered.")
        App.displayTextAndReRender("In its heart waits a towering silhouette wreathed in radiant flame — a being of ash and molten bone.")

        val solarAbomination = Enemy()
        solarAbomination.name = "Ashen Sentinel"
        solarAbomination.description =
            "A corrupted guardian of the Sunpeak observatory, now twisted by dark forces gnawing at the sky itself."
        solarAbomination.maxHealth = 130.0
        solarAbomination.attacks.add(Attack("", "lashes out with burning chains", 12.0))
        solarAbomination.attacks.add(Attack("", "erupts in searing light", 14.0))
        combat(solarAbomination)

        App.displayTextAndReRender("As the Sentinel crumbles, it screams in a voice not its own: *He rises... and the sun shall kneel before the mire.*")
        App.displayTextAndReRender("The council receives you in silence. Their faces are grave now.")

        App.displayTextAndReRender("\"Sunpeak will not fall as Dunmarch nearly did. We will prepare. And we will answer your call.\"")
        App.displayTextAndReRender("\"But there is a place you must go — a vault sealed since the First Dawn. In Glimmerpeak. If this truly is the Drowned King’s return, it may hold the key to stopping him.\"")
        App.displayTextAndReRender(Map.describeDirections(Map.getDirections(Map.sunpeak, Map.glimmerpeak)))
        App.storyDestination = Map.glimmerpeak

        App.flags.add("mainline_sunpeak_council")
        App.inSequence = false
        App.rerender()
    }

    fun mainlineGlimmerpeakVault() {
        App.inSequence = true
        App.storyDestination = null
        App.displayTextAndReRender("The journey to Glimmerpeak winds through frost-laced canyons and narrow cliffside paths.")
        App.displayTextAndReRender("As you approach, the city’s spires shimmer with an unnatural gleam — crystal towers catching what little light the sky gives.")

        App.displayTextAndReRender("The gates are sealed. Runes shimmer faintly across their surface. Only when you present the sun-branded sigil from Sunpeak do they open.")
        App.displayTextAndReRender("A priestess, old as stone and blind in both eyes, greets you. \"So the sun finally remembers its twin. Come. The vault awaits.\"")

        App.displayTextAndReRender("You are led through hollow caverns filled with ancient solar relics and murals depicting battles beneath shattered moons.")
        App.displayTextAndReRender("The Vault of First Dawn is hidden beneath the oldest chapel — a shaft descending into darkness lit only by lightstone veins.")

        App.displayTextAndReRender("Within the vault, you find a broken altar — once radiant, now dim. Glyphs mark it: *He was chained by oath and flame. Betrayed not by evil, but by mercy.*")

        App.displayTextAndReRender("You approach the relic — a sun-forged shard, still pulsing faintly. But as your hand nears, shadow erupts from behind.")

        val echo = Enemy()
        echo.name = "Echo of the Drowned"
        echo.description =
            "A memory given form — a flickering, hateful remnant of the Drowned King, bound within the vault as a final seal."
        echo.maxHealth = 140.0
        echo.attacks.add(Attack("", "lashes with incorporeal chains of grief", 11.0))
        echo.attacks.add(Attack("", "invokes the weight of sunken oaths", 13.5))
        combat(echo)

        App.displayTextAndReRender("When the echo fades, the altar reignites briefly. Words burn into your vision: *He was king, he was mourner, he was martyr.*")
        App.displayTextAndReRender("The priestess appears beside you, though you never saw her enter. \"Now you understand. The Drowned King was not born of shadow — he was made by it.\"")

        App.displayTextAndReRender("\"Return to Sunpeak. Tell them what stirs in the dark was once our guardian.\"")
        App.storyDestination =
            Map.sunpeak // Does not invoke the onVisited callback so we can do this before setting the flag

        App.flags.add("mainline_glimmerpeak_vault")
        App.inSequence = false
        App.rerender()
    }

    fun mainlineSunpeakToHallowforge() {
        App.inSequence = true
        App.storyDestination = null
        App.displayTextAndReRender("You return to Sunpeak bearing truths from Glimmerpeak — of the Drowned King’s fall and his original purpose.")
        App.displayTextAndReRender("The High Seer listens, lips thin. \"Then the old texts were right. He was our shield once... and we broke him.\"")

        App.displayTextAndReRender("\"We must reach Hallowforge. Their smith-priests alone possess the rite to sanctify the sun-shard you recovered.\"")
        App.displayTextAndReRender("She presses a sealed scroll into your hand. \"Take this to the Black Council. They’ll understand its urgency.\"")

        App.displayTextAndReRender("You leave Sunpeak by the east pass, the sun lost behind layers of cloud. The road winds downward into brambled valleys.")
        App.displayTextAndReRender("By dusk, you reach Ravenshollow — a quiet settlement hemmed in by towering trees and crooked ironwood fences.")

        App.displayTextAndReRender("At the tavern, a hooded woman beckons you. \"You're bound for Hallowforge? Don’t. They sealed the gates a month ago. No one returns.\"")
        App.displayTextAndReRender("\"But there’s an old path — the Emberreach — lost under the cliffs. Dangerous, but it bypasses the outer wall.\"")

        App.displayTextAndReRender("You thank her and rest briefly before setting out again under moonlight. The Emberreach is narrow and treacherous, carved by forgotten hands.")

        App.displayTextAndReRender("Eventually, you emerge within the walls of Hallowforge itself — a city of black basalt and molten crucibles, eerily silent.")
        App.displayTextAndReRender("Ash clings to every surface. There are signs of struggle — but no bodies, only dust, and twisted, empty armour.")

        App.displayTextAndReRender("Near the heart of the forge-city, you find the remains of a great brazier, cracked open. Shadows stir beyond it...")

        val fallenSmith = Enemy()
        fallenSmith.name = "Ashbound Forgemaster"
        fallenSmith.description =
            "Once Hallowforge’s grand artisan, now consumed by an inner fire — reforged as something hollow and vengeful."
        fallenSmith.maxHealth = 160.0
        fallenSmith.attacks.add(Attack("", "hurls searing molten slag", 13.0))
        fallenSmith.attacks.add(Attack("", "lashes with chains of white-hot iron", 15.0))
        combat(fallenSmith)

        App.displayTextAndReRender("As the Ashbound Forgemaster collapses, it gasps: *We quenched the flame with silence. But the silence screamed back.*")

        App.displayTextAndReRender("You recover the forge-seal from its broken hand. The sanctification chamber is unlocked — dim, but intact.")
        App.displayTextAndReRender("You place the sun-shard into its cradle. It pulses — then erupts in light. Sanctified. Whole. Awakened.")

        App.displayTextAndReRender("A voice — deeper than stone, older than kings — echoes through the chamber. *When fire is forgotten, shadow reigns. But light remembers.*")

        App.displayTextAndReRender("You leave Hallowforge with the shard now burning at your side — and a new fear: the silence in the forge was no accident.")
        App.currentLocation = Map.hallowforge

        App.displayTextAndReRender("Outside the forge chamber, you unseal the scroll given to you in Sunpeak. The ink glows faintly, warded against tampering.")
        App.displayTextAndReRender("The High Seer's words are terse: 'If the shard is sanctified, go to Ironhold. Their war-priests must bear witness and prepare the gates.'")
        App.storyDestination = Map.ironhold

        App.flags.add("mainline_hallowforge_sanctified")
        App.inSequence = false
        App.rerender()
    }

    fun mainlineIronholdSanctum() {
        App.inSequence = true
        App.storyDestination = null
        App.displayTextAndReRender("Ironhold rises from the crags like a blade driven into the stone — black walls streaked with soot, guarded by silent statues of ancestors past.")
        App.displayTextAndReRender("The air smells of ash and oil. Hammers ring in rhythm, and great forges belch steam into the snow-misted air.")
        App.displayTextAndReRender("A priest in bronze scale meets you at the gate. \"You're the one the High Seer sent? Come. The Flamefather waits.\"")

        App.displayTextAndReRender("You follow him through vast halls lined with molten runes, until you reach the inner sanctum — a great domed chamber with a crucible burning at its centre.")
        App.displayTextAndReRender("An elderly dwarf, beard braided with silver bands, steps forward. His eyes flicker to the shard. \"So. The relic stirs again.\"")

        App.displayTextAndReRender("\"We must act quickly. If the Drowned King rises, the Gate of Horn must be sealed anew. But we cannot do it alone.\"")
        App.displayTextAndReRender("\"Take the shard to the Anvil Choir. They'll prepare the Rite. In the meantime, we need to know if the tombs below have been breached.\"")

        App.displayTextAndReRender("You're handed a rusted iron key. \"Below Ironhold lies the Vault of Binding. None have entered since the Last Vigil. If the seals there are broken, we face more than prophecy — we face annihilation.\"")

        App.displayTextAndReRender("You descend through ancient stone steps, deeper and deeper until the warmth of the forge fades into silence.")
        App.displayTextAndReRender("At the vault doors, the runes are dim. Faint scratch marks suggest something *left*... not entered.")

        val fallen = Enemy()
        fallen.name = "Bound Ascendant"
        fallen.description =
            "A former hero of Ironhold, twisted by ancient pacts and now enslaved to the Drowned King's will."
        fallen.maxHealth = 130.0
        fallen.attacks.add(Attack("", "lashes out with a chain of molten iron", 11.0))
        fallen.attacks.add(Attack("", "screams a curse that tears at your mind", 13.5))
        combat(fallen)

        App.displayTextAndReRender("The creature crumbles in a burst of dust and memory. Something escaped... but not this one.")
        App.displayTextAndReRender("You return to the surface, shard in hand, and report to the Flamefather.")
        App.displayTextAndReRender("\"Then time is shorter than I feared. You must ride for cinderholm. If their watchers fall, the southern wards will fail next.\"")
        App.displayTextAndReRender(Map.describeDirections(Map.getDirections(Map.ironhold, Map.cinderholm)))
        App.storyDestination = Map.cinderholm

        App.flags.add("mainline_ironhold_sanctum")
        App.inSequence = false
        App.rerender()
    }

    fun mainlineCinderholmAshwake() {
        App.inSequence = true
        App.storyDestination = null
        App.displayTextAndReRender("Smoke coils over the horizon long before you reach Cinderholm — not from fire or battle, but from the volcanic fields that birthed the town’s name.")
        App.displayTextAndReRender("Built atop shifting basalt and ash-choked ravines, Cinderholm is a fortress of dark stone, its walls reinforced with obsidian and fear.")
        App.displayTextAndReRender("The air stings your lungs. The heat is unnatural — too intense even for the region’s famed forges.")

        App.displayTextAndReRender("A woman in scaled robes approaches you, face covered in soot. \"You're from Ironhold? Good. The Ashwake has begun. The Emberfold tremble, and something beneath them calls to the shard you carry.\"")

        App.displayTextAndReRender("\"The Emberfold were once sealed by runebinders. But the wards cracked this morning. We sent in a scouting party — none returned. If the source of this corruption takes hold here, it’ll spread across the lowlands.\"")

        App.displayTextAndReRender("You descend into the Emberfold — a tangled underworld of molten rivers, obsidian spires, and whispering heat. Flickers of movement dart through the haze.")

        val ashbound = Enemy()
        ashbound.name = "Ashbound Colossus"
        ashbound.description =
            "A towering elemental formed of lava, obsidian, and cursed runes — drawn to the shard like a moth to flame."
        ashbound.maxHealth = 145.0
        ashbound.attacks.add(Attack("", "hurls molten stone in an arcing blaze", 12.0))
        ashbound.attacks.add(Attack("", "slams the ground, sending a shockwave of fractured heat", 14.5))
        combat(ashbound)

        App.displayTextAndReRender("As the colossus falls, the shard pulses faintly — stronger now, but no less ominous. You sense it no longer hides from what hunts it.")
        App.displayTextAndReRender("Returning to Cinderholm, the robed woman kneels by a cracked wardstone. \"It’s awakening. The Drowned King no longer waits. He *moves*.\"")

        App.displayTextAndReRender("\"You must reach Thornfall next. They still keep the oldest codices. If there is any hope left, it lies buried in their catacombs.\"")
        App.displayTextAndReRender(Map.describeDirections(Map.getDirections(Map.cinderholm, Map.thornfall)))
        App.storyDestination = Map.thornfall

        App.flags.add("mainline_cinderholm_ashwake")
        App.inSequence = false
        App.rerender()
    }

    fun mainlineThornfallReckoning() {
        App.inSequence = true
        App.storyDestination = null
        App.displayTextAndReRender("Thornfall lies cloaked in mist and mourning. Black banners hang from withered trees, and the air tastes of iron and rain.")
        App.displayTextAndReRender("Once a stronghold of knights and holy banners, the village now sits quiet — like the breath before a scream.")

        App.displayTextAndReRender("You are met by a grim-faced sentinel in cracked armour. \"You're too late,\" she says flatly. \"The Black Herald has already come. He bears a blade that cuts through sanctity itself.\"")

        App.displayTextAndReRender("\"He waits in the ruined chapel atop Thornfall’s spine — where the first oath to the Drowned King was sworn. If you truly carry the shard, he’ll come for you.\"")

        App.displayTextAndReRender("As you ascend the twisted path, wind howls between the stones. The chapel looms like a broken tooth, and at its altar stands a knight in darkened mail, helm crowned with a rusted diadem.")

        val herald = Enemy()
        herald.name = "The Black Herald"
        herald.description =
            "Chosen champion of the Drowned King, bound to a cursed oath and wielding a relic of ancient ruin."
        herald.maxHealth = 160.0
        herald.attacks.add(Attack("", "slashes at you with a blade that bleeds shadow", 14.0))
        herald.attacks.add(Attack("", "summons chains of oathlight to bind you in place", 11.5))
        combat(herald)

        App.displayTextAndReRender("The Black Herald collapses, black mist pouring from his wounds. His weapon — a curved sword etched with the names of forgotten kings — falls at your feet.")
        App.displayTextAndReRender("You take the blade. It’s warm, even in the rain, and heavier than it looks. It hums in time with the shard.")

        App.primaryWeapon = Dictionary.kingsbaneBlade
        App.displayTextAndReRender("A robed monk emerges from the mist, silent until the last breath of the Herald fades.")
        App.displayTextAndReRender("\"It is time. The final seal has broken. The Drowned King rises in Dunmarch, and there he must fall — or all will drown with him.\"")
        App.displayTextAndReRender(Map.describeDirections(Map.getDirections(Map.thornfall, Map.dunmarch)))
        App.storyDestination = Map.dunmarch

        App.flags.add("mainline_thornfall_reckoning")
        App.inSequence = false
        App.rerender()
    }

    fun mainlineFinaleDrownedKing() {
        App.inSequence = true
        App.storyDestination = null

        App.displayTextAndReRender("Dunmarch lies quiet beneath blackened skys. The marsh churns, uneasy. You stand at the edge of the town square as stormlight ripples over the waterlogged stones.")

        App.displayTextAndReRender("The mayor steps forward, her cloak dripping, face drawn. She offers no greeting — only a sodden scrap of vellum: *The tide comes from within.*")

        App.displayTextAndReRender("Old voices gather in the mist. Envoys from Thornfall, Stormhold, and Northbury speak of drowned paths, poisoned roots, and wells turned brackish. The signs are clear: the Drowned King has awakened.")

        App.displayTextAndReRender("An elder of Dunmarch speaks: \"Caer Detha stirs. Not as a fortress, but as a tomb seeking breath. He does not rage. He remembers.\"")

        App.displayTextAndReRender("You are given a choice: descend alone into the heart of Caer Detha, or lead a vanguard to breach its depths with you.")
        App.displayTextAndReRender("The sun-forged shard hums at your side, pulsing with quiet resolve. You choose to face him alone.")

        App.displayTextAndReRender("Caer Detha looms once more, half-sunken in the swamp. Fog rolls like surf. At its core lies the chamber you glimpsed in your visions — a throne of barnacles and rusted chains.")

        App.displayTextAndReRender("As you approach, the waters part. He rises.")
        App.displayTextAndReRender("The Drowned King — once a protector, now twisted by grief, oathbound and submerged for an age. His crown is fused to his skull. His voice is the roar of oceans: *You would unmake my sorrow?*")

        val drownedKing = Enemy()
        drownedKing.name = "The Drowned King"
        drownedKing.description =
            "Once a guardian of the realms, now a revenant lord of tide and shadow, burdened by endless mourning and the weight of broken pacts."
        drownedKing.maxHealth = 500.0
        drownedKing.attacks.add(Attack("", "raises a tidal wave of memories, drowning thought and breath", 15.0))
        drownedKing.attacks.add(Attack("", "binds you with chains wrought from oaths never fulfilled", 18.0))
        drownedKing.attacks.add(Attack("", "summons wailing echoes of the drowned to claw at your will", 12.5))
        combat(drownedKing)

        App.displayTextAndReRender("As the King falls to one knee, the chamber trembles. The tide within him retreats.")
        App.displayTextAndReRender("*End it,* he whispers. *Or set me free.*")

        App.displayTextAndReRender("You raise the sun-shard. It pulses once — not with violence, but remembrance.")
        App.displayTextAndReRender("You press it to his chest. Light and water collide. Silence follows.")

        App.displayTextAndReRender("When the mist clears, only chains remain — broken, rusted, and still.")

        App.displayTextAndReRender("You emerge into grey dawn. The waters of Mirefen recede. The sky, for the first time in weeks, shows a pale sun.")

        App.displayTextAndReRender("Dunmarch lights its lanterns. Stormhold lowers its banners in salute. In Northbury, children sing again.")

        App.displayTextAndReRender("You are no longer just a traveller. You are the one who remembered. Who stood against the flood not with wrath, but with resolve.")

        repeat(3) {
            App.displayText += "\n."
            App.rerender()
            sleep(1.0)
        }

        App.displayText += "\nFIN"
        sleep(3.0)

        App.displayTextAndReRender(
            """
            You have reached the end of the story.
            
            The world will remain open for explanation.
            Peace has returned to this land. Aside from any missed side stories, there will not be any more enemies.
        """.trimIndent()
        )

        App.flags.add("mainline_finale_drowned_king")
        App.inSequence = false
        App.rerender()
    }

    // Side story

    fun sidequestBalmoralRatcatcher() {
        App.inSequence = true
        App.displayTextAndReRender("Back in Balmoral, you find the streets somehow filthier than before.")
        App.displayTextAndReRender("A wiry man in patched leathers grabs your arm. \"You look handy. Rats. Too many of them. Got into the grain stores.\"")
        App.displayTextAndReRender("\"Kill a few and I’ll see you get a proper meal, eh? Better than the captain feeds ya.\"")
        App.displayTextAndReRender("You descend into the storehouse cellar. The air reeks of rot. Red eyes blink from the dark.")

        val ratKing = Enemy()
        ratKing.name = "Bloated Rat King"
        ratKing.description = "A massive, diseased rat swollen on grain and filth, flanked by smaller vermin."
        ratKing.maxHealth = 55.0
        ratKing.attacks.add(Attack("", "lunges at you with yellowed teeth", 5.0))
        ratKing.attacks.add(Attack("", "calls nearby rats to bite and claw", 8.0))
        combat(ratKing)

        App.displayTextAndReRender("You return, stinking of rat blood and spoiled wheat.")
        App.displayTextAndReRender("\"Dead, is it? Good.\" The ratcatcher drops a wrapped bundle into your hand. \"Roast hare and some coin. Balmoral thanks you... quietly.\"")

        App.flags.add("sidequest_balmoral_ratcatcher")
        App.inSequence = false
        App.rerender()
    }

    fun sidequestWhisperingGladeGolem() {
        App.inSequence = true
        App.displayTextAndReRender("The Whispering Glade lies east of Thornfall — a forest hollow where wind speaks through the trees and spirits linger like mist.")

        App.displayTextAndReRender("As you step beneath the hanging moss, the hush of the glade is broken by deep grinding — stone against stone. Vines slither aside to reveal a hulking figure: a mossbound golem, awakened from ancient slumber.")

        App.displayTextAndReRender("It bears no malice, only duty — a silent warden bound to defend the glade. The shard you carry thrums with unsettling resonance, as if it recognises the creature.")

        val golem = Enemy()
        golem.name = "Warden of the Glade"
        golem.description =
            "An ancient golem overgrown with moss and runes, guarding the forest’s heart with tireless resolve."
        golem.maxHealth = 350.0
        golem.attacks.add(Attack("", "swings a heavy limb of root-entwined stone", 6.0))
        golem.attacks.add(Attack("", "stomps the ground, sending a tremor through the glade", 4.5))
        combat(golem)

        App.displayTextAndReRender("The golem crumbles, not from hatred or pain, but as if its task is finally complete. A faint whisper follows its fall — not words, but relief.")

        App.flags.add("sidequest_whispering_glade_golem")
        App.inSequence = false
        App.rerender()
    }
}
