package minigames.game.admin.worldSetup

import org.bukkit.entity.Player
import minigames.util.WorldUtil
import java.util.*

class WorldSetupManager {
    val setupWorlds = mutableListOf<WorldSetup>()

    fun startSetup(template: String, player: Player) {
        val existingSetupWizard = setupWorlds.find {
            it.world.name.split("-").getOrNull(1) == template
        }

        val targetWorldSetup = existingSetupWizard ?: createNewWorldSetup(template)
        targetWorldSetup.addAdmin(player)
    }

    fun getSetupWizard(player: Player): WorldSetup? = setupWorlds.find { it.isEditing(player) }

    private fun createNewWorldSetup(template: String): WorldSetup {
        val world = WorldUtil.createFromTemplate(template, "setup-$template-${UUID.randomUUID()}")
        val setup = WorldSetup(world, template)
        setupWorlds.add(setup)
        return setup
    }
}