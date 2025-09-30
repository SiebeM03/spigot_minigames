package minigames.gui.menus

import minigames.gui.inventory.InventoryGui
import minigames.gui.inventory.InventorySlot
import minigames.util.ItemBuilder
import minigames.util.ItemBuilder.name
import org.bukkit.Material

class AdminMenu: InventoryGui("Admin menu", 45) {
    init {
        setItem(InventorySlot(2, 3), ItemBuilder.item(Material.EMERALD) {
            name("Create world")
        })
    }
}