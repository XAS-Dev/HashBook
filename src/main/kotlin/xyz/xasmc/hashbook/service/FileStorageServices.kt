package xyz.xasmc.hashbook.service

import xyz.xasmc.hashbook.HashBook
import java.io.File
import java.io.FileNotFoundException

class FileStorageServices : StorageServices {
    init {
        File(HashBook.getDataFolder(), "pages").mkdirs()
    }

    override fun save(hash: String, content: String) {
        val file = File(HashBook.getDataFolder(), "pages/$hash.json")
        file.createNewFile()
        file.writeText(content)
    }

    override fun read(hash: String): String? {
        val file = File(HashBook.getDataFolder(), "pages/$hash.json")
        return try {
            file.readText()
        } catch (e: FileNotFoundException) {
            return null
        }
    }
}