package minigames.util.logging

import minigames.plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

/**
 * Centralized logging manager for the minigames plugin
 * Provides a simple way to get loggers for different classes
 */
object LoggerManager {
    private val loggers = ConcurrentHashMap<String, Logger>()
    private var globalLogger: Logger? = null
    private var debugEnabled = false
    
    /**
     * Initialize the logging system
     */
    fun initialize(plugin: JavaPlugin, debugEnabled: Boolean = false) {
        this.debugEnabled = debugEnabled
        globalLogger = BukkitLogger(plugin, debugEnabled, LoggerManager::class.java)
        
        globalLogger?.info { "LoggerManager initialized with debug: $debugEnabled" }
    }
    
    /**
     * Get a logger for a specific class
     */
    fun getLogger(clazz: Class<*>): Logger {
        return loggers.computeIfAbsent(clazz.simpleName) { 
            BukkitLogger(plugin, debugEnabled, clazz)
        }
    }
    
    /**
     * Get a logger for a specific name/category
     */
    fun getLogger(name: String): Logger {
        return loggers.computeIfAbsent(name) { 
            // Create a custom class that returns the name as simpleName
            BukkitLogger(plugin, debugEnabled, object : Any() {
                override fun toString(): String = name
            }.javaClass)
        }
    }
    
    /**
     * Get the global logger
     */
    fun getGlobalLogger(): Logger {
        return globalLogger ?: throw IllegalStateException("LoggerManager not initialized")
    }
    
    /**
     * Enable or disable debug logging globally
     */
    fun setDebugEnabled(enabled: Boolean) {
        this.debugEnabled = enabled
        // Recreate all existing loggers with new debug setting
        loggers.clear()
        globalLogger = BukkitLogger(plugin, enabled, LoggerManager::class.java)
    }
    
    /**
     * Check if debug logging is enabled
     */
    fun isDebugEnabled(): Boolean = debugEnabled
}

/**
 * Extension function to get a logger for any class
 */
fun Class<*>.getLogger(): Logger = LoggerManager.getLogger(this)

/**
 * Extension function to get a logger for any object
 */
fun Any.getLogger(): Logger = LoggerManager.getLogger(this.javaClass)
