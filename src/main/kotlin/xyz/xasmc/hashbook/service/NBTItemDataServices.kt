package xyz.xasmc.hashbook.service

import de.tr7zw.nbtapi.NBTCompound
import de.tr7zw.nbtapi.NBTItem
import org.bukkit.inventory.ItemStack

class NBTItemDataServices : ItemDataServices {
    override fun <T> setItemData(
        item: ItemStack,
        path: String,
        dataType: ItemDataServices.DataType<T>,
        value: T
    ): ItemStack? {
        val nbtItem = NBTItem(item)
        val (nbtPath, key) = splitPathAndKey(path)
        val compound = getNbtCompoundByPath(nbtItem, nbtPath, true) ?: return null

        when (dataType) {
            ItemDataServices.DataType.Byte -> compound.setByte(key, value as Byte)
            ItemDataServices.DataType.Short -> compound.setShort(key, value as Short)
            ItemDataServices.DataType.Long -> compound.setLong(key, value as Long)
            ItemDataServices.DataType.ByteArray -> compound.setByteArray(key, value as ByteArray)
            ItemDataServices.DataType.Double -> compound.setDouble(key, value as Double)
            ItemDataServices.DataType.Float -> compound.setFloat(key, value as Float)
            ItemDataServices.DataType.Boolean -> compound.setBoolean(key, value as Boolean)
            ItemDataServices.DataType.String -> compound.setString(key, value as String)
        }

        return nbtItem.item
    }

    override fun <T> getItemData(item: ItemStack, path: String, dataType: ItemDataServices.DataType<T>): T? {
        val nbtItem = NBTItem(item)
        val (nbtPath, key) = splitPathAndKey(path)
        val compound = getNbtCompoundByPath(nbtItem, nbtPath) ?: return null

        return if (compound.hasKey(path)) when (dataType) {
            ItemDataServices.DataType.Byte -> compound.getByte(path) as T
            ItemDataServices.DataType.Short -> compound.getShort(path) as T
            ItemDataServices.DataType.Long -> compound.getLong(path) as T
            ItemDataServices.DataType.ByteArray -> compound.getByteArray(path) as T
            ItemDataServices.DataType.Double -> compound.getDouble(path) as T
            ItemDataServices.DataType.Float -> compound.getFloat(path) as T
            ItemDataServices.DataType.Boolean -> compound.getBoolean(path) as T
            ItemDataServices.DataType.String -> compound.getString(path) as T
            else -> null
        } else null
    }

    override fun hasItemData(item: ItemStack, path: String): Boolean {
        val nbtItem = NBTItem(item)
        val (nbtPath, key) = splitPathAndKey(path)
        return getNbtCompoundByPath(nbtItem, nbtPath)?.hasKey(key) ?: false
    }

    private fun splitPathAndKey(path: String): Pair<String, String> {
        val lastIndex = path.lastIndexOf(".")
        return Pair(path.substring(0, lastIndex), path.substring(lastIndex + 1))
    }

    private fun getNbtCompoundByPath(nbtItem: NBTItem, path: String, create: Boolean = false): NBTCompound? {
        var current: NBTCompound? = nbtItem
        path.split(".").forEach { str ->
            current = if (create) current?.getOrCreateCompound(str) else current?.getCompound(str)
        }
        return current
    }

}