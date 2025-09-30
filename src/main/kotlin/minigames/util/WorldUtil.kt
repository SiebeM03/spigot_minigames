package minigames.util

import minigames.config.WorldConfig
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.util.Vector
import minigames.config.YamlConfigLoader
import minigames.core.world.WorldType
import java.io.File

object WorldUtil {

    fun createFromTemplate(templateName: String, newWorldName: String): World {
        val templateDir = File(Bukkit.getWorldContainer(), "templates/$templateName")
        val targetDir = File(Bukkit.getWorldContainer(), newWorldName)
        templateDir.copyRecursively(targetDir, overwrite = true)

        val world = Bukkit.createWorld(WorldCreator(newWorldName))!!
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world.weatherDuration = 0
        world.isThundering = false
        world.time = 3000L

        return world
    }

    fun deleteWorld(worldName: String) {
        Bukkit.unloadWorld(worldName, false)

        val targetDir = File(Bukkit.getWorldContainer(), worldName)
        targetDir.deleteRecursively()
    }

    private val spawnCageOffsets: List<Vector> = mutableListOf(
        Vector(0.0, -1.0, 0.0),
        Vector(1.0, 0.0, 0.0),
        Vector(1.0, 1.0, 0.0),
        Vector(1.0, 2.0, 0.0),
        Vector(-1.0, 0.0, 0.0),
        Vector(-1.0, 1.0, 0.0),
        Vector(-1.0, 2.0, 0.0),
        Vector(0.0, 0.0, 1.0),
        Vector(0.0, 1.0, 1.0),
        Vector(0.0, 2.0, 1.0),
        Vector(0.0, 0.0, -1.0),
        Vector(0.0, 1.0, -1.0),
        Vector(0.0, 2.0, -1.0),
    )

    fun destroySpawns(world: World, config: WorldConfig) {
        config.spawns.forEach {
            val spawnCopy = it.clone()
            spawnCageOffsets.forEach { blockOffset ->
                val blockLoc = spawnCopy.add(blockOffset)
                world.getBlockAt(blockLoc).type = Material.AIR
                spawnCopy.subtract(blockOffset)
            }
        }
    }
}