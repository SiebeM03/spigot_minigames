package minigames.core.world

import minigames.config.YamlConfigLoader
import minigames.core.world.WorldType
import minigames.plugin
import minigames.util.logging.getLogger
import org.bukkit.*
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class WorldManager {
    private val logger = getLogger()

    private val templates = ConcurrentHashMap<String, WorldTemplate>()
    private val activeWorlds = ConcurrentHashMap<UUID, GameWorld>()

    private val templateDirectory = plugin.serverConfig.worlds.templates.templateDir
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(2)

    init {
        loadTemplates()
    }

    // ---- Internal ----
    fun loadOrCreateLobbyWorld(): GameWorld? {
        val template = getTemplate(plugin.serverConfig.worlds.lobbyWorldTemplate)
        val worldName = plugin.serverConfig.worlds.lobbyWorldName

        // Check if a lobby world already exists
        val world = WorldFactory.loadWorld(template, worldName)
        if (world != null) {
            logger.info("$worldName already exists, loading the existing lobby world")
            return world
        }

        // Create a new world
        logger.info("$worldName does not exist yet, creating a new lobby world")
        return WorldFactory.createFromTemplate(template, worldName).get()
    }

    private fun loadTemplates() {
        val dirs = templateDirectory.listFiles()?.filter { it.isDirectory } ?: return
        var loadedCount = 0
        dirs.forEach { dir ->
            val template = TemplateLoader.fromDirectory(dir)
            if (template != null && template.isValid()) {
                templates[template.name] = template
                loadedCount++
                logger.info("Loaded template: ${template.name} (${template.mode})")
            } else {
                logger.warning("Invalid template directory: ${dir.name}")
            }
        }
        logger.info("Loaded $loadedCount templates")
    }

    // ---- Public API ----
    fun getAllTemplates(): List<WorldTemplate> =
        templates.values.toList()

    fun getTemplatesByMode(worldType: WorldType): List<WorldTemplate> =
        templates.values.filter { it.mode == worldType && it.isEnabled }

    fun getTemplate(name: String): WorldTemplate =
        templates[name]
            ?: error("No template found with the name $name")

    fun getRandomTemplate(worldType: WorldType): WorldTemplate =
        getTemplatesByMode(worldType).randomOrNull()
            ?: error("No templates found of type ${worldType.name}")


    fun createGameWorld(template: WorldTemplate): CompletableFuture<GameWorld> =
        WorldFactory.createFromTemplate(template)


    fun destroyGameWorld(gameWorld: GameWorld): CompletableFuture<Boolean> =
        WorldLifecycle.destroy(gameWorld)

    fun loadGameWorld(template: WorldTemplate, worldName: String): GameWorld? =
        WorldFactory.loadWorld(template, worldName)


    fun cleanup() {
        fun cleanupLoadedWorlds() {
            logger.info("Deleting files for all worlds stored in WorldManager (found ${activeWorlds.values.size} worlds)")
            activeWorlds.values
                .filter { it != plugin.lobby.world }
                .forEach { destroyGameWorld(it) }
        }

        fun cleanupLeftoverWorlds() {
            val gameWorldFolderRegex = ".*-[a-fA-F0-9]{8}-([a-fA-F0-9]{4}-){3}[a-fA-F0-9]{12}$".toRegex()
            val remainingWorlds = Bukkit.getWorldContainer().listFiles()
                .filter { it.isDirectory }
                .filter { gameWorldFolderRegex.matches(it.name) }
            logger.info("Deleting files for all worlds that match the world name template but were not cleaned up (found ${remainingWorlds.size} worlds)")
            remainingWorlds.forEach { it.deleteRecursively() }
        }

        cleanupLoadedWorlds()
        cleanupLeftoverWorlds()
    }


    private object TemplateLoader {
        val logger get() = plugin.worldManager.logger

        fun fromDirectory(directory: File): WorldTemplate? {
            val templateName = directory.name
            val mode = determineMode(templateName)

            return WorldTemplate(
                name = templateName,
                mode = mode,
                templatePath = directory,
                displayName = templateName.substringBefore("_")
                    .replaceFirstChar(Char::uppercaseChar),
                description = "Template for ${mode.name} minigame",
                maxPlayers = determineMaxPlayers(templateName),
                minPlayers = 2,
                isEnabled = true
            )
        }

        private fun determineMode(templateName: String): WorldType = when {
            templateName.startsWith("skywars", true) -> WorldType.SKYWARS
            templateName.startsWith("bedwars", true) -> WorldType.BEDWARS
            else -> WorldType.LOBBY
        }

        private fun determineMaxPlayers(templateName: String): Int {
            val config = YamlConfigLoader.loadOrCreate("worlds")
            val section = config.getConfigurationSection(templateName)
                ?: error("No config found for a world named $templateName")
            val spawns = section.getList("spawns")
                ?: error("Missing spawns for $templateName")
            return spawns.size
        }
    }

    private object WorldFactory {
        val logger get() = plugin.worldManager.logger

        fun createFromTemplate(template: WorldTemplate, worldName: String? = null): CompletableFuture<GameWorld> {
            val future = CompletableFuture<GameWorld>()

            CompletableFuture.runAsync {
                val worldId = UUID.randomUUID()
                val actualName = worldName ?: "${template.name}-${worldId}"
                val worldDir = File(Bukkit.getWorldContainer(), actualName)
                template.templatePath.copyRecursively(worldDir, overwrite = true)

                Bukkit.getScheduler().callSyncMethod(plugin) {
                    val bukkitWorld = WorldCreator(actualName).createWorld()
                        ?: throw IllegalStateException("Failed to create world: $actualName")
                    configure(bukkitWorld)

                    val gameWorld = GameWorld(template, bukkitWorld, worldId).also {
                        plugin.worldManager.activeWorlds[it.gameId] = it
                        logger.info("Created game world: ${it.name} from template: ${template.name}")
                    }

                    future.complete(gameWorld)
                }
            }.exceptionally { ex ->
                future.completeExceptionally(ex)
                null
            }
            return future
        }

        fun loadWorld(template: WorldTemplate, worldName: String): GameWorld? {
            val worldDir = File(Bukkit.getWorldContainer(), worldName)
            if (!worldDir.exists() || !worldDir.isDirectory) {
                plugin.worldManager.logger.warning("World folder $worldName not found.")
                return null
            }

            // Already loaded?
            val bukkitWorld = Bukkit.getWorld(worldName) ?: WorldCreator(worldName).createWorld()
            if (bukkitWorld == null) {
                logger.warning("Failed to load world $worldName.")
                return null
            }

            return GameWorld(template, bukkitWorld).also {
                plugin.worldManager.activeWorlds[it.gameId] = it
                logger.info("Loaded existing world: ${it.name} (${it.worldType})")
            }
        }

        fun configure(world: World) {
            world.difficulty = Difficulty.NORMAL
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
            world.setGameRule(GameRule.DO_FIRE_TICK, false)
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
            world.time = 6000L
        }
    }

    private object WorldLifecycle {
        val logger get() = plugin.worldManager.logger

        fun destroy(gameWorld: GameWorld): CompletableFuture<Boolean> {
            val future = CompletableFuture<Boolean>()
            ejectPlayers(gameWorld)
            if (!Bukkit.unloadWorld(gameWorld.bukkitWorld, false)) future.complete(false)

            CompletableFuture.runAsync {
                try {
                    deleteDir(gameWorld.name)
                    plugin.worldManager.activeWorlds.remove(gameWorld.gameId)
                    gameWorld.deactivate()

                    logger.info("Destroyed game world: ${gameWorld.name}")
                    future.complete(true)
                } catch (e: Exception) {
                    logger.error("Failed to destroy ${gameWorld.name}: ${e.message}")
                    future.complete(false)
                }
            }

            return future
        }

        private fun ejectPlayers(world: GameWorld) =
            world.allPlayers.forEach { plugin.lobby.addPlayer(it) }

        private fun deleteDir(name: String) =
            File(Bukkit.getWorldContainer(), name).deleteRecursively()
    }
}