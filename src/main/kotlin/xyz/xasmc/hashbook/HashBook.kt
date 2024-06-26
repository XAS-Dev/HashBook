package xyz.xasmc.hashbook

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import xyz.xasmc.hashbook.command.HashBookCommand
import xyz.xasmc.hashbook.config.ConfigLoader
import xyz.xasmc.hashbook.config.PluginConfig
import xyz.xasmc.hashbook.listener.OpenBookListener
import xyz.xasmc.hashbook.service.ItemDataServices
import xyz.xasmc.hashbook.service.StorageServices

class HashBook : JavaPlugin() {
    override fun onEnable() {
        HashBook.instance = this
        this.load()

        Bukkit.getPluginManager().registerEvents(OpenBookListener(), this)
        HashBookCommand.create().register(this)
    }

    override fun onDisable() {

    }

    fun load() {
        HashBook.config = ConfigLoader.loadConfig()
        ItemDataServices.load(HashBook.config)
        StorageServices.load(HashBook.config)
    }

    companion object {
        lateinit var instance: HashBook
        lateinit var config: PluginConfig

        fun getDataFolder() = instance.dataFolder
        fun load() = instance.load()
    }
}
