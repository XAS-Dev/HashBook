package xyz.xasmc.hashbook.util

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

object MarkUtil {
    private val mm = MiniMessage.miniMessage()
    private val playerMark = mutableMapOf<Player, Mark>()

    class Mark {
        private val armorStands = mutableListOf<ArmorStand>()

        fun update(location: Location, text: String) {
            val world = location.world
            val textList = text.lines()
            val count = textList.size
            val interval = 0.2
            val offset = -0.4
            val currentCount = armorStands.size
            when {
                count > currentCount -> repeat(count - currentCount) { armorStands.add(createArmorStand(world)) }
                count < currentCount -> repeat(currentCount - count) { armorStands.removeLast()?.remove() }
            }
            val top = location.clone().add(0.0, interval * count / 2 + offset, 0.0)
            textList.forEachIndexed { i, it ->
                val mark = armorStands[i]
                mark.teleport(top.clone().add(0.0, -i * interval, 0.0))
                mark.customName(mm.deserialize(it))
            }
        }

        fun remove() {
            armorStands.forEach { it.remove() }
            armorStands.clear()
        }

        private fun createArmorStand(world: World): ArmorStand {
            return (world.spawnEntity(Location(world, .0, .0, .0), EntityType.ARMOR_STAND) as ArmorStand).apply {
                isVisible = false
                isMarker = true
                isCustomNameVisible = true
                setGravity(false)
            }
        }
    }

    fun updateMark(player: Player, location: Location, text: String) {
        val mark = playerMark[player] ?: Mark().also { playerMark[player] = it }
        mark.update(location, text)
    }

    fun removeMark(player: Player) {
        playerMark[player]?.remove()
        playerMark.remove(player)
    }

    fun clearAllMark() {
        playerMark.forEach { it.value.remove() }
        playerMark.clear()
    }
}