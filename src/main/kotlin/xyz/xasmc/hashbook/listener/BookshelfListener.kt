package xyz.xasmc.hashbook.listener

import org.bukkit.Material
import org.bukkit.block.ChiseledBookshelf
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.BookMeta.Generation.*
import xyz.xasmc.hashbook.util.MarkUtil


class BookshelfListener : Listener {


    @EventHandler
    fun onPlayerMode(event: PlayerMoveEvent) {
        val player = event.player
        val world = player.world
        val direction = player.eyeLocation.direction
        val maxDistance = 4.0
        val result = player.world.rayTraceBlocks(player.eyeLocation, direction, maxDistance)
        if (result == null) {
            MarkUtil.removeMark(player)
            return
        }
        val hitBlock = result.hitBlock
        if (hitBlock == null) {
            MarkUtil.removeMark(player)
            return
        }
        val hitPosition = result.hitPosition
        if (hitBlock.type != Material.CHISELED_BOOKSHELF) {
            MarkUtil.removeMark(player)
            return
        }
        val bookshelf = hitBlock.state as ChiseledBookshelf
        val blockHitPosition = hitPosition.clone().subtract(hitBlock.location.toVector())
        val item = bookshelf.inventory.getItem(bookshelf.getSlot(blockHitPosition))
        if (item == null) {
            MarkUtil.removeMark(player)
            return
        }
        val normalizedEyeDirection = player.eyeLocation.direction.clone().normalize()
        val markLocation = hitPosition.clone().subtract(normalizedEyeDirection.multiply(0.1))
        val nameSb = StringBuilder(item.type.name)
        if (item.type == Material.WRITTEN_BOOK) {
            val meta = item.itemMeta as BookMeta
            nameSb.append("\n<aqua>${meta.title}\n<gray>${meta.author} 著")
            val generation = when (meta.generation) {
                ORIGINAL -> "原稿"
                COPY_OF_ORIGINAL -> "原稿的副本"
                COPY_OF_COPY -> "副本的副本"
                TATTERED -> "破烂不堪"
                null -> "原稿"
            }
            nameSb.append("\n<gray>$generation")
        }
        MarkUtil.updateMark(player, markLocation.toLocation(world), nameSb.toString())
    }
}
