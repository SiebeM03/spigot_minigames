package minigames.gui.inventory

import org.bukkit.entity.Player

class GuiManager {
    private val openGuis: MutableMap<Player, InventoryGui> = mutableMapOf()

    fun openGui(player: Player, inventoryGui: InventoryGui) {
        openGuis[player] = inventoryGui
        inventoryGui.open(player)
    }

    fun getGui(player: Player): InventoryGui? {
        return openGuis[player]
    }

    fun closeGui(player: Player) {
        openGuis.remove(player)
        player.closeInventory()
    }
}