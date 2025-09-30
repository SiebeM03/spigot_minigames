package minigames.events.custom

import minigames.events.CustomEvent
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.entity.EntityDamageEvent

class PlayerFallDamageEvent(
    val player: Player,
    val damage: Double,
) : CustomEvent(), Cancellable {
    val cause: EntityDamageEvent.DamageCause = EntityDamageEvent.DamageCause.FALL
    private var cancelled = false

    override fun isCancelled(): Boolean = cancelled
    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }
}