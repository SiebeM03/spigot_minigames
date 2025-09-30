package minigames.gui.inventory

class InventorySlot(
    val row: Int,
    val column: Int
) {
    fun getIndex(): Int {
        return row * 9 + column
    }
}