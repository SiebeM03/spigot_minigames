package minigames.gui.menus

import minigames.core.world.WorldType
import minigames.gui.inventory.InventoryGui
import minigames.plugin
import minigames.util.Color
import minigames.util.ItemBuilder
import minigames.util.ItemBuilder.lore
import minigames.util.ItemBuilder.name
import org.bukkit.Material
import org.bukkit.entity.Player

class GameSelectorMenu : InventoryGui("Game selector", 9) {
    init {
        setItem(2, ItemBuilder.item(Material.DIAMOND) {
            name("${Color.AQUA}START SKYWARS")
            lore("${Color.GRAY}Click to start a skywars game")
        }) { e ->
            val player = e.whoClicked as Player
            plugin.gameManager.getAvailableGame(WorldType.SKYWARS).thenAccept { game ->
                game.addPlayer(player)
            }
        }
        setItem(4, ItemBuilder.item(Material.RED_BED) {
            name("${Color.AQUA}START SKYWARS")
            lore("${Color.GRAY}Click to start a bedwars game")
        }) { e ->
            val player = e.whoClicked as Player
            plugin.gameManager.getAvailableGame(WorldType.BEDWARS).thenAccept { game ->
                game.addPlayer(player)
            }
        }

        setItem(6, ItemBuilder.item(Material.BARRIER) {
            name("${Color.RED}Close Menu")
        }) { e ->
            e.whoClicked.closeInventory()
        }
    }
}