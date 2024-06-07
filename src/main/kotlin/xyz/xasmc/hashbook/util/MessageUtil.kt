package xyz.xasmc.hashbook.util

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.xasmc.hashbook.HashBook

object MessageUtil {
    val mm = MiniMessage.miniMessage()

    infix fun Player.sendMiniMessage(message: String) =
        this.sendMessage(mm.deserialize(message))

    infix fun Player.debugMiniMessage(message: String) =
        if (HashBook.config.debug) this.sendMessage(mm.deserialize(message)) else null

    infix fun CommandSender.sendMiniMessage(message: String) =
        this.sendMessage(mm.deserialize(message))
}