package minigames.core.game

import minigames.core.player.GamePlayer
import minigames.core.player.PlayerState
import minigames.core.world.GameWorld
import minigames.core.world.WorldType
import minigames.events.EventBus
import minigames.plugin
import minigames.util.Color
import minigames.util.Messaging
import minigames.util.TaskUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
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
    var startTime: Long? = null

    init {
        registerEvents()
    }

    // ---- Player management ----
    open fun addPlayer(bukkit: Player) {
        val gp = GamePlayer(bukkit)
        players[bukkit.uniqueId] = gp
        onPlayerJoin(gp)
        gameWorld.spawnPlayer(bukkit)

        if (!bukkit.isOp) {
            bukkit.inventory.clear()
        }

        players.forEach { it ->
            createScoreBoard(it.value.bukkitPlayer)
        }
    }

    open fun removePlayer(bukkit: Player) {
        val gp = players.remove(bukkit.uniqueId) ?: return
        onPlayerLeave(gp)
    }

    fun getPlayers(): Collection<GamePlayer> = players.values
    fun getGamePlayer(bukkit: Player): GamePlayer? = players[bukkit.uniqueId]

    // ---- Lifecycle ----
    fun start() {
        state = GameState.STARTING
        TaskUtil.runCountdown(
            20, 10,
            onTick = { t -> this.broadcast("${Color.YELLOW}Game staring in ${t}s") },
            onFinish = {
                state = GameState.PLAYING
                startTime = System.nanoTime()
                players.forEach { it ->
                    it.value.state = PlayerState.ALIVE
                }

                this.broadcast("${Color.GREEN}Go!")
                onStart()
            }
        )
    }

    fun end() {
        state = GameState.ENDED
        onEnd()
        TaskUtil.runTaskLater(20 * 5) {
        }
        TaskUtil.runCountdown(
            20, 5,
            onTick = { t -> this.broadcast("${Color.YELLOW}You will be teleported back to the lobby in ${t}s") },
            onFinish = {
                getPlayers().forEach {
                    it.teleportToLobby()
                }
                plugin.gameManager.endGame(uuid)
            }
        )
        eventBus.clear()
    }

    fun handlePlayerDeath(player: Player) {
        val gamePlayer = getGamePlayer(player) ?: return
        gamePlayer.state = PlayerState.ELIMINATED
        gamePlayer.onDeath()
        gamePlayer.addDeath()
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            player.teleport(Location(player.world, 0.0, 90.0, 0.0, 0.0f, 0.0f))
        }, 1)
        if (checkWinCondition()) {
            end()
        }
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
    protected open fun createScoreBoard(player: Player) {}
    protected open fun cleanupSubClass() {}
    protected open fun onPlayerJoin(player: GamePlayer) {}
    protected open fun onPlayerLeave(player: GamePlayer) {}
    abstract fun checkWinCondition(): Boolean
}