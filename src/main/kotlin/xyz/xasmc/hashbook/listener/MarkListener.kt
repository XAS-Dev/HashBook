package xyz.xasmc.hashbook.listener

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.ChiseledBookshelf
import org.bukkit.block.Lectern
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.util.Vector
import xyz.xasmc.hashbook.util.BookUtil
import xyz.xasmc.hashbook.util.MarkUtil


class MarkListener : Listener {


    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        if (player.gameMode == GameMode.SPECTATOR) return
        val direction = player.eyeLocation.direction
        val maxDistance = 5.0
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
        when (hitBlock.type) {
            Material.CHISELED_BOOKSHELF -> updateChiseledBookshelfMark(hitBlock, hitPosition, player)
            Material.LECTERN -> updateLecternMark(hitBlock, player)

            else -> MarkUtil.removeMark(player)
        }
    }

    private fun updateChiseledBookshelfMark(hitBlock: Block, hitPosition: Vector, player: Player) {
        val world = player.world
        val bookshelf = hitBlock.state as ChiseledBookshelf
        val blockHitPosition = hitPosition.clone().subtract(hitBlock.location.toVector())
        val item = bookshelf.inventory.getItem(bookshelf.getSlot(blockHitPosition))
        if (item == null) {
            MarkUtil.removeMark(player)
            return
        }
        val normalizedEyeDirection = player.eyeLocation.direction.clone().normalize()
        val markLocation = hitPosition.clone().subtract(normalizedEyeDirection.multiply(0.1))
        val abstract = BookUtil.getAbstract(item)
        MarkUtil.updateMark(player, markLocation.toLocation(world), abstract)
    }

    private fun updateLecternMark(hitBlock: Block, player: Player) {
        val lectern = hitBlock.state as Lectern
        val item = lectern.inventory.getItem(0)
        if (item == null) {
            MarkUtil.removeMark(player)
            return
        }
        val markLocation = hitBlock.location.clone()
        markLocation.x += 0.5
        markLocation.y += 1.5
        markLocation.z += 0.5
        val abstract = BookUtil.getAbstract(item)
        MarkUtil.updateMark(player, markLocation, abstract)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        MarkUtil.removeMark(event.player)
    }
}
