package xyz.xasmc.hashbook.service

import xyz.xasmc.hashbook.HashBook
import java.io.FileNotFoundException
import java.util.*

class FileStorageServices : StorageServices {
    init {
        HashBook.getDataFolder().resolve("pages").mkdirs()
    }

    override fun save(hash: String, content: String) {
        val file = HashBook.getDataFolder().resolve("pages/$hash.json")
        file.createNewFile()
        file.writeText(content)
    }

    override fun read(hash: String): String? {
        val file = HashBook.getDataFolder().resolve("pages/$hash.json")
        return try {
            file.readText()
        } catch (e: FileNotFoundException) {
            return null
        }
    }

    override fun search(incompleteHash: String): List<Pair<String, String>> {
        val result = LinkedList<Pair<String, String>>()
        HashBook.getDataFolder().resolve("pages").listFiles()?.forEach {
            if (it.nameWithoutExtension.startsWith(incompleteHash)) result.add(
                Pair(it.nameWithoutExtension, it.readText())
            )
        }
        return result
    }
}