package xyz.xasmc.hashbook.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandExecutor
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

        val searchBookCommand = CommandAPICommand("searchBook")
            .withPermission("xasmc.hashbook.command.searchbook")
            .withArguments(StringArgument("incompleteHash"))
            .executes(CommandExecutor { sender, args ->
                val incompleteHash = args["incompleteHash"] as String
                sender.sendMiniMessage("$msgTitle <dark_green>搜索到以下结果")
                StorageServices.search(incompleteHash).forEach {
                    val shortHashMsg = shortHashMessage(it.first)
                    sender.sendMiniMessage("<light_purple>==============================")
                    sender.sendMiniMessage("$msgTitle <aqua>hash</aqua>: <green>$shortHashMsg")
                    sender.sendMiniMessage("$msgTitle <aqua>content</aqua>:")
                    sender.sendMiniMessage(it.second)
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
            .withPermission("xasmc.hashbook.command.hashbook")
            .withSubcommands(
                reloadCommand,
                calcHashCommand,
                setHashCommand,
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
                sender.sendMiniMessage("$msgTitle <aqua>lore_content</aqua>: <green>${HashBook.config.loreContent}")
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

}