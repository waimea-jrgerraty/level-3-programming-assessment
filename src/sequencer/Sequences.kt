package sequencer

import App
import java.util.*
import kotlin.system.exitProcess

// Utilities
fun sleep(t: Double) {
    Thread.sleep((t * 1000).toLong())
}

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
    var modifier: Double = 1.0

    val attacks = mutableListOf<Attack>()

    fun doTurn(): Attack = attacks.random()
}

class Sequencer(
    private val app: App,
) {
    fun combat(enemy: Enemy) {
        app.displayText =
            """
            |${app.name} versus ${enemy.name} - Begin
            |${enemy.name}'s Health: ${enemy.health}/${enemy.maxHealth}
            |
            |${app.name}'s turn:
            |
            |
            """.trimMargin()
        app.rerender()

        var turnNumber = 1
        while (true) {
            val move = app.getCombatAction()
            enemy.health = (enemy.health - move.damage).coerceAtLeast(0.0)
            app.primaryWeapon.cooldown[move] = move.cooldown
            app.displayText += "You ${move.verb}, dealing ${move.damage} damage.\n\n"
            app.rerender()
            sleep(2.0)

            if (enemy.health == 0.0) {
                break
            }

            app.displayText += "${enemy.name} [${enemy.health}/${enemy.maxHealth}]\n"
            app.rerender()
            sleep(2.0)
            val enemyMove = enemy.doTurn()
            app.takeDamage(enemyMove.damage)
            app.displayText += "${enemy.name} ${enemyMove.verb}, dealing ${enemyMove.damage} damage"
            app.rerender()
            sleep(3.0)

            if (app.health == 0.0) {
                app.displayTextAndReRender(
                    "Tragically, you have passed away due to your wounds in battle. Unfortunately, you do not magically come back to life in this world, so this is goodbye.",
                )
                sleep(3.0)
                exitProcess(0)
            }

            turnNumber++
            app.displayText =
                """
                |${app.name} versus ${enemy.name} - Turn $turnNumber
                |${enemy.name}'s Health: ${enemy.health}/${enemy.maxHealth}
                |
                |${app.name}'s turn:
                |
                |
                """.trimIndent()

            // Lower the cooldown
            for ((key, value) in app.primaryWeapon.cooldown.toMap()) {
                val newValue = if (value > 0u) value - 1u else 0u
                if (newValue == 0u) {
                    app.primaryWeapon.cooldown.remove(key)
                } else {
                    app.primaryWeapon.cooldown[key] = newValue.toUByte()
                }
            }

            app.rerender()
        }

        app.giveExp(enemy.maxHealth)
        app.displayTextAndReRender("You have successfully defeated ${enemy.name}!")
        sleep(3.0)
    }

    fun onboarding() {
        app.displayTextAndReRender(
            "\"You there! What is your name!\" A guard shouts out, chasing after you.",
        )
        app.name =
            app
                .getTextInput()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        app.displayTextAndReRender("\"${app.name} then, come with me.\" The guard says, leaving you no option but to follow.")
        sleep(0.5)

        app.displayTextAndReRender("You are dragged through the muddy streets of Balmoral, the stench of rot and failure heavy in the air.")
        sleep(1.0)

        app.displayTextAndReRender(
            "The guard leads you into a crumbling stone keep, its once-proud banners now little more than tattered rags.",
        )
        sleep(0.8)

        app.displayTextAndReRender("\"The captain will want a word with you,\" the guard grunts, shoving you into a dimly-lit hall.")
        sleep(0.6)

        app.displayTextAndReRender("A grizzled man with a face like a butchered hog glowers at you from behind a broken table.")
        sleep(0.7)

        app.displayTextAndReRender("\"Name's Captain Rourke. You look barely worth the lice on your scalp. Can you fight, ${app.name}?\"")
        app.getTextInput()

        app.displayTextAndReRender("\"Really?\" Rourke spits on the floor. \"We'll see about that.\"")
        sleep(0.5)

        app.displayTextAndReRender(
            "Without warning, he tosses you a battered weapon. \"Prove to me that you can swing that without crying.\"",
        )
        sleep(0.6)

        app.displayTextAndReRender("Captain Rourke squares off against you with his own — slightly less battered — weapon in hand")
        sleep(1.0)

        val rourke = Enemy()
        rourke.name = "Captain Rourke"
        rourke.description = "Captain of the Balmoral Guards. A man with a face like a butchered hog."
        rourke.maxHealth = 100.0
        rourke.attacks.add(Attack("", "swings his sword at you", 7.0))
        combat(rourke)
    }
}
