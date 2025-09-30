package minigames.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import minigames.plugin
import java.io.File

object YamlConfigLoader {
    fun loadOrCreate(name: String): FileConfiguration {
        val file = File(plugin.dataFolder, "$name.yml")

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("$name.yml", false);
        }

        return YamlConfiguration.loadConfiguration(file)
    }

    fun save(name: String, config: FileConfiguration) {
        val file = File(plugin.dataFolder, "$name.yml")
        config.save(file);
    }
}