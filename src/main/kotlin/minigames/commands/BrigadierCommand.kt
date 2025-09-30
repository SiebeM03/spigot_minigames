package minigames.commands

import com.mojang.brigadier.CommandDispatcher
import org.bukkit.command.CommandSender

abstract class BrigadierCommand {
    lateinit var dispatcher: CommandDispatcher<CommandSender>

    fun init(dispatcher: CommandDispatcher<CommandSender>) {
        this.dispatcher = dispatcher
        register()
        setExecutor()
        tabCompleter()
    }

    abstract fun register()
    abstract fun setExecutor()
    open fun tabCompleter() {}
}