package minigames.util

import org.bukkit.Location
import kotlin.math.atan2
import kotlin.math.sqrt


object LocationUtil {
    fun lookAt(from: Location, to: Location): Location {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val dz = to.z - from.z

        val distanceXZ = sqrt(dx * dx + dz * dz)

        val yaw = Math.toDegrees(atan2(-dx, dz)).toFloat()
        val pitch = Math.toDegrees(-atan2(dy, distanceXZ)).toFloat()

        return from.clone().apply {
            this.yaw = yaw
            this.pitch = pitch
        }
    }
}