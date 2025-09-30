package minigames.events

import io.papermc.paper.event.block.BlockBreakBlockEvent
import minigames.core.game.Game
import minigames.events.custom.PlayerFallDamageEvent
import minigames.events.custom.PlayerOpenChestEvent
import minigames.events.custom.PlayerVoidDamageEvent
import minigames.items.custom.AdminTool
import minigames.plugin
import minigames.util.logging.getLogger
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class GlobalListener : Listener {
    private val logger = getLogger()

    private fun getGame(player: Player): Game? = plugin.gameManager.getGameByPlayer(player)
    private fun getGame(world: World): Game? = plugin.gameManager.getGameByWorld(world)

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        plugin.lobby.addPlayer(event.player)
        if (event.player.isOp) {
            val adminTool = AdminTool()
            plugin.customItemManager.register(adminTool)
            event.player.give(adminTool.item)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        plugin.gameManager.getGameByPlayer(event.player)?.removePlayer(event.player)
    }

    @EventHandler
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        logger.info("Player damage event")
        val player = event.entity as Player
        if (player.world.uid == plugin.lobby.world.bukkitWorld.uid) {
            event.isCancelled = true
            return
        }
        when (event.cause) {
            EntityDamageEvent.DamageCause.FALL -> {
                logger.info("Player took fall damage! $event")
                val custom = PlayerFallDamageEvent(player, event.damage)
                if (!custom.callEvent()) event.isCancelled = true
            }

            EntityDamageEvent.DamageCause.VOID -> {
                val custom = PlayerVoidDamageEvent(player, event.damage)
                if (!custom.callEvent()) event.isCancelled = true
            }

            else -> {

            }
        }
    }

    @EventHandler
    fun onPlayerFallDamage(event: PlayerFallDamageEvent) {
        logger.info("Player ${event.player.name} took damage! Cause: ${event.cause}")
        val game = getGame(event.player) ?: return

        game.eventBus.post(event)
    }

    @EventHandler
    fun onPlayerVoidDamage(event: PlayerVoidDamageEvent) {
        logger.info("Player ${event.player.name} took damage! Cause: ${event.cause}")
        val game = getGame(event.player) ?: return

        game.eventBus.post(event)
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        logger.info("Player ${event.player.name} died!")
        val game = getGame(event.player) ?: return
        game.eventBus.post(event)
    }

    @EventHandler
    fun onInventoryOpen(event: InventoryOpenEvent) {
        logger.info("Opened inventory")
        if (event.player !is Player) return

        logger.info("Opened inventory2")
        val holder = event.inventory.holder
        if (holder is Chest) {
            logger.info("Opened inventory3")
            val custom = PlayerOpenChestEvent(event.player as Player, holder)
            Bukkit.getPluginManager().callEvent(custom)

            val game = getGame(custom.player) ?: return
            game.eventBus.post(custom)
        }
    }

    @EventHandler
    fun onBreakBlock(event: BlockBreakEvent) {
        val game = getGame(event.player) ?: return
        game.eventBus.post(event)
    }

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent) {
        val game = getGame(event.block.world) ?: return
        game.eventBus.post(event)
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        val game = getGame(event.location.world) ?: return
        game.eventBus.post(event)
    }
}