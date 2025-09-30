package minigames.config

import org.bukkit.Bukkit
import java.io.File

data class ServerConfig(
    val logging: LoggingConfigs = LoggingConfigs(false),
    val worlds: WorldConfigs = WorldConfigs()
)

data class LoggingConfigs(
    val debugLogging: Boolean
)

data class WorldConfigs(
    val templates: WorldTemplateConfigs = WorldTemplateConfigs(),
    val lobbyWorldTemplate: String = "lobby",
    val lobbyWorldName: String = "lobby-world"
)

data class WorldTemplateConfigs(
    val templateDir: File = File(Bukkit.getWorldContainer(), "templates")
) {
    init {
        if (!templateDir.exists()) {
            templateDir.mkdirs()
        }
    }
}