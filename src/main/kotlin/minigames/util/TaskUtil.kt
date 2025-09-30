package minigames.util

import minigames.plugin
import org.bukkit.scheduler.BukkitRunnable

object TaskUtil {
    /**
     * Runs the given [task] after [ticks] server ticks.
     * 20 ticks = 1 second.
     */
    fun runTaskLater(delay: Long, task: () -> Unit) {
        object : BukkitRunnable() {
            override fun run() {
                task()
            }
        }.runTaskLater(plugin, delay)
    }

    fun runTaskTimer(delay: Long, period: Long, task: () -> Unit) {
        object : BukkitRunnable() {
            override fun run() {
                task()
            }
        }.runTaskTimer(plugin, delay, period)
    }

    fun runCountdown(
        tickInterval: Long,
        times: Int,
        onTick: (Int) -> Unit,
        onFinish: () -> Unit
    ) {
        object : BukkitRunnable() {
            var time = times

            override fun run() {
                if (time <= 0) {
                    onFinish()
                    cancel()
                    return
                }

                onTick(time)
                time--
            }
        }.runTaskTimer(plugin, 0, tickInterval)
    }
}