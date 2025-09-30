package minigames.util.logging

/**
 * Simple logging interface for the minigames plugin
 */
interface Logger {
    fun debug(message: String)
    fun info(message: String)
    fun warning(message: String)
    fun error(message: String)
    fun error(message: String, throwable: Throwable?)
    
    fun debug(message: () -> String)
    fun info(message: () -> String)
    fun warning(message: () -> String)
    fun error(message: () -> String)
    fun error(message: () -> String, throwable: Throwable?)
}
