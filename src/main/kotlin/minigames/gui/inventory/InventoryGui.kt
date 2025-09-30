package minigames.gui.inventory

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

abstract class InventoryGui(
    private val title: String,
    private val size: Int
) {
    private val inventory: Inventory = Bukkit.createInventory(null, size, Component.text(title))
    private val actions: MutableMap<Int, (InventoryClickEvent) -> Unit> = mutableMapOf()

    protected fun setItem(slot: Int, item: ItemStack, onClick: (InventoryClickEvent) -> Unit = {}) {
        inventory.setItem(slot, item)
        actions[slot] = onClick
    }

    protected fun setItem(slot: InventorySlot, item: ItemStack, onClick: (InventoryClickEvent) -> Unit = {}) {
        inventory.setItem(slot.getIndex(), item)
        actions[slot.getIndex()] = onClick
    }

    fun open(player: Player) {
        player.openInventory(inventory)
    }

    fun handleClick(event: InventoryClickEvent) {
        if (event.clickedInventory == null || event.clickedInventory != inventory) return
        event.isCancelled = true // prevent taking/moving items
        actions[event.slot]?.invoke(event)
    }
}