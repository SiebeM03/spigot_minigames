package minigames.items.custom

import minigames.gui.menus.AdminMenu
import minigames.items.CustomItem
import minigames.plugin
import minigames.util.Color
import minigames.util.ItemBuilder
import minigames.util.ItemBuilder.lore
import minigames.util.ItemBuilder.name
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent

class AdminTool : CustomItem("admin_tool", ItemBuilder.item(Material.STICK) {
    name("${Color.AQUA}Admin tool")
    lore("${Color.GRAY}Click to open admin menu")
}) {
    override fun registerEvents() {
        eventBus.register(PlayerInteractEvent::class) { e ->
            plugin.guiManager.openGui(e.player, AdminMenu())
        }
    }
}