package minigames.gamemodes.skywars

import minigames.core.game.Game
import minigames.core.player.PlayerState
import minigames.core.world.GameWorld
import minigames.events.custom.PlayerFallDamageEvent
import minigames.events.custom.PlayerOpenChestEvent
import minigames.events.custom.PlayerVoidDamageEvent
import minigames.gamemodes.skywars.systems.chestfiller.ChestManager
import minigames.util.logging.getLogger
import org.bukkit.block.BlockType
import org.bukkit.block.Chest
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.PlayerDeathEvent

class SkywarsGame(gameWorld: GameWorld) : Game(gameWorld) {
    val chestManager = ChestManager()

    override fun registerEvents() {
        eventBus.register(PlayerDeathEvent::class) { e ->
            getLogger().info("PlayerDeathEvent: Player died from ${e.entity.lastDamageCause}")
            e.isCancelled = true
            handlePlayerDeath(e.player)
            this.broadcast("${e.player.name} was defeated by ${e.entity.name}")
        }
        eventBus.register(PlayerFallDamageEvent::class) { e ->
            getLogger().info("PlayerFallDamageEvent: Player took fall damage")
        }
        eventBus.register(PlayerVoidDamageEvent::class) { e ->
            getLogger().info("PlayerVoidDamageEvent: Player took damage")
            e.isCancelled = true
            handlePlayerDeath(e.player)
        }
        // Chest loot handling events
        eventBus.register(PlayerOpenChestEvent::class) { e ->
            getLogger().info("PlayerOpenChestEvent: Player opened chest at ${e.chest.location.x} ${e.chest.location.y} ${e.chest.location.z}")
            chestManager.onOpenChest(e.chest)
        }
        eventBus.register(BlockBreakEvent::class) { e ->
            getLogger().info("BlockBreakEvent: Block broken")
            val blockState = e.block.state
            if (blockState is Chest) {
                getLogger().info("Chest broken")
                chestManager.onBreakChest(blockState)
            }
        }
        eventBus.register(BlockExplodeEvent::class) { e ->
            getLogger().info("BlockExplodeEvent")
            e.blockList().forEach { block ->
                val blockState = block.state
                if (blockState is Chest) {
                    getLogger().info("Chest broken")
                    chestManager.onBreakChest(blockState)
                }
            }
        }
        eventBus.register(EntityExplodeEvent::class) { e ->
            getLogger().info("EntityExplodeEvent")
            e.blockList().forEach { block ->
                val blockState = block.state
                if (blockState is Chest) {
                    getLogger().info("Chest broken")
                    chestManager.onBreakChest(blockState)
                }
            }
        }
    }

    override fun onStart() {
        this.broadcast("SkyWars started in ${gameWorld.name}!")
        gameWorld.destroySpawns()
    }

    override fun onEnd() {
        this.broadcast("SkyWars ended in ${gameWorld.name}!")
    }

    override fun checkWinCondition(): Boolean {
        val alive = getPlayers().filter { it.state == PlayerState.ALIVE }
        if (alive.size <= 1) {
            val winner = alive.firstOrNull()
            this.broadcast("Winner: ${winner?.bukkitPlayer?.name ?: "Nobody"}")
            return true
        }
        return false
    }
}