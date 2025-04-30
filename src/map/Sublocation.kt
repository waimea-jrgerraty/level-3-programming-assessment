package map

open class Sublocation(val name: String) {
    lateinit var description: String
        internal set
}

data class ShopItem(val name: String, val price: Double)

class Shop(name: String) : Sublocation(name) {
    // Using a backing field here so we get an immutable list when looking at the shop's items
    private val _items = mutableListOf<ShopItem>()
    val items: List<ShopItem> get() = _items

    internal fun addItem(item: ShopItem) {
        _items.add(item)
        _items.sortBy { it.price }
    }
}