package minigames.config

import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object ServerConfigLoader {
    fun load(plugin: JavaPlugin): ServerConfig {
        plugin.saveDefaultConfig()
        val cfg = plugin.config

        return ServerConfig(
            logging = loadLoggingConfigs(cfg),
            worlds = loadWorldConfigs(cfg)
        )
    }

    private fun loadLoggingConfigs(cfg: FileConfiguration): LoggingConfigs {
        val debugLogging = cfg.getBoolean("debug-logging")

        return LoggingConfigs(
            debugLogging = debugLogging
        )
    }

    private fun loadWorldConfigs(cfg: FileConfiguration): WorldConfigs {
        val templateDir = File(
            Bukkit.getWorldContainer(),
            cfg.getString("worlds.templates.dir") ?: "templates"
        )

        return WorldConfigs(
            templates = WorldTemplateConfigs(templateDir)
        )
    }
}