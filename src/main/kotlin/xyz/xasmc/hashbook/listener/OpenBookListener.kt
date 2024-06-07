package xyz.xasmc.hashbook.listener

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.BookMeta
import xyz.xasmc.hashbook.HashBook
import xyz.xasmc.hashbook.service.ItemDataServices
import xyz.xasmc.hashbook.service.StorageServices
import xyz.xasmc.hashbook.util.BookUtil
import xyz.xasmc.hashbook.util.MessageUtil.debugMiniMessage
import xyz.xasmc.hashbook.util.MessageUtil.sendMiniMessage


class OpenBookListener : Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (!(event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)) return

        val msgTitle = "<dark_aqua>[HashBook]</dark_aqua>"

        val player = event.player
        val hand = event.hand ?: run {
            player.sendMiniMessage("$msgTitle <yellow>[warn] 无法获取装备槽")
            return@onPlayerInteract
        }

        val item = event.item ?: return
        if (item.type != Material.WRITTEN_BOOK) return

        if (HashBook.config.setHashWhenOpenBook) BookUtil.calcAndSetBookHash(item, player, hand)

        if (!ItemDataServices.hasItemData(item, "HashBook.Hash")) return
        val hash = ItemDataServices.getItemData(item, "HashBook.Hash", ItemDataServices.DataType.String) ?: run {
            player.sendMiniMessage("$msgTitle <yellow>[warn] 无法读取成书哈希值")
            return@onPlayerInteract
        }
        val bookMeta = item.itemMeta as BookMeta
        bookMeta.pages(StorageServices.read(hash) ?: run {
            player.sendMiniMessage("$msgTitle <yellow>[warn] 无法读取成书书页, hash: $hash")
            return@onPlayerInteract
        })
        player.openBook(bookMeta)
        player.debugMiniMessage("$msgTitle <aqua>[debug] <dark_green>成功替换数据, hash: $hash")
        event.isCancelled = true
    }
}
