package minigames.commands.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.suggestion.SuggestionProvider
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import minigames.commands.BrigadierCommand
import minigames.commands.CommandsUtil
import minigames.plugin
import minigames.util.Color

class SetupCommand : BrigadierCommand() {
    override fun register() {
        val templateSuggestionProvider = SuggestionProvider<CommandSender> { context, builder ->
            CommandsUtil.suggestWorldTemplates(builder.remaining).forEach { builder.suggest(it) }
            builder.buildFuture()
        }

        dispatcher.register(
            LiteralArgumentBuilder.literal<CommandSender>("setup")
                .requires { sender -> sender is Player }
                .requires { sender -> sender.isOp }
                .then(
                    RequiredArgumentBuilder.argument<CommandSender, String>(
                        "template_name", StringArgumentType.word()
                    )
                        .suggests(templateSuggestionProvider)
                        .executes { context ->
                            val player = context.source as Player
                            val templateName = StringArgumentType.getString(context, "template_name")
                            setup(player, templateName)
                            1
                        }
                ))
    }

    override fun setExecutor() {
        plugin.getCommand("setup")?.setExecutor { sender, _, label, args ->
            val input = "setup ${args.joinToString(" ")}"
            try {
                dispatcher.execute(input, sender)
            } catch (e: Exception) {
                sender.sendMessage("${Color.RED}Invalid usage hello: ${e.message}")
            }
            true
        }
    }

    override fun tabCompleter() {
        plugin.getCommand("setup")?.tabCompleter = TabCompleter { sender, command, alias, args ->
            when (args.size) {
                1 -> CommandsUtil.suggestWorldTemplates(args[0])
                else -> emptyList()
            }
        }
    }

    private fun setup(player: Player, template: String) {
        plugin.worldSetupManager.startSetup(template, player)
    }
}