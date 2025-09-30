package minigames.util

import minigames.plugin
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.slf4j.event.Level

object ItemBuilder {
    fun item(material: Material, builder: ItemStack.() -> Unit = {}): ItemStack {
        val stack = ItemStack(material)
        stack.builder()
        return stack
    }

    fun ItemStack.setCustomId(id: String): ItemStack {
        val meta = itemMeta ?: return this
        val container = meta.persistentDataContainer
        container.set(NamespacedKey(plugin, "custom_item"), PersistentDataType.STRING, id)
        itemMeta = meta
        return this
    }

    fun ItemStack.getCustomId(): String? {
        val meta = itemMeta ?: return null
        val container = meta.persistentDataContainer
        return container.get(NamespacedKey(plugin, "custom_item"), PersistentDataType.STRING)
    }

    fun ItemStack.name(displayName: Component): ItemStack {
        val meta = itemMeta
        meta.displayName(displayName)
        itemMeta = meta
        return this
    }

    fun ItemStack.name(displayName: String): ItemStack {
        val meta = itemMeta
        meta.displayName(Component.text(displayName))
        itemMeta = meta
        return this
    }

    fun ItemStack.lore(vararg lines: Component): ItemStack {
        val meta = itemMeta
        meta.lore(lines.toList())
        itemMeta = meta
        return this
    }

    fun ItemStack.lore(vararg lines: String): ItemStack {
        val meta = itemMeta
        meta.lore(lines.map(Component::text).toList())
        itemMeta = meta
        return this
    }
}