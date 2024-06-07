package xyz.xasmc.hashbook.util

import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import xyz.xasmc.hashbook.HashBook
import xyz.xasmc.hashbook.service.ItemDataServices
import xyz.xasmc.hashbook.service.StorageServices
import xyz.xasmc.hashbook.util.MessageUtil.debugMiniMessage
import xyz.xasmc.hashbook.util.MessageUtil.sendMiniMessage
import java.util.*

object BookUtil {
    @OptIn(ExperimentalStdlibApi::class)
    fun generateHash(bookMeta: BookMeta): String {
        var hash = 0
        bookMeta.pages().forEach { page ->
            hash shr 1
            hash += page.hashCode()
        }
        return hash.toHexString()
    }

    fun calcAndSetBookHash(item: ItemStack, player: Player, hand: EquipmentSlot): Boolean {
        val msgTitle = "<dark_aqua>[HashBook]</dark_aqua>"
        var newItem = item

        val bookMeta = newItem.itemMeta as BookMeta

        var cleanedPages = false
        var setHash = false

        if (bookMeta.hasPages()) {
            val bookMetaClone = bookMeta.clone()
            bookMetaClone.pages(listOf())
            newItem.setItemMeta(bookMetaClone)
            cleanedPages = true
        }

        if (!ItemDataServices.hasItemData(newItem, "HashBook.Hash")) {
            val hash = generateHash(bookMeta)

            if (HashBook.config.setLore) {
                val lore = bookMeta.lore() ?: LinkedList()
                lore.add(MessageUtil.mm.deserialize(HashBook.config.loreContent))
                bookMeta.lore(lore)
                newItem.setItemMeta(bookMeta)
            }

            StorageServices.save(hash, bookMeta.pages())
            player.debugMiniMessage("$msgTitle <aqua>[debug]<dark_green>已存储成书书页</dark_green> <aqua>hash</aqua>: <green>$hash</green> <aqua>meta</aqua>: <green>$bookMeta</green>")

            newItem = ItemDataServices.setItemData(
                newItem, "HashBook.Hash", ItemDataServices.DataType.String, hash
            ) ?: run {
                player.sendMiniMessage("$msgTitle <yellow>[warn] 写入哈希失败")
                return false
            }
            setHash = true
        }

        if (cleanedPages || setHash) when (hand) {
            EquipmentSlot.HAND -> player.inventory.setItemInMainHand(newItem)
            EquipmentSlot.OFF_HAND -> player.inventory.setItemInOffHand(newItem)
            else -> return run {
                player.sendMiniMessage("$msgTitle <yellow>[warn] 错误的装备槽")
                return false
            }
        }

        when (Pair(cleanedPages, setHash)) {
            Pair(true, true) -> player.sendMiniMessage("$msgTitle <dark_green>已清除书页并记录哈希值")
            Pair(false, true) -> player.sendMiniMessage("$msgTitle <dark_green>已记录哈希值")
            Pair(true, false) -> player.sendMiniMessage("$msgTitle <dark_green>已清除书页")
        }
        return true
    }
}