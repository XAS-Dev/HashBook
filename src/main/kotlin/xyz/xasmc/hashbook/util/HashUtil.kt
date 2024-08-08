package xyz.xasmc.hashbook.util

import java.security.MessageDigest

object HashUtil {
    val md = MessageDigest.getInstance("SHA-256")

    @OptIn(ExperimentalStdlibApi::class)
    fun hashString(str: String): String {
        return md.digest(str.toByteArray()).toHexString()
    }
}