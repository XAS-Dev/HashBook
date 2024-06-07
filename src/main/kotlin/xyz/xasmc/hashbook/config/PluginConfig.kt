package xyz.xasmc.hashbook.config

data class PluginConfig(
    val debug: Boolean,
    val storageMode: StorageMode,
    val itemDataMode: ItemDataMode,
    val setHashWhenOpenBook: Boolean,
    val setLore: Boolean,
    val loreContent: String
)
