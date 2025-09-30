package minigames.config

import minigames.core.world.WorldType
import org.bukkit.Location
import org.bukkit.World

data class WorldConfig(
    val templateName: String,
    val spawns: List<Location>,
    val mode: WorldType
) {
    companion object {
        private val config = YamlConfigLoader.loadOrCreate("worlds")

        fun loadFromYaml(templateName: String, world: World): WorldConfig {
            val worldSection = config.getConfigurationSection(templateName)
                ?: error("No config found for a world named $templateName")

            val mode = WorldType.valueOf(
                worldSection.getString("mode")?.uppercase()
                    ?: error("Missing mode")
            )
            val spawnsSection = worldSection.getList("spawns")
                ?: error("Missing spawns")

            val spawns = spawnsSection.map {
                val map = it as Map<*, *>
                Location(
                    world,
                    (map["x"] as Number).toDouble() + 0.5,
                    (map["y"] as Number).toDouble(),
                    (map["z"] as Number).toDouble() + 0.5,
                    (map["yaw"] as Number).toFloat(),
                    (map["pitch"] as Number).toFloat()
                )
            }

            return WorldConfig(templateName, spawns, mode)
        }
    }
}