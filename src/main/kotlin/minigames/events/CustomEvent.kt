package minigames.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

open class CustomEvent : Event() {
    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }

    override fun getHandlers(): HandlerList = handlerList
}