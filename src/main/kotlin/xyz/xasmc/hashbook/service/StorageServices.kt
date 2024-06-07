package xyz.xasmc.hashbook.service

import net.kyori.adventure.text.Component
import xyz.xasmc.hashbook.config.PluginConfig
import xyz.xasmc.hashbook.config.StorageMode.*

interface StorageServices {
    fun save(hash: String, content: List<Component>)

    fun read(hash: String): List<Component>?

    companion object {
        lateinit var instance: StorageServices

        fun load(config: PluginConfig) {
            instance = when (config.storageMode) {
                FILE -> FileStorageServices()
                SQLITE -> TODO()
                MYSQL -> TODO()
            }
        }

        fun save(hash: String, content: List<Component>) = instance.save(hash, content)
        fun read(hash: String): List<Component>? = instance.read(hash)
    }
}