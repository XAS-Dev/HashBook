package xyz.xasmc.hashbook.config

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import xyz.xasmc.hashbook.HashBook
import java.io.File

object ConfigLoader {

    fun loadConfig(): PluginConfig {
        HashBook.instance.saveDefaultConfig()
        val configFile = File(HashBook.getDataFolder(), "config.yml")
        val yamlConfig = YamlConfiguration.loadConfiguration(configFile)

        val config = PluginConfig(
            debug = yamlConfig.getBoolean("debug"),
            storageMode = when (yamlConfig.getString("storage_mode")?.lowercase()) {
                "file" -> StorageMode.FILE
                "sqlite" -> StorageMode.SQLITE
                "mysql" -> StorageMode.MYSQL
                else -> {
                    Bukkit.getLogger().warning("配置项 storage_mode 无效, 将使用默认 FILE 模式")
                    StorageMode.FILE
                }
            },
            itemDataMode = when (yamlConfig.getString("item_data_mode")?.lowercase()) {
                "pdc" -> ItemDataMode.PDC
                "nbt" -> ItemDataMode.NBT
                else -> {
                    Bukkit.getLogger().warning("配置项 storage_mode 无效, 将使用默认 PDC 模式")
                    ItemDataMode.PDC
                }
            },
            setHashWhenOpenBook = yamlConfig.getBoolean("set_hash_when_open_book")
        )

        return config
    }
}