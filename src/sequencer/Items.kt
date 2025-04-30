package sequencer

class Weapon(val name: String) {
    val moves = mutableListOf<Attack>()
    val cooldown = mutableMapOf<Attack, UByte>()
}

// Stores all items
object Dictionary {
    val batteredSword =
        Weapon("Battered Sword")
    val northburyAxe = Weapon("Deserted Axe")
    val sabre = Weapon("Sabre")
    val kingsbaneBlade = Weapon("Kingsbane Blade")

    init {
        batteredSword.moves.add(Attack("Swing", "swing the battered sword", 6.0))
        batteredSword.moves.add(Attack("Thrust", "thrust the sword at the enemy", 11.0, 2u))
        batteredSword.moves.add(
            Attack(
                "Tornado",
                "eccentrically swing the sword around yourself several times before hitting the enemy",
                30.0,
                5u
            )
        )

        northburyAxe.moves.add(Attack("Chop", "chop downward with the heavy axe", 8.0))
        northburyAxe.moves.add(
            Attack(
                "Cleaver's Arc",
                "swing the axe in a wide arc aimed at the enemy's midsection",
                14.0,
                2u
            )
        )
        northburyAxe.moves.add(
            Attack(
                "Splintering Blow",
                "slam the axe into the ground, sending a shockwave of splinters toward the enemy",
                26.0,
                4u
            )
        )
        northburyAxe.moves.add(
            Attack(
                "Executioner's Dance",
                "spin with brutal force, striking all nearby enemies with repeated cleaves",
                38.0,
                6u
            )
        )

        sabre.moves.add(Attack("Slash", "quickly slash with the sharp edge of the sabre", 7.0))
        sabre.moves.add(Attack("Riposte", "swiftly counter an incoming attack with a precise thrust", 12.0, 2u))
        sabre.moves.add(
            Attack(
                "Viper Strike",
                "deliver a lightning-fast thrust aimed at the enemy's vitals, leaving little room for defense",
                20.0,
                3u
            )
        )
        sabre.moves.add(
            Attack(
                "Whirlwind Dance",
                "spin gracefully, slashing all enemies within reach with fluid, elegant strikes",
                28.0,
                4u
            )
        )
        sabre.moves.add(
            Attack(
                "Fury of the Blade",
                "execute a series of rapid slashes, overwhelming the enemy with speed and precision",
                35.0,
                5u
            )
        )

        kingsbaneBlade.moves.add(
            Attack(
                "Shadow Rend",
                "cleave through the enemy with a strike that tears both flesh and soul, leaving a trail of shadow",
                15.0
            )
        )

        kingsbaneBlade.moves.add(
            Attack(
                "Crownbreaker",
                "deliver a crushing overhead strike meant to humble kings, infused with ancient spite",
                24.0,
                2u
            )
        )

        kingsbaneBlade.moves.add(
            Attack(
                "Umbral Chains",
                "lash out with tendrils of shadow that bind the enemy, draining their strength before the blade strikes",
                32.0,
                4u
            )
        )

        kingsbaneBlade.moves.add(
            Attack(
                "Thronecleaver",
                "unleash a devastating horizontal arc that carries the weight of fallen monarchs, disrupting all foes in its path",
                42.0,
                5u
            )
        )

        kingsbaneBlade.moves.add(
            Attack(
                "End of Rule",
                "invoke the blade’s full power in a single, sovereign-slaying blow — shadows surge as the blade becomes a silhouette of vengeance",
                60.0,
                7u
            )
        )
    }
}