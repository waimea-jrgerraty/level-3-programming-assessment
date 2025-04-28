package sequencer

class Weapon(val name: String, val description: String) {
    val moves = mutableListOf<Attack>()
    val cooldown = mutableMapOf<Attack, UByte>()
}

// Stores all items
object Dictionary {
    val batteredSword =
        Weapon("Battered Sword", "A cheap sword given to you by Captain Rourke. It has definitely seen better days.")

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
    }
}