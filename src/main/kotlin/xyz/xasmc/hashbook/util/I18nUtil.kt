package xyz.xasmc.hashbook.util

import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.BookMeta.Generation.*
import java.io.File

object I18nUtil {
    private lateinit var config: YamlConfiguration

    fun loadTranslate(path: File) {
        config = YamlConfiguration.loadConfiguration(path)
    }

    fun translate(generation: BookMeta.Generation?): String {
        return when (generation) {
            ORIGINAL -> getTranslate("book.generation.0")
            COPY_OF_ORIGINAL -> getTranslate("book.generation.1")
            COPY_OF_COPY -> getTranslate("book.generation.2")
            TATTERED -> getTranslate("book.generation.3")
            null -> getTranslate("book.generation.0")
        }
    }

    fun translate(type: Material): String {
        val key = type.itemTranslationKey
        return if (key != null) getTranslate(key) else type.name.lowercase()
    }

    fun translate(enchantment: Enchantment, level: Int): String {
        val name = getTranslate(enchantment.translationKey())
        val levelStr = if (level <= 10) getTranslate("enchantment.level.$level") else level.toString()
        return "$name $levelStr"
    }

    fun getTranslate(key: String): String {
        return config.getString(key) ?: key
    }
}