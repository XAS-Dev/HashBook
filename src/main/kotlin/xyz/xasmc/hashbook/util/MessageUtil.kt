package xyz.xasmc.hashbook.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.xasmc.hashbook.HashBook

object MessageUtil {
    val msgTitle = "<dark_aqua>[HashBook]</dark_aqua>"
    val mm = MiniMessage.miniMessage()

    fun copyMsg(message: String, copy: String, hover: String? = null): String {
        val messageComponent = mm.deserialize(message)
        val hoverComponent = hover?.let { mm.deserialize(it) }
        return mm.serialize(
            Component.text()
                .append(messageComponent)
                .clickEvent(ClickEvent.copyToClipboard(copy))
                .hoverEvent(hoverComponent)
                .build()
        )
    }

    infix fun Player.sendMiniMessage(message: String) =
        this.sendMessage(mm.deserialize(message))

    infix fun Player.debugMiniMessage(message: String) =
        if (HashBook.config.debug) this.sendMessage(mm.deserialize(message)) else null

    infix fun CommandSender.sendMiniMessage(message: String) =
        this.sendMessage(mm.deserialize(message))
}