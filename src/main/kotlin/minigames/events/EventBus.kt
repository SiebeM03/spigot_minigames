package minigames.events

import minigames.util.logging.getLogger
import org.bukkit.event.Event
import kotlin.reflect.KClass

class EventBus {
    private val handlers = mutableMapOf<KClass<out Event>, MutableList<(Event) -> Unit>>()

    /** Register a handler for a specific event type */
    fun <T : Event> register(eventType: KClass<T>, handler: (T) -> Unit) {
        val wrapped: (Event) -> Unit = { event ->
            try {
                @Suppress("UNCHECKED_CAST")
                handler(event as T)
            } catch (ex: Exception) {
                // Fail-safe: log instead of crashing the game
                println("[EventBus] Error handling ${eventType.simpleName}: ${ex.message}")
                ex.printStackTrace()
            }
        }
        handlers.computeIfAbsent(eventType) { mutableListOf() }.add(wrapped)
    }

    /** Remove all handlers for this event type (optional) */
    fun <T : Event> unregister(eventType: KClass<T>) {
        handlers.remove(eventType)
    }

    /** Clear all handlers (e.g. when a game ends) */
    fun clear() {
        handlers.clear()
    }

    /** Post an event to all registered handlers */
    fun post(event: Event) {
        getLogger().info("Handling event: ${event.javaClass}...")
        handlers[event::class]?.forEach { handler ->
            try {
                handler(event)
            } catch (ex: Exception) {
                println("[EventBus] Error dispatching ${event::class.simpleName}: ${ex.message}")
                ex.printStackTrace()
            }
        }
    }
}