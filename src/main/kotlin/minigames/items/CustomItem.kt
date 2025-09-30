package minigames.items

import minigames.events.EventBus
import minigames.util.ItemBuilder.setCustomId
import org.bukkit.inventory.ItemStack

abstract class CustomItem(
    val id: String,
    val item: ItemStack
) {
    val eventBus = EventBus()

    init {
        item.setCustomId(id)
        registerEvents()
    }

    protected abstract fun registerEvents()
}