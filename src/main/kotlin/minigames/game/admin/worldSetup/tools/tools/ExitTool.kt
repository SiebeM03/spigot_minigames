package minigames.game.admin.worldSetup.tools.tools

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import minigames.game.admin.worldSetup.WorldSetup
import minigames.game.admin.worldSetup.tools.SetupTool
import minigames.plugin
import minigames.util.Color
import minigames.util.WorldUtil

class ExitTool : SetupTool {
    override val item: ItemStack = ItemStack(Material.BARRIER).apply {
        itemMeta = itemMeta?.apply {
            displayName(Component.text("${Color.GREEN}Exit world"))
            lore(listOf(Component.text("${Color.GRAY}Click to exit the current setup world, unsaved changes will be lost")))
        }
    }

    override fun handleLeftClick(
        event: PlayerInteractEvent,
        worldSetup: WorldSetup
    ) {
        handleClick(event, worldSetup)
    }

    override fun handleRightClick(
        event: PlayerInteractEvent,
        worldSetup: WorldSetup
    ) {
        handleClick(event, worldSetup)
    }

    private fun handleClick(
        event: PlayerInteractEvent,
        worldSetup: WorldSetup
    ) {
        val spawnLocation = Bukkit.getWorld("world")?.spawnLocation
        spawnLocation?.let { event.player.teleport(it) }

        WorldUtil.deleteWorld(worldSetup.world.name)

        worldSetup.admins.forEach { worldSetup.removeAdmin(it) }
        plugin.worldSetupManager.setupWorlds.remove(worldSetup)
    }
}