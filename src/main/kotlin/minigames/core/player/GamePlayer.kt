package minigames.core.player

import minigames.plugin
import java.util.UUID

class GamePlayer(
    val bukkitPlayer: org.bukkit.entity.Player,
    var state: PlayerState = PlayerState.LOBBY
) {
    val uuid: UUID get() = bukkitPlayer.uniqueId
    var kills: Int = 0
    var deaths: Int = 0

    fun resetStats() {
        kills = 0
        deaths = 0
    }

    fun addKill() {
        kills++
    }

    fun addDeath() {
        deaths++
    }

    fun sendMessage(message: String) {
        bukkitPlayer.sendMessage(message)
    }

    fun teleportToLobby() {
        plugin.lobby.addPlayer(bukkitPlayer)
    }
}

enum class PlayerState {
    LOBBY,
    ALIVE,
    SPECTATOR,
    ELIMINATED
}