package minigames.gamemodes.skywars.systems.chestfiller

import minigames.config.YamlConfigLoader
import minigames.util.logging.getLogger
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ThreadLocalRandom

data class LootItem(
    private val material: Material,
    private val customName: String?,
    private val enchantmentToLevelMap: HashMap<Enchantment, Int>,
    private val chance: Double,
    private val minAmount: Int,
    private val maxAmount: Int
) {
    companion object {
        private val config = YamlConfigLoader.loadOrCreate("skywars")

        fun loadAllFromConfig(): List<LootItem> {
            val lootItemsSection = config.getConfigurationSection("loot_items") ?: return emptyList()
            return lootItemsSection.getKeys(false).mapNotNull { key ->
                val section = lootItemsSection.getConfigurationSection(key) ?: return@mapNotNull null
                fromConfig(section)
            }
        }

        private fun fromConfig(section: ConfigurationSection): LootItem {
            val material = try {
                Material.valueOf(section.getString("material") ?: throw IllegalStateException())
            } catch (e: Exception) {
                Material.AIR
            }
            val customName = section.getString("name")

            val enchantmentsSection = section.getConfigurationSection("enchantments")
            val enchantmentToLevelMap = hashMapOf<Enchantment, Int>()
            enchantmentsSection?.getKeys(false)?.forEach { enchantmentKey ->
                val enchantment = Enchantment.getByKey(
                    NamespacedKey.minecraft(
                        enchantmentKey.lowercase()
                    )
                )

                if (enchantment != null) {
                    val level = enchantmentsSection.getInt(enchantmentKey)
                    enchantmentToLevelMap.put(enchantment, level)
                }
            }
            val chance = section.getDouble("chance")
            val minAmount = section.getInt("min_amount")
            val maxAmount = section.getInt("max_amount")

            return LootItem(
                material = material,
                customName = customName,
                enchantmentToLevelMap = enchantmentToLevelMap,
                chance = chance,
                minAmount = minAmount,
                maxAmount = maxAmount
            )
        }
    }

    fun shouldFill(random: ThreadLocalRandom): Boolean =
        random.nextDouble() < chance

    fun make(random: ThreadLocalRandom): ItemStack {
        val amount = random.nextInt(minAmount, maxAmount + 1)
        val itemStack = ItemStack(material, amount)
        val meta = itemStack.itemMeta
        if (customName != null) {
            meta.displayName(Component.text(customName))
        }
        if (!enchantmentToLevelMap.isEmpty()) {
            enchantmentToLevelMap.forEach { enchantment, level ->
                meta.addEnchant(enchantment, level, true)
            }
        }
        itemStack.itemMeta = meta
        return itemStack
    }
}