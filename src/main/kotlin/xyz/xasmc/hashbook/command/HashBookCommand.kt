package xyz.xasmc.hashbook.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandExecutor
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import xyz.xasmc.hashbook.HashBook
import xyz.xasmc.hashbook.service.ItemDataServices
import xyz.xasmc.hashbook.service.StorageServices
import xyz.xasmc.hashbook.util.BookUtil
import xyz.xasmc.hashbook.util.MessageUtil
import xyz.xasmc.hashbook.util.MessageUtil.msgTitle
import xyz.xasmc.hashbook.util.MessageUtil.sendMiniMessage
import xyz.xasmc.hashbook.util.MessageUtil.shortHashMessage

object HashBookCommand {
    fun create(): CommandAPICommand {
        val reloadCommand = CommandAPICommand("reload")
            .withPermission("xasmc.hashbook.command.reload")
            .executes(CommandExecutor { sender, _ ->
                sender.sendMiniMessage("$msgTitle <dark_green>重新加载中")
                HashBook.load()
                sender.sendMiniMessage("$msgTitle <dark_green>重新加载完成")
            })

        val calcHashCommand = CommandAPICommand("calcHash")
            .withPermission("xasmc.hashbook.command.calchash")
            .executes(CommandExecutor { sender, args ->
                val player = checkPlayer(sender) ?: return@CommandExecutor
                val (item) = checkWrittenBook(player) ?: return@CommandExecutor
                val bookMeta = item.itemMeta as BookMeta
                val hash = BookUtil.generateHash(bookMeta)
                val shortHashMsg = shortHashMessage(hash)
                player.sendMiniMessage("$msgTitle <aqua>hash</aqua>: <green>$shortHashMsg")
            })

        val setHashCommand = CommandAPICommand("setHash")
            .withPermission("xasmc.hashbook.command.sethash")
            .withArguments(StringArgument("hash"))
            .executes(CommandExecutor { sender, args ->
                val player = checkPlayer(sender) ?: return@CommandExecutor
                val (item) = checkWrittenBook(player) ?: return@CommandExecutor
                val oldHash =
                    ItemDataServices.getItemData(item, "HashBook.hash", ItemDataServices.DataType.String) ?: "<null>"
                val newHash = args["hash"] as String
                ItemDataServices.setItemData(item, "HashBook.hash", ItemDataServices.DataType.String, newHash)
                val oldShortHashMsg = shortHashMessage(oldHash)
                val newShortHashMsg = shortHashMessage(newHash)
                player.sendMiniMessage("$msgTitle <dark_green>已修改成书哈希")
                player.sendMiniMessage("$msgTitle <aqua>old_hash</aqua>: <green>$oldShortHashMsg")
                player.sendMiniMessage("$msgTitle <aqua>new_hash</aqua>: <green>$newShortHashMsg")
            })

        val getPage = CommandAPICommand("getPage")
            .withPermission("xasmc.hashbook.command.getpage")
            .withArguments(StringArgument("hash"), IntegerArgument("page"))
            .executes(CommandExecutor { sender, args ->
                val hash = args["hash"] as String
                val page = args["page"] as Int
                val realPage = page - 1
                val data = StorageServices.read(hash) ?: run {
                    val shortHashMsg = shortHashMessage(hash)
                    sender.sendMiniMessage("$msgTitle <dark_green>未找到成书数据")
                    sender.sendMiniMessage("$msgTitle <aqua>hash</aqua>: <green>$shortHashMsg")
                    return@CommandExecutor
                }
                val pageList = BookUtil.deserializePages(data)
                if (pageList.isEmpty()) {
                    sender.sendMiniMessage("$msgTitle <dark_green>该成书没有书页")
                    return@CommandExecutor
                }
                if (pageList.size < page || page <= 0) {
                    sender.sendMiniMessage("$msgTitle <dark_green>错误的页码")
                    return@CommandExecutor
                }

                val shortHashMsg = shortHashMessage(hash)
                val message = Component.text()
                    .append(MessageUtil.mm.deserialize("$msgTitle <dark_green>Book: <green>$shortHashMsg</green> Page $page</dark_green>\n"))
                    .append(pageList[realPage])
                sender.sendMessage(message)

                val pageBarMessage = createPageBarMessage(hash, realPage, pageList.size)
                sender.sendMessage(pageBarMessage)
            })

        val searchBookCommand = CommandAPICommand("searchBook")
            .withPermission("xasmc.hashbook.command.searchbook")
            .withArguments(StringArgument("incompleteHash"))
            .executes(CommandExecutor { sender, args ->
                val incompleteHash = args["incompleteHash"] as String
                val searchResult = StorageServices.search(incompleteHash)
                sender.sendMiniMessage(
                    if (searchResult.isEmpty()) "$msgTitle <dark_green>未搜索到结果"
                    else "$msgTitle <dark_green>搜索到 ${searchResult.size} 条结果"
                )
                searchResult.forEach { result ->
                    val pageList = BookUtil.deserializePages(result.second)
                    val totalPage = pageList.size
                    val shortHashMsg = shortHashMessage(result.first)
                    sender.sendMiniMessage("<light_purple>========================================")
                    sender.sendMiniMessage("$msgTitle <aqua>hash</aqua>: <green>$shortHashMsg")
                    sender.sendMessage(
                        Component.text()
                            .append(MessageUtil.mm.deserialize("$msgTitle <aqua>content</aqua>:\n"))
                            .also { if (totalPage > 0) it.append(pageList.first()) }
                    )
                    sender.sendMessage(createPageBarMessage(result.first, 0, totalPage))
                }
            })

        val storeBookCommand = CommandAPICommand("storeBook")
            .withPermission("xasmc.hashbook.command.storebook")
            .executes(CommandExecutor { sender, args ->
                val player = checkPlayer(sender) ?: return@CommandExecutor
                val (item, hand) = checkWrittenBook(player) ?: return@CommandExecutor
                BookUtil.storeBook(item, player, hand)
                player.sendMiniMessage("$msgTitle <dark_green>完成")
            })

        val bookInfoCommand = CommandAPICommand("bookInfo")
            .withPermission("xasmc.hashbook.command.bookinfo")
            .executes(CommandExecutor { sender, _ ->
                val player = checkPlayer(sender) ?: return@CommandExecutor
                val (item) = checkWrittenBook(player) ?: return@CommandExecutor
                val bookMeta = item.itemMeta as BookMeta

                val title = bookMeta.title ?: "<null>"
                val author = bookMeta.author ?: "<null>"
                val hash = ItemDataServices.getItemData(
                    item, "HashBook.Hash", ItemDataServices.DataType.String
                ) ?: "<null>"

                Bukkit.getLogger().info(hash.javaClass.name)

                val shortHashMsg = shortHashMessage(hash)
                player.sendMiniMessage("$msgTitle <blue>HashBook Book Info")
                player.sendMiniMessage("$msgTitle <aqua>title</aqua>: <green>$title")
                player.sendMiniMessage("$msgTitle <aqua>author</aqua>: <green>$author")
                player.sendMiniMessage("$msgTitle <aqua>hash</aqua>: <green>$shortHashMsg")
            })

        val command = CommandAPICommand("hashbook")
            .withSubcommands(
                reloadCommand,
                calcHashCommand,
                setHashCommand,
                getPage,
                searchBookCommand,
                storeBookCommand,
                bookInfoCommand
            )
            .executes(CommandExecutor { sender, _ ->
                sender.sendMiniMessage("$msgTitle HashBook is Running!")
                sender.sendMiniMessage("$msgTitle <aqua>debug</aqua>: <green>${HashBook.config.debug}")
                sender.sendMiniMessage("$msgTitle <aqua>storage_mode</aqua>: <green>${HashBook.config.storageMode}")
                sender.sendMiniMessage("$msgTitle <aqua>item_data_mode</aqua>: <green>${HashBook.config.itemDataMode}")
                sender.sendMiniMessage("$msgTitle <aqua>set_lore</aqua>: <green>${HashBook.config.setLore}")
                sender.sendMiniMessage("$msgTitle <aqua>lore_content</aqua>: <i><green>${HashBook.config.loreContent}</i>")
            })

        return command
    }

    private fun checkPlayer(sender: CommandSender): Player? {
        if (sender !is Player) {
            sender.sendMiniMessage("<red>只能由玩家使用")
            return null
        }
        return sender
    }

    private fun checkWrittenBook(player: Player): Pair<ItemStack, EquipmentSlot>? {
        var pair = Pair(player.inventory.itemInMainHand, EquipmentSlot.HAND)
        if (pair.first.type != Material.WRITTEN_BOOK) pair =
            Pair(player.inventory.itemInOffHand, EquipmentSlot.OFF_HAND)
        if (pair.first.type != Material.WRITTEN_BOOK) {
            player.sendMiniMessage("<yellow>未检测到手持成书")
            return null
        }
        return pair
    }

    private fun generatePageRange(page: Int, total: Int, side: Int = 3): Pair<Pair<Boolean, Boolean>, Pair<Int, Int>> {
        var start = (page - side).coerceAtMost(total - 1 - 2 * side).coerceAtLeast(0)
        var end = (page + 1 + side).coerceAtLeast(2 * side + 1).coerceAtMost(total)
        val first = start > 0
        val last = end < total - 1
        if (first) start++
        if (last) end--
        return Pair(Pair(first, last), Pair(start, end))
    }

    private fun createPageBarMessage(hash: String, page: Int, total: Int, side: Int = 3): Component {
        val prevPageStr =
            "<hover:show_text:'<gray>上一页</gray>'><click:run_command:'/hashbook getPage $hash ${page + 1 - 1}'><</click></hover> "
        val nextPageStr =
            "<hover:show_text:'<gray>下一页</gray>'><click:run_command:'/hashbook getPage $hash ${page + 1 + 1}'>></click></hover> "
        val noPrevPageStr = "<hover:show_text:'<red><i>没有上一页</i></red>'><gray><</gray></hover> "
        val noNextPageStr = "<hover:show_text:'<red><i>没有下一页</i></red>'><gray>></gray></hover> "

        val createPageCodeStr = { i: Int, underline: Boolean ->
            val str = if (underline) "<u>${i + 1}</u>" else "${i + 1}"
            "<hover:show_text:'<gray>页码${i + 1}</gray>'><click:run_command:'/hashbook getPage $hash ${i + 1}'>$str</click></hover>"
        }

        val pageBarSB = StringBuilder()

        pageBarSB.append(if (page == 0) noPrevPageStr else prevPageStr)
        pageBarSB.append("<dark_green>Page</dark_green> <gray>${page + 1}</gray><dark_gray>/</dark_gray><gray>${total}</gray> ")
        pageBarSB.append(if (page == total - 1) noNextPageStr else nextPageStr)

        val (firstAndLast, startAndEnd) = generatePageRange(page, total, side)
        val (first, last) = firstAndLast
        val (start, end) = startAndEnd
        pageBarSB.append("<dark_gray>(</dark_gray>")
        if (first) pageBarSB.append(createPageCodeStr(0, false)).append(" <dark_gray>...</dark_gray> ")
        for (i in start until end) {
            if (i != start) pageBarSB.append(" <dark_gray>|</dark_gray> ")
            pageBarSB.append(createPageCodeStr(i, i == page))
        }
        if (last) pageBarSB.append(" <dark_gray>...</dark_gray> ").append(createPageCodeStr(total - 1, false))
        pageBarSB.append("<dark_gray>)</dark_gray>")

        val pageBarMessage = pageBarSB.toString()
        return MessageUtil.mm.deserialize(pageBarMessage)
    }
}