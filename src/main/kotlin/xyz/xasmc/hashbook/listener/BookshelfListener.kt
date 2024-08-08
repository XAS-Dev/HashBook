package xyz.xasmc.hashbook.listener

import org.bukkit.Material
import org.bukkit.block.ChiseledBookshelf
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import xyz.xasmc.hashbook.util.I18nUtil
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
        val nameSb = StringBuilder(I18nUtil.translate(item.type))
        when (item.type) {
            Material.WRITTEN_BOOK -> {
                val meta = item.itemMeta as BookMeta
                nameSb.append("\n").append(meta.title)
                nameSb.append("\n<gray>").append(I18nUtil.getTranslate("book.byAuthor").format(meta.author))
                nameSb.append("\n<gray>").append(I18nUtil.translate(meta.generation))
            }

            Material.ENCHANTED_BOOK -> {
                val meta = item.itemMeta as EnchantmentStorageMeta
                meta.storedEnchants.forEach {
                    nameSb.append("\n<gray>").append(I18nUtil.translate(it.key, it.value))
                }
            }

            else -> {}
        }
        MarkUtil.updateMark(player, markLocation.toLocation(world), nameSb.toString())
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        MarkUtil.removeMark(event.player)
    }
}
