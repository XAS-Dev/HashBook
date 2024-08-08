package xyz.xasmc.hashbook.util

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.json.simple.JSONArray
import org.json.simple.parser.JSONParser
import xyz.xasmc.hashbook.HashBook
import xyz.xasmc.hashbook.service.ItemDataServices
import xyz.xasmc.hashbook.service.StorageServices
import xyz.xasmc.hashbook.util.MessageUtil.debugMiniMessage
import xyz.xasmc.hashbook.util.MessageUtil.MSG_TITLE
import xyz.xasmc.hashbook.util.MessageUtil.sendMiniMessage
import xyz.xasmc.hashbook.util.MessageUtil.shortHashMessage
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

    fun serializePages(pages: List<Component>): String {
        val serializedPages = LinkedList<String>()
        pages.forEach {
            serializedPages.add(MessageUtil.mm.serialize(it))
        }
        return JSONArray.toJSONString(serializedPages)
    }

    fun deserializePages(content: String): List<Component> {
        val pages = LinkedList<Component>()
        val parser = JSONParser()
        (parser.parse(content) as JSONArray).forEach {
            pages.add(MessageUtil.mm.deserialize(it as String))
        }
        return pages
    }

    fun storeBook(item: ItemStack, player: Player, hand: EquipmentSlot): Boolean {
        val bookMeta = item.itemMeta as BookMeta

        var newItem = item.clone()
        val newBookMeta = bookMeta.clone()

        var cleanedPages = false
        var setHash = false

        if (bookMeta.hasPages()) {
            newBookMeta.pages(listOf())
            newItem.setItemMeta(newBookMeta)
            cleanedPages = true
        }

        if (!ItemDataServices.hasItemData(newItem, "HashBook.Hash")) {
            if (HashBook.config.setLore) {
                val lore = bookMeta.lore() ?: LinkedList()
                lore.add(MessageUtil.mm.deserialize(HashBook.config.loreContent))
                bookMeta.lore(lore)
                newItem.setItemMeta(bookMeta)
            }

            val serialized = serializePages(bookMeta.pages())
            val hash = HashUtil.hashString(serialized)
            StorageServices.save(hash, serialized)
            val shortHashMsg = shortHashMessage(hash)
            player.debugMiniMessage("$MSG_TITLE <aqua>[debug]<dark_green>已存储成书书页</dark_green>")
            player.debugMiniMessage("$MSG_TITLE <aqua>[debug]<aqua>hash</aqua>: <green>$shortHashMsg")
            player.debugMiniMessage("$MSG_TITLE <aqua>[debug]<aqua>meta</aqua>: <green>$bookMeta")

            newItem = ItemDataServices.setItemData(
                newItem, "HashBook.Hash", ItemDataServices.DataType.String, hash
            ) ?: run {
                player.sendMiniMessage("$MSG_TITLE <yellow>[warn] 写入哈希失败")
                return false
            }
            setHash = true
        }

        if (cleanedPages || setHash) when (hand) {
            EquipmentSlot.HAND -> player.inventory.setItemInMainHand(newItem)
            EquipmentSlot.OFF_HAND -> player.inventory.setItemInOffHand(newItem)
            else -> return run {
                player.sendMiniMessage("$MSG_TITLE <yellow>[warn] 错误的装备槽")
                false
            }
        }

        when (Pair(cleanedPages, setHash)) {
            Pair(true, true) -> player.sendMiniMessage("$MSG_TITLE <dark_green>已清除书页并记录哈希值")
            Pair(false, true) -> player.sendMiniMessage("$MSG_TITLE <dark_green>已记录哈希值")
            Pair(true, false) -> player.sendMiniMessage("$MSG_TITLE <dark_green>已清除书页")
        }
        return true
    }

    fun getAbstract(item: ItemStack): String {
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
        return nameSb.toString()
    }
}