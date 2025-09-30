package minigames

import minigames.commands.Commands
import minigames.core.game.GameManager
import minigames.core.world.WorldManager
import minigames.events.GlobalListener
import minigames.game.admin.worldSetup.WorldSetupManager
import minigames.util.logging.LoggerManager
import minigames.config.ServerConfig
import minigames.config.ServerConfigLoader
import minigames.core.Lobby
import minigames.events.CustomItemListeners
import minigames.events.GuiListeners
import minigames.gui.inventory.GuiManager
import minigames.items.CustomItemManager
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: ServerPlugin

class ServerPlugin : JavaPlugin() {
    lateinit var gameManager: GameManager
    lateinit var worldSetupManager: WorldSetupManager
    lateinit var serverConfig: ServerConfig
    lateinit var worldManager: WorldManager
    lateinit var lobby: Lobby

    lateinit var guiManager: GuiManager
    lateinit var customItemManager: CustomItemManager

    override fun onEnable() {
        plugin = this

        serverConfig = ServerConfigLoader.load(this)

        LoggerManager.initialize(this, serverConfig.logging.debugLogging)

        Commands.init()
        gameManager = GameManager()
        worldSetupManager = WorldSetupManager()

        worldManager = WorldManager()
        lobby = Lobby()

        guiManager = GuiManager()
        customItemManager = CustomItemManager()

        this.server.pluginManager.registerEvents(GlobalListener(), this)
        this.server.pluginManager.registerEvents(GuiListeners(), this)
        this.server.pluginManager.registerEvents(CustomItemListeners(), this)
        logger.info("Server plugin enabled.")
    }

    override fun onDisable() {
        logger.info("Server plugin disabled.")

        gameManager.cleanup()
        worldManager.cleanup()
    }
}