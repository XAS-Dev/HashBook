package xyz.xasmc.hashbook.service

import net.kyori.adventure.text.Component
import org.json.simple.JSONArray
import org.json.simple.parser.JSONParser
import xyz.xasmc.hashbook.HashBook
import xyz.xasmc.hashbook.util.MessageUtil
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class FileStorageServices : StorageServices {
    init {
        File(HashBook.getDataFolder(), "pages").mkdirs()
    }

    override fun save(hash: String, content: List<Component>) {
        val file = File(HashBook.getDataFolder(), "pages/$hash.json")
        val serializePages = LinkedList<String>()
        content.forEach {
            serializePages.add(MessageUtil.mm.serialize(it))
        }
        file.createNewFile()
        file.writeText(JSONArray.toJSONString(serializePages))
    }

    override fun read(hash: String): List<Component>? {
        val file = File(HashBook.getDataFolder(), "pages/$hash.json")
        val content = LinkedList<Component>()
        val parser = JSONParser()
        val fileContent = try {
            file.readText()
        } catch (e: FileNotFoundException) {
            return null
        }
        (parser.parse(fileContent) as JSONArray).forEach {
            content.add(MessageUtil.mm.deserialize(it as String))
        }
        return content
    }
}