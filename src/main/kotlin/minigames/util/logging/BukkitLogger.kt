package minigames.util.logging

import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

/**
 * Bukkit/Spigot implementation of the Logger interface
 */
class BukkitLogger(
    private val plugin: JavaPlugin,
    private val debugEnabled: Boolean = false,
    private val clazz: Class<*>
) : Logger {

    private val bukkitLogger = plugin.logger

    override fun debug(message: String) {
        if (debugEnabled) {
            bukkitLogger.log(Level.INFO, "[${clazz.simpleName}] [DEBUG] $message")
        }
    }

    override fun info(message: String) {
        bukkitLogger.info("[${clazz.simpleName}] $message")
    }

    override fun warning(message: String) {
        bukkitLogger.warning("[${clazz.simpleName}] $message")
    }

    override fun error(message: String) {
        bukkitLogger.severe("[${clazz.simpleName}] $message")
    }

    override fun error(message: String, throwable: Throwable?) {
        if (throwable != null) {
            bukkitLogger.log(Level.SEVERE, "[${clazz.simpleName}] $message", throwable)
        } else {
            error(message)
        }
    }

    override fun debug(message: () -> String) {
        debug(message())
    }

    override fun info(message: () -> String) {
        info(message())
    }

    override fun warning(message: () -> String) {
        warning(message())
    }

    override fun error(message: () -> String) {
        error(message())
    }

    override fun error(message: () -> String, throwable: Throwable?) {
        error(message(), throwable)
    }
}