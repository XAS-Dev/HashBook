package xyz.xasmc.hashbook

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import xyz.xasmc.hashbook.command.HashBookCommand
import xyz.xasmc.hashbook.config.ConfigLoader
import xyz.xasmc.hashbook.config.PluginConfig
import xyz.xasmc.hashbook.listener.BookshelfListener
import xyz.xasmc.hashbook.listener.OpenBookListener
import xyz.xasmc.hashbook.service.ItemDataServices
import xyz.xasmc.hashbook.service.StorageServices
import xyz.xasmc.hashbook.util.I18nUtil
import xyz.xasmc.hashbook.util.MarkUtil
import java.io.File

class HashBook : JavaPlugin() {
    override fun onEnable() {
        instance = this
        this.load()

        Bukkit.getPluginManager().registerEvents(BookshelfListener(), this)
        Bukkit.getPluginManager().registerEvents(OpenBookListener(), this)
        HashBookCommand.create().register(this)
    }

    override fun onDisable() {
        MarkUtil.clearAllMark()
    }

    fun load() {
        val langDir = File(dataFolder, "lang")
        if (!langDir.exists()) {
            langDir.mkdir()
            saveResource("lang/zh_cn.yml", true)
        }
        I18nUtil.loadTranslate(File(dataFolder, "lang/zh_cn.yml"))
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
