package minigames.items

import minigames.util.ItemBuilder.getCustomId
import org.bukkit.inventory.ItemStack

class CustomItemManager {
    private val items = mutableMapOf<String, CustomItem>()

    fun register(item: CustomItem) {
        items[item.id] = item
    }

    fun getItem(item: ItemStack): CustomItem? {
        val id = item.getCustomId() ?: return null
        return items[id]
    }
}