package minigames.commands

import org.bukkit.Bukkit
import java.io.File
import kotlin.text.startsWith

object CommandsUtil {

    fun suggestWorldNames(input: String): List<String> {
        return Bukkit.getWorlds()
            .map { it.name }
            .filter { it.startsWith(input, ignoreCase = true) }
    }

    fun suggestWorldTemplates(input: String): List<String> {
        return File(Bukkit.getWorldContainer(), "templates")
            .listFiles()
            .filter { !it.isFile }.map { it.name }
            .filter { it.startsWith(input, ignoreCase = true) }
    }
}