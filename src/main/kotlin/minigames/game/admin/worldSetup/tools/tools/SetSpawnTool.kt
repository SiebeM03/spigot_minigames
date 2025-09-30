package minigames.game.admin.worldSetup.tools.tools

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import minigames.game.admin.worldSetup.WorldSetup
import minigames.game.admin.worldSetup.tools.SetupTool
import minigames.util.Color

class SetSpawnTool : SetupTool {
    override val item: ItemStack = ItemStack(Material.EMERALD).apply {
        itemMeta = itemMeta?.apply {
            displayName(Component.text("${Color.GREEN}Set Spawn Point"))
            lore(
                listOf(
                    Component.text("${Color.GRAY}Left-click to add a spawn location"),
                    Component.text("${Color.GRAY}Right-click to remove a spawn location")
                )
            )
        }
    }

    override fun handleLeftClick(
        event: PlayerInteractEvent,
        worldSetup: WorldSetup
    ) {
        val loc = event.clickedBlock?.location ?: return
        if (!worldSetup.data.spawns.contains(loc)) {
            worldSetup.data.spawns.add(loc)
            event.player.sendMessage("${Color.GREEN}[Setup] Spawn point set at ${loc.blockX}, ${loc.blockY}, ${loc.blockZ}")
        } else {
            event.player.sendMessage("${Color.RED}[Setup] Spawn point at ${loc.blockX}, ${loc.blockY}, ${loc.blockZ} already exists")
        }
    }

    override fun handleRightClick(
        event: PlayerInteractEvent,
        worldSetup: WorldSetup
    ) {
        val loc = event.clickedBlock?.location ?: return
        if (worldSetup.data.spawns.contains(loc)) {
            worldSetup.data.spawns.remove(loc)
            event.player.sendMessage("${Color.RED}[Setup] Spawn point removed at ${loc.blockX}, ${loc.blockY}, ${loc.blockZ}")
        } else {
            event.player.sendMessage("${Color.RED}[Setup] No spawn point found at ${loc.blockX}, ${loc.blockY}, ${loc.blockZ}")
        }
    }
}