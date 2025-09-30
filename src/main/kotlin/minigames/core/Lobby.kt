package minigames.core

import minigames.core.world.GameWorld
import minigames.plugin
import minigames.util.logging.getLogger
import org.bukkit.GameMode
import org.bukkit.entity.Player

class Lobby {
    private val logger = getLogger()

    var world: GameWorld = plugin.worldManager.loadOrCreateLobbyWorld()
        ?: error("Failed to load or create lobby world")

    fun addPlayer(player: Player) {
        world.spawnPlayer(player)
        player.gameMode = GameMode.SURVIVAL
    }
}

