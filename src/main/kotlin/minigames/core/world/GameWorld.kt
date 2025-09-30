package minigames.core.world

import minigames.config.WorldConfig
import minigames.util.WorldUtil
import minigames.util.logging.getLogger
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.time.Instant
import java.util.*

/**
 * Wrapper around Bukkit.World that provides game-specific functionality
 */
class GameWorld(
    val template: WorldTemplate,
    val bukkitWorld: World,
    val gameId: UUID = UUID.randomUUID(),
    val createdAt: Instant = Instant.now()
) {
    private val logger = getLogger()

    private val players = mutableListOf<Player>()
    private val worldConfig = WorldConfig.loadFromYaml(template.name, bukkitWorld)
    private var isActive = true
    private var lastActivity = Instant.now()

    val name: String get() = bukkitWorld.name
    val worldType: WorldType get() = template.mode
    val playerCount: Int get() = players.size
    val allPlayers: Set<Player> get() = players.toSet()
    val hasRoom: Boolean get() = playerCount < template.maxPlayers
    val isWorldActive: Boolean get() = isActive
    val timeSinceLastActivity: Long get() = Instant.now().epochSecond - lastActivity.epochSecond
    val isLobby: Boolean get() = template.mode == WorldType.LOBBY


    fun addPlayer(player: Player): Boolean {
        if (!isActive) return false

        val success = players.add(player)
        if (success) {
            updateActivity()
            logger.info("Player ${player.name} joined world $name")
        }
        return success
    }

    fun removePlayer(player: Player): Boolean {
        val success = players.remove(player)
        if (success) {
            updateActivity()
            logger.info("Player ${player.name} left world $name")
        }
        return success
    }

    /** Teleports a player to this world */
    fun teleportPlayer(player: Player, location: Location? = null): Boolean {
        if (!isActive) return false

        val targetLocation = location ?: bukkitWorld.spawnLocation
        player.teleport(targetLocation)
        addPlayer(player)
        return true
    }

    /** Spawns a player at the next available spawn location */
    fun spawnPlayer(player: Player): Boolean {
        if (!isActive) return false
        if (playerCount >= template.maxPlayers) return false

        return teleportPlayer(player, worldConfig.spawns[playerCount])
    }

    /** Sets the world as inactive (game ended) */
    fun deactivate() {
        isActive = false
        updateActivity()
        logger.info("World $name deactivated")
    }

    /** Updates the last activity timestamp */
    fun updateActivity() {
        lastActivity = Instant.now()
    }

    /** Checks if the world is empty */
    fun isEmpty(): Boolean = players.isEmpty()

    /** Checks if the world can be cleaned up */
    fun canBeCleanedUp(): Boolean {
        return !isActive
                && isEmpty()
                && timeSinceLastActivity > 300 // 5 minutes
                && !isLobby // Never clean up lobby
    }

    fun destroySpawns() {
        WorldUtil.destroySpawns(bukkitWorld, worldConfig)
    }

    /** Gets world information for debugging */
    fun getDebugInfo(): Map<String, String> {
        return mapOf(
            "name" to name,
            "mode" to worldType.name,
            "template" to template.name,
            "gameId" to gameId.toString(),
            "playerCount" to playerCount.toString(),
            "isActive" to isActive.toString(),
            "createdAt" to createdAt.toString(),
            "lastActivity" to lastActivity.toString()
        )
    }
}