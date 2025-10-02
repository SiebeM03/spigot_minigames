package minigames.gamemodes.skywars

import minigames.core.game.Game
import minigames.core.player.GamePlayer
import minigames.core.player.PlayerState
import minigames.core.world.GameWorld
import minigames.events.custom.PlayerFallDamageEvent
import minigames.events.custom.PlayerOpenChestEvent
import minigames.events.custom.PlayerVoidDamageEvent
import minigames.gamemodes.skywars.systems.chestfiller.ChestManager
import minigames.util.logging.getLogger
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.FireworkEffect
import org.bukkit.block.BlockType
import org.bukkit.block.Chest
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard

class SkywarsGame(gameWorld: GameWorld) : Game(gameWorld) {
    val chestManager = ChestManager()

    override fun registerEvents() {
        eventBus.register(PlayerDeathEvent::class) { e ->
            getLogger().info("PlayerDeathEvent: Player died from ${e.entity.lastDamageCause}")
            val victim = e.entity
            e.isCancelled = true
            handlePlayerDeath(victim)

            // Add kill to player stats and update scoreboard
            val killer = victim.killer
            if (killer != null) {
                players[killer.uniqueId]?.addKill()
                createScoreBoard(killer)
                this.broadcast("${victim.name} was defeated by ${killer.name}")
            }
        }
        eventBus.register(PlayerFallDamageEvent::class) { e ->
            getLogger().info("PlayerFallDamageEvent: Player took fall damage")
        }
        eventBus.register(PlayerVoidDamageEvent::class) { e ->
            getLogger().info("PlayerVoidDamageEvent: Player took damage")
            e.isCancelled = true
            handlePlayerDeath(e.player)
        }
        // Chest loot handling events
        eventBus.register(PlayerOpenChestEvent::class) { e ->
            getLogger().info("PlayerOpenChestEvent: Player opened chest at ${e.chest.location.x} ${e.chest.location.y} ${e.chest.location.z}")
            chestManager.onOpenChest(e.chest)
        }
        eventBus.register(BlockBreakEvent::class) { e ->
            getLogger().info("BlockBreakEvent: Block broken")
            val blockState = e.block.state
            if (blockState is Chest) {
                getLogger().info("Chest broken")
                chestManager.onBreakChest(blockState)
            }
        }
        eventBus.register(BlockExplodeEvent::class) { e ->
            getLogger().info("BlockExplodeEvent")
            e.blockList().forEach { block ->
                val blockState = block.state
                if (blockState is Chest) {
                    getLogger().info("Chest broken")
                    chestManager.onBreakChest(blockState)
                }
            }
        }
        eventBus.register(EntityExplodeEvent::class) { e ->
            getLogger().info("EntityExplodeEvent")
            e.blockList().forEach { block ->
                val blockState = block.state
                if (blockState is Chest) {
                    getLogger().info("Chest broken")
                    chestManager.onBreakChest(blockState)
                }
            }
        }
    }

    override fun createScoreBoard(player: Player) {
        val manager = Bukkit.getScoreboardManager()
        val scoreboard = manager.newScoreboard

        val objective: Objective = scoreboard.registerNewObjective(
            "skywars",
            Criteria.DUMMY,
            Component.text("§b§lSkyWars") // Colored title
        )

        objective.displaySlot = DisplaySlot.SIDEBAR

        // Add lines (scores must be unique & descending)
        objective.getScore("§eMap: §f${gameWorld.name.substringAfter("_").substringBefore("-")}").score = 6
        objective.getScore("§ePlayers: §f${gameWorld.playerCount}/${gameWorld.template.maxPlayers}").score = 5
        objective.getScore("§eKills: §f${players[player.uniqueId]?.kills}").score = 4
        objective.getScore("§eTime: §f02:14").score = 3
        objective.getScore("§7----------------").score = 2
        objective.getScore("§bplay.yourserver.net").score = 1

        player.scoreboard = scoreboard
    }

    override fun onStart() {
        this.broadcast("SkyWars started in ${gameWorld.name}!")
        gameWorld.destroySpawns()
    }

    override fun onEnd() {

    }

    override fun checkWinCondition(): Boolean {
        val alive = getPlayers().filter { it.state == PlayerState.ALIVE }
        val winner = alive.firstOrNull() ?: return false
        displayWinner(winner)
        spawnFireworks(winner)
        this.broadcast("Winner: ${winner.bukkitPlayer.name}")
        return true
    }

    private fun displayWinner(player: GamePlayer) {
        players.forEach {
            it.value.bukkitPlayer.showTitle(
                Title.title(
                    Component.text("Winner!", NamedTextColor.GOLD),
                    Component.text(player.bukkitPlayer.name, NamedTextColor.AQUA)
                )
            )
        }
    }

    private fun spawnFireworks(player: GamePlayer) {
        val loc = player.bukkitPlayer.location
        val fw = player.bukkitPlayer.world.spawn(loc, Firework::class.java)
        val meta = fw.fireworkMeta
        meta.addEffect(
            FireworkEffect.builder()
                .withColor(org.bukkit.Color.AQUA)
                .withFade(org.bukkit.Color.BLUE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .flicker(true)
                .trail(true)
                .build()
        )
        meta.power = 1
        fw.fireworkMeta = meta
    }
}