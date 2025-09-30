package minigames.game.admin.worldSetup.tools.tools

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import minigames.config.YamlConfigLoader
import minigames.game.admin.worldSetup.BaseWorldSetup
import minigames.game.admin.worldSetup.WorldSetup
import minigames.game.admin.worldSetup.tools.SetupTool
import minigames.plugin
import minigames.util.Color
import minigames.util.LocationUtil
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class SaveTool : SetupTool {
    override val item: ItemStack = ItemStack(Material.PAPER).apply {
        itemMeta = itemMeta?.apply {
            displayName(Component.text("${Color.GREEN}Save config"))
            lore(listOf(Component.text("${Color.GRAY}Right-click to save to config file")))
        }
    }

    override fun handleLeftClick(
        event: PlayerInteractEvent,
        worldSetup: WorldSetup
    ) {

    }

    override fun handleRightClick(
        event: PlayerInteractEvent,
        worldSetup: WorldSetup
    ) {
        val data = worldSetup.data
        val config = YamlConfigLoader.loadOrCreate("worlds")
        val worldSection = config.getConfigurationSection(worldSetup.templateName)
            ?: error("No config found for ${worldSetup.templateName}")

        if (data.spawns.isNotEmpty()) {
            saveSpawns(data, worldSection)
            event.player.sendMessage("${Color.GREEN}[Setup] Saved spawns to worlds.yml!")
        }
        if (data.isWorldChanged) {
            saveTemplate(worldSetup)
            event.player.sendMessage("${Color.GREEN}[Setup] Saved world to template!")
        }

        YamlConfigLoader.save("worlds", config)
        event.player.inventory.clear()
    }

    private fun saveSpawns(data: BaseWorldSetup, worldSection: ConfigurationSection) {
        val spawnList = data.spawns.map { loc ->
            LocationUtil.lookAt(loc, loc.clone().set(0.0, loc.y, 0.0))
        }.map { loc ->
            mapOf(
                "x" to loc.x,
                "y" to (loc.y + 1.0),
                "z" to loc.z,
                "yaw" to loc.yaw,
                "pitch" to loc.pitch
            )
        }
        worldSection.set("spawns", spawnList)
    }

    private fun saveTemplate(worldSetup: WorldSetup) {
        println("Saving world...")
        val world = Bukkit.getWorld(worldSetup.world.name)
        world?.save()

        fun copyFiles() {
            println("Copying world files to /templates/${worldSetup.templateName} folder...")
            val sourceDir = File(Bukkit.getWorldContainer(), worldSetup.world.name)
            val targetDir = File(Bukkit.getWorldContainer(), "templates/${worldSetup.templateName}")

            if (!targetDir.exists()) targetDir.mkdirs()

            val sourceLevelDat = File(sourceDir, "level.dat")
            val targetLevelDat = File(targetDir, "level.dat")
            if (sourceLevelDat.exists()) {
                Files.copy(
                    sourceLevelDat.toPath(),
                    targetLevelDat.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }

            val sourceRegionDir = File(sourceDir, "region")
            val targetRegionDir = File(targetDir, "region")
            if (sourceRegionDir.exists()) {
                if (!targetRegionDir.exists()) targetRegionDir.mkdirs()

                sourceRegionDir.listFiles()?.forEach { file ->
                    val targetFile = File(targetRegionDir, file.name)
                    Files.copy(
                        file.toPath(),
                        targetFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                }
            }
        }

        object : BukkitRunnable() {
            override fun run() {
                copyFiles()
            }
        }.runTaskLater(plugin, 10L)
    }
}