package minigames.core.world

import minigames.core.world.WorldType
import org.bukkit.Location
import java.io.File

/**
 * Represents a world template configuration
 */
data class WorldTemplate(
    val name: String,
    val mode: WorldType,
    val templatePath: File,
    val displayName: String = name,
    val description: String = "",
    val maxPlayers: Int = 8,
    val minPlayers: Int = 2,
    val spawns: List<Location> = mutableListOf(),
    val isEnabled: Boolean = true,
    val metadata: Map<String, String> = emptyMap()
) {
    /**
     * Checks if the template file exists and is valid
     */
    fun isValid(): Boolean {
        return templatePath.exists() && templatePath.isDirectory
    }

    /**
     * Gets the world folder name for this template
     */
    fun getWorldFolderName(): String {
        return templatePath.name
    }

    /**
     * Gets a metadata value by key
     */
    fun getMetadata(key: String): String? {
        return metadata[key]
    }

    /**
     * Checks if this template supports the given player count
     */
    fun supportsPlayerCount(count: Int): Boolean {
        return count in minPlayers..maxPlayers
    }
}