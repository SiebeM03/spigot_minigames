package minigames.game.admin.worldSetup.tools

import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import minigames.game.admin.worldSetup.WorldSetup

interface SetupTool {
    val item: ItemStack

    fun handleInteract(event: PlayerInteractEvent, worldSetup: WorldSetup) {
        when (event.action) {
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> handleRightClick(event, worldSetup)
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> handleLeftClick(event, worldSetup)
            else -> {}
        }
    }

    fun handleLeftClick(event: PlayerInteractEvent, worldSetup: WorldSetup)
    fun handleRightClick(event: PlayerInteractEvent, worldSetup: WorldSetup)
}