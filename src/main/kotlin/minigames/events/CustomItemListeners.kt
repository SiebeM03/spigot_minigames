package minigames.events

import minigames.items.CustomItem
import minigames.plugin
import minigames.util.logging.getLogger
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class CustomItemListeners : Listener {
    private val logger = getLogger()

    private fun getCustomItem(item: ItemStack): CustomItem? = plugin.customItemManager.getItem(item)

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        val customItem = getCustomItem(item) ?: return
        customItem.eventBus.post(event)
    }
}