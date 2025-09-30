package minigames.util

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object Messaging {
    fun broadcast(message: String) {
        Bukkit.broadcast(Component.text(message))
    }

    fun broadcast(message: String, players: List<Player>) {
        players.forEach { it.sendMessage(message) }
    }
}