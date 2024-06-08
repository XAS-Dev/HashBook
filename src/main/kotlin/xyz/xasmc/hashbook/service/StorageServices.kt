package xyz.xasmc.hashbook.service

import xyz.xasmc.hashbook.config.PluginConfig
import xyz.xasmc.hashbook.config.StorageMode.*

interface StorageServices {
    fun save(hash: String, content: String)

    fun read(hash: String): String?

    companion object {
        lateinit var instance: StorageServices

        fun load(config: PluginConfig) {
            instance = when (config.storageMode) {
                FILE -> FileStorageServices()
                SQLITE -> TODO()
                MYSQL -> TODO()
            }
        }

        fun save(hash: String, content: String) = instance.save(hash, content)
        fun read(hash: String): String? = instance.read(hash)
    }
}