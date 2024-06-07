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
import xyz.xasmc.hashbook.util.BookUtil
import xyz.xasmc.hashbook.util.MessageUtil.sendMiniMessage

object HashBookCommand {
    fun create(): CommandAPICommand {
        val msgTitle = "<dark_aqua>[HashBook]</dark_aqua>"

        val reloadCommand = CommandAPICommand("reload")
            .withPermission("xasmc.hashbook.command.reload")
            .executes(CommandExecutor { sender, _ ->
                sender.sendMiniMessage("$msgTitle <dark_green>重新加载中")
                HashBook.load()
                sender.sendMiniMessage("$msgTitle <dark_green>重新加载完成")
            })

        val calcHashCommand = CommandAPICommand("calcHash")
            .executes(CommandExecutor { sender, args ->
                val player = checkPlayer(sender) ?: return@CommandExecutor
                val (item) = checkHandItem(player, Material.WRITTEN_BOOK) ?: return@CommandExecutor
                val bookMeta = item.itemMeta as BookMeta
                val hash = BookUtil.generateHash(bookMeta)
                player.sendMiniMessage("$msgTitle <aqua>hash</aqua>: <green>${hash}")
            })

        val setHashCommand = CommandAPICommand("setHash")
            .withPermission("xasmc.hashbook.command.sethash")
            .withArguments(StringArgument("hash"))
            .executes(CommandExecutor { sender, args ->
                val player = checkPlayer(sender) ?: return@CommandExecutor
                val (item) = checkHandItem(player, Material.WRITTEN_BOOK) ?: return@CommandExecutor
                val hash =
                    ItemDataServices.getItemData(item, "HashBook.hash", ItemDataServices.DataType.String) ?: "<null>"
                ItemDataServices.setItemData(
                    item,
                    "HashBook.hash",
                    ItemDataServices.DataType.String,
                    args["hash"] as String
                )
                player.sendMiniMessage("$msgTitle <dark_green>已修改成书哈希 <aqua>old_hash</aqua>: <green>$hash</green> <aqua>new_hash</aqua>: <green>${args["hash"]}</green>")
            })

        val hashCommand = CommandAPICommand("hash")
            .executes(CommandExecutor { sender, args ->
                val player = checkPlayer(sender) ?: return@CommandExecutor
                val (item, hand) = checkHandItem(player, Material.WRITTEN_BOOK) ?: return@CommandExecutor
                BookUtil.calcAndSetBookHash(item, player, hand)
                player.sendMiniMessage("$msgTitle <dark_green>完成")
            })

        val bookInfoCommand = CommandAPICommand("bookInfo")
            .executes(CommandExecutor { sender, _ ->
                val player = checkPlayer(sender) ?: return@CommandExecutor
                val (item) = checkHandItem(player, Material.WRITTEN_BOOK) ?: return@CommandExecutor
                val bookMeta = item.itemMeta as BookMeta

                val title = bookMeta.title ?: "<null>"
                val author = bookMeta.author ?: "<null>"
                val hash = ItemDataServices.getItemData(
                    item, "HashBook.Hash", ItemDataServices.DataType.String
                ) ?: "<null>"

                Bukkit.getLogger().info(hash.javaClass.name)

                player.sendMiniMessage("<blue>===== <dark_aqua>HashBook Book Info <blue>=====")
                player.sendMiniMessage("<aqua>title</aqua>: <green>${title}")
                player.sendMiniMessage("<aqua>author</aqua>: <green>${author}")
                player.sendMiniMessage("<aqua>hash</aqua>: <green>${hash}")
            })

        val command = CommandAPICommand("hashbook")
            .withSubcommands(reloadCommand, calcHashCommand, setHashCommand, hashCommand, bookInfoCommand)
            .executes(CommandExecutor { sender, _ ->
                sender.sendMiniMessage("$msgTitle HashBook is Running!")
                sender.sendMiniMessage("<aqua>debug</aqua>: <green>${HashBook.config.debug}")
                sender.sendMiniMessage("<aqua>storage_mode</aqua>: <green>${HashBook.config.storageMode}")
                sender.sendMiniMessage("<aqua>item_data_mode</aqua>: <green>${HashBook.config.itemDataMode}")
                sender.sendMiniMessage("")
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

    private fun checkHandItem(player: Player, type: Material): Pair<ItemStack, EquipmentSlot>? {
        var pair = Pair(player.inventory.itemInMainHand, EquipmentSlot.HAND)
        if (pair.first.type != type) pair = Pair(player.inventory.itemInOffHand, EquipmentSlot.OFF_HAND)
        if (pair.first.type != type) {
            player.sendMiniMessage("<yellow>未检测到手持成书")
            return null
        }
        return pair
    }

}