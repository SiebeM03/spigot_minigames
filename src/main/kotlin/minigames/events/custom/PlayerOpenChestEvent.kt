package minigames.events.custom

import minigames.events.CustomEvent
import org.bukkit.Location
import org.bukkit.block.Chest
import org.bukkit.entity.Player

class PlayerOpenChestEvent(
    val player: Player,
    val chest: Chest
) : CustomEvent() {

}