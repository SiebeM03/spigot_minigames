package minigames.game.admin.worldSetup

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import minigames.plugin

class SetupListener : Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val setup = plugin.worldSetupManager.getSetupWizard(event.player) ?: return
        val item = event.item

        val setupTool = setup.tools.firstOrNull {
            it.item.isSimilar(item)
        }
        if (setupTool != null) {
            setupTool.handleInteract(event, setup)
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val setup = plugin.worldSetupManager.getSetupWizard(event.player) ?: return
        val world = event.block.world
        if (setup.world != world) return

        println("World changed")
        setup.data.isWorldChanged = true
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val setup = plugin.worldSetupManager.getSetupWizard(event.player) ?: return
        val world = event.block.world
        if (setup.world != world) return

        println("World changed")
        setup.data.isWorldChanged = true
    }
}
