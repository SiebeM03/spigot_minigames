package minigames.commands

import com.mojang.brigadier.CommandDispatcher
import org.bukkit.command.CommandSender
import minigames.commands.commands.GameCommand
import minigames.commands.commands.SetupCommand

object Commands {

    fun init() {
        val dispatcher = CommandDispatcher<CommandSender>()

        GameCommand().init(dispatcher)
        SetupCommand().init(dispatcher)
    }

}