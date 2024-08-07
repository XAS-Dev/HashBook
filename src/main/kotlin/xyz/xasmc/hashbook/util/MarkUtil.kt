package xyz.xasmc.hashbook.util

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

object MarkUtil {
    private val mm = MiniMessage.miniMessage()
    private val playerMark = mutableMapOf<Player, MutableList<ArmorStand>>()

    fun updateMark(player: Player, location: Location, name: String) {
        val world = player.world
        val textList = name.split("\n")
        val count = textList.size
        val interval = 0.2
        val offset = -0.4

        val marks = playerMark[player] ?: mutableListOf<ArmorStand>().also { playerMark[player] = it }

        val currentCount = marks.size
        when {
            count > currentCount -> repeat(count - currentCount) { marks.add(createArmorStand(world)) }
            count < currentCount -> repeat(currentCount - count) { marks.removeLast()?.remove() }
        }

        val top = location.clone().add(0.0, interval * count / 2 + offset, 0.0)
        textList.forEachIndexed { i, it ->
            val mark = playerMark[player]!![i]
            mark.teleport(top.clone().add(0.0, -i * interval, 0.0))
            mark.customName(mm.deserialize(it))
        }
    }

    fun removeMark(player: Player) {
        playerMark[player]?.forEach { it.remove() }
        playerMark.remove(player)
    }

    fun clearAllMark() {
        playerMark.forEach { it.value.forEach { it.remove() } }
        playerMark.clear()
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