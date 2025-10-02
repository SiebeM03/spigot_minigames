package minigames.gui.menus

import minigames.gui.inventory.InventoryGui
import minigames.gui.inventory.InventorySlot
import minigames.plugin
import minigames.util.ItemBuilder
import minigames.util.ItemBuilder.name
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player

class AdminMenu : InventoryGui("Admin menu", 45) {
    init {
        setItem(InventorySlot(2, 3), ItemBuilder.item(Material.EMERALD) {
            name("Start current game")
        }) { e ->
            val game = plugin.gameManager.getGameByPlayer(e.whoClicked as Player)
            if (game == null) {
                e.whoClicked.sendMessage(
                    Component.text(
                        "You must be in a game to be able to start it.",
                        NamedTextColor.RED
                    )
                )
            } else {
                game.start()
                e.inventory.close()
            }
        }
    }
}