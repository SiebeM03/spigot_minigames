package minigames.commands.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.suggestion.SuggestionProvider
import minigames.commands.BrigadierCommand
import minigames.core.world.WorldType
import minigames.plugin
import minigames.util.Color
import minigames.util.logging.getLogger
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class GameCommand : BrigadierCommand() {
    override fun register() {
        val worldTypeSuggestionProvider = SuggestionProvider<CommandSender> { context, builder ->
            WorldType.entries
                .map { it.name }
                .filter { it.startsWith(builder.remaining) }
                .forEach { builder.suggest(it) }
            builder.buildFuture()
        }

        dispatcher.register(
            LiteralArgumentBuilder.literal<CommandSender>("game")
                .then(
                    RequiredArgumentBuilder.argument<CommandSender, String>(
                        "game_mode", StringArgumentType.word()
                    )
                        .suggests(worldTypeSuggestionProvider)
                        .then(
                            LiteralArgumentBuilder.literal<CommandSender>("create")
                                .executes { context ->
                                    val player = context.source as Player
                                    val gameMode = StringArgumentType.getString(context, "game_mode")
                                    create(player, WorldType.valueOf(gameMode))
                                    1
                                }
                        )
                        .then(
                            LiteralArgumentBuilder.literal<CommandSender>("play")
                                .executes { context ->
                                    val player = context.source as Player
                                    val gameMode = StringArgumentType.getString(context, "game_mode")
                                    play(player, WorldType.valueOf(gameMode))
                                    1
                                }
                        )
                        .then(
                            LiteralArgumentBuilder.literal<CommandSender>("delete")
                                .executes { context ->

                                    1
                                }
                        )
                )
        )
    }

    override fun setExecutor() {
        plugin.getCommand("game")?.setExecutor { sender, _, label, args ->
            val input = "game ${args.joinToString(" ")}"
            try {
                dispatcher.execute(input, sender)
            } catch (e: Exception) {
                sender.sendMessage("${Color.RED}Invalid usage: ${e.message}")
            }
            true
        }
    }

    override fun tabCompleter() {
        plugin.getCommand("game")?.tabCompleter = TabCompleter { sender, command, alias, args ->
            when (args.size) {
                1 -> WorldType.entries.map { it.name }
                2 -> listOf("create", "play", "delete")
                else -> emptyList()
            }
        }
    }

    private fun create(player: Player, worldType: WorldType) {
        plugin.gameManager.createGameFromType(worldType).thenAccept { game ->
            game.addPlayer(player)
        }
    }

    private fun play(player: Player, worldType: WorldType) {
        plugin.gameManager.getAvailableGame(worldType).thenAccept { game ->
            game.addPlayer(player)
        }
    }
}