package minigames.core.game

import minigames.core.world.GameWorld
import minigames.core.world.WorldType
import minigames.gamemodes.skywars.SkywarsGame
import minigames.plugin
import minigames.util.logging.getLogger
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class GameManager {
    private val logger = getLogger()

    private val activeGames = ConcurrentHashMap<UUID, Game>()

    fun createGameFromType(worldType: WorldType): CompletableFuture<Game> {
        val template = plugin.worldManager.getRandomTemplate(worldType)
        return plugin.worldManager.createGameWorld(template).thenApply { world ->
            createGame(world)
        }
    }

    private fun createGame(world: GameWorld): Game {
        return when (world.worldType) {
            WorldType.SKYWARS -> SkywarsGame(world)
            WorldType.BEDWARS -> TODO()
            WorldType.LOBBY -> error("Lobby worlds should not be created using GameManager.createGame()")
        }.also { game ->
            activeGames.put(game.uuid, game)
        }
    }

    fun getAvailableGame(worldType: WorldType): CompletableFuture<Game> {
        val game = GamesQuery.builder()
            .withType(worldType)
            .hasRoom()
            .waitingForPlayersOnly()
            .random()

        return if (game != null) {
            CompletableFuture.completedFuture(game)
        } else {
            createGameFromType(worldType)
        }
    }

    fun getGameByPlayer(player: Player): Game? =
        GamesQuery.builder().hasPlayer(player).first()

    fun getGameByWorld(world: World): Game? =
        GamesQuery.builder().withWorld(world).first()

    fun cleanup() {
        activeGames.values.forEach(Game::cleanup)
    }

    class GamesQuery private constructor(
        private val games: List<Game> = plugin.gameManager.activeGames.values.toList()
    ) {
        fun withType(type: WorldType): GamesQuery =
            GamesQuery(games.filter { it.mode == type })

        fun hasRoom(): GamesQuery =
            GamesQuery(games.filter { it.hasRoom })

        fun hasPlayer(player: Player): GamesQuery =
            GamesQuery(games.filter { it.getGamePlayer(player) != null })

        fun withWorld(world: World): GamesQuery =
            GamesQuery(games.filter { it.gameWorld.bukkitWorld.uid == world.uid })

        fun waitingForPlayersOnly(): GamesQuery =
            GamesQuery(games.filter { it.state == GameState.WAITING_FOR_PLAYERS })

        fun runningOnly(): GamesQuery =
            GamesQuery(games.filter { it.state == GameState.PLAYING })

        fun filter(predicate: (Game) -> Boolean): GamesQuery =
            GamesQuery(games.filter(predicate))

        fun toList(): List<Game> = games

        fun first(): Game? {
            return try {
                games.first()
            } catch (e: NoSuchElementException) {
                null
            }
        }

        fun random(): Game? =
            games.randomOrNull()

        fun find(predicate: (Game) -> Boolean): Game? =
            games.find(predicate)

        companion object {
            fun builder(): GamesQuery = GamesQuery(plugin.gameManager.activeGames.values.toList())
        }
    }
}