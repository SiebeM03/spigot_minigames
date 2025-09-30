package minigames.game.admin.worldSetup

import minigames.config.WorldConfig
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import minigames.game.admin.worldSetup.tools.SetupTool
import minigames.game.admin.worldSetup.tools.tools.ExitTool
import minigames.game.admin.worldSetup.tools.tools.SaveTool
import minigames.game.admin.worldSetup.tools.tools.SetSpawnTool
import minigames.core.world.WorldType

class WorldSetup(
    val world: World,
    val templateName: String
) {
    val data = BaseWorldSetup()
    private val currentAdmins = mutableListOf<Player>()
    val admins: List<Player> get() = currentAdmins.toList()
    lateinit var tools: List<SetupTool>

    fun addAdmin(player: Player) {
        currentAdmins.add(player)

        player.teleport(Location(world, 0.0, 80.0, 0.0))
        player.gameMode = GameMode.CREATIVE
        giveItems(player)
    }

    fun removeAdmin(player: Player) {
        currentAdmins.remove(player)

        player.gameMode = GameMode.SURVIVAL
        player.inventory.clear()
    }

    fun isEditing(player: Player): Boolean = currentAdmins.contains(player)


    private fun giveItems(player: Player) {
        player.inventory.clear()

        val config = WorldConfig.loadFromYaml(templateName, world)
        tools = getToolsByGameMode(config.mode)

        tools.forEachIndexed { i, tool ->
            player.inventory.setItem(i, tool.item)
        }
    }

    private fun getToolsByGameMode(worldType: WorldType): List<SetupTool> {
        return when (worldType) {
            WorldType.SKYWARS -> listOf(SetSpawnTool(), SaveTool(), ExitTool())
            else -> emptyList()
        }
    }
}
