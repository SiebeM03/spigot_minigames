package minigames.core.game

import minigames.core.player.GamePlayer
import minigames.core.world.GameWorld
import minigames.events.EventBus
import minigames.core.world.WorldType
import minigames.plugin
import minigames.util.Color
import minigames.util.Messaging
import minigames.util.TaskUtil
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.attribute.Attribute.MAX_HEALTH
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.Listener
import java.util.*

abstract class Game(
    val gameWorld: GameWorld,
) : Listener {
    protected val players: MutableMap<UUID, GamePlayer> = mutableMapOf()
    val uuid get() = gameWorld.gameId
    var state: GameState = GameState.WAITING_FOR_PLAYERS

    val mode: WorldType get() = gameWorld.worldType
    val isRunning get() = state == GameState.PLAYING
    val hasRoom get() = gameWorld.hasRoom
    val eventBus = EventBus()

    init {
        registerEvents()
    }

    // ---- Player management ----
    open fun addPlayer(bukkit: Player) {
        val gp = GamePlayer(bukkit)
        players[bukkit.uniqueId] = gp
        onPlayerJoin(gp)
        gameWorld.spawnPlayer(bukkit)
    }

    open fun removePlayer(bukkit: Player) {
        val gp = players.remove(bukkit.uniqueId) ?: return
        onPlayerLeave(gp)
    }

    fun getPlayers(): Collection<GamePlayer> = players.values
    fun getGamePlayer(bukkit: Player): GamePlayer? = players[bukkit.uniqueId]

    // ---- Lifecycle ----
    fun start() {
        if (isRunning) return
        state = GameState.PLAYING
        TaskUtil.runCountdown(
            20, 10,
            onTick = { t -> this.broadcast("${Color.YELLOW}Countdown: $t") },
            onFinish = {
                this.broadcast("${Color.GREEN}Go!")
                onStart()
            }
        )
    }

    fun end() {
        if (!isRunning) return
        state = GameState.ENDED
        onEnd()
        TaskUtil.runTaskLater(20 * 5) {
            getPlayers().forEach { it.teleportToLobby() }
        }
        eventBus.clear()
    }

    fun handlePlayerDeath(player: Player) {
        player.health = player.getAttribute(MAX_HEALTH)!!.value
        player.fallDistance = 0.0f
        player.gameMode = GameMode.SPECTATOR
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            player.teleport(Location(player.world, 0.0, 90.0, 0.0, 0.0f, 0.0f))
        }, 1)
    }

    fun cleanup() {
        players.clear()
        eventBus.clear()
        cleanupSubClass()
    }

    fun broadcast(message: String) {
        Messaging.broadcast(message, this.players.values.map(GamePlayer::bukkitPlayer).toList())
    }

    // ---- Hooks ----
    protected abstract fun registerEvents()
    protected abstract fun onStart()
    protected abstract fun onEnd()
    protected open fun cleanupSubClass() {}
    protected open fun onPlayerJoin(player: GamePlayer) {}
    protected open fun onPlayerLeave(player: GamePlayer) {}
    abstract fun checkWinCondition()
}