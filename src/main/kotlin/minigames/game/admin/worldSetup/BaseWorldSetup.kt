package minigames.game.admin.worldSetup

import org.bukkit.Location

data class BaseWorldSetup(
    var isWorldChanged: Boolean = false,
    val spawns: MutableList<Location> = mutableListOf()
)
