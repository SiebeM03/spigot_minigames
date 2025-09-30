package minigames.gamemodes.skywars.systems.chestfiller

import minigames.util.logging.getLogger
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

class ChestManager {
    companion object {
        private val lootItems by lazy { LootItem.loadAllFromConfig() }
    }

    private val openedChestLocations: MutableList<Location> = mutableListOf();
    private fun hasBeenFilled(c: Chest): Boolean = c.location in openedChestLocations

    fun onOpenChest(chest: Chest) {
        if (!hasBeenFilled(chest)) {
            fillChest(chest)
            openedChestLocations.add(chest.location)
        }
    }

    fun onBreakChest(chest: Chest) {
        if (!hasBeenFilled(chest)) {
            fillChest(chest)

            val inventory = chest.blockInventory
            val world = chest.world
            inventory.contents.filterNotNull().forEach { item ->
                world.dropItemNaturally(chest.location, item)
            }
            inventory.clear()
        }
    }

    private fun fillChest(chest: Chest) {
        getLogger().info("Filling chest with loot")

        chest.inventory.clear()
        val random = ThreadLocalRandom.current()
        val usedItems = mutableSetOf<LootItem>()
        for (i in 0 until chest.inventory.size) {
            lootItems.random()
            val randomItem = lootItems[random.nextInt(lootItems.size)]
            if (usedItems.contains(randomItem)) continue
            usedItems.add(randomItem)

            if (randomItem.shouldFill(random)) {
                val itemStack = randomItem.make(random)
                chest.inventory.setItem(i, itemStack)
            }
        }
    }

    fun resetChests() {
        openedChestLocations.clear()
    }
}