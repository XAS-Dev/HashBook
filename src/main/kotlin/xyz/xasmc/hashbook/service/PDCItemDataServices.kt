package xyz.xasmc.hashbook.service

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.xasmc.hashbook.HashBook

class PDCItemDataServices : ItemDataServices {
    @Suppress("UNCHECKED_CAST")
    override fun <T> setItemData(
        item: ItemStack, path: String, dataType: ItemDataServices.DataType<T>, value: T
    ): ItemStack? {
        val itemMeta = item.itemMeta
        val namespacedKey = NamespacedKey(HashBook.instance, path)
        val type = dataTypeToPdcType(dataType) ?: return null
        val data = when (dataType) {
            ItemDataServices.DataType.Boolean ->
                if (value is Boolean) (if (value) 1 else 0).toByte() as T
                else return null

            else -> value
        } as (T & Any)
        itemMeta.persistentDataContainer.set(namespacedKey, type, data)
        item.itemMeta = itemMeta
        return item
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getItemData(item: ItemStack, path: String, dataType: ItemDataServices.DataType<T>): T? {
        val itemMeta = item.itemMeta
        val namespace = NamespacedKey(HashBook.instance, path)
        val type = dataTypeToPdcType(dataType) ?: return null
        val result = itemMeta.persistentDataContainer.get(namespace, type)

        return when (dataType) {
            ItemDataServices.DataType.Boolean -> ((result is Byte) && (result.toInt() != 0)) as T
            else -> result
        }
    }

    override fun hasItemData(item: ItemStack, path: String): Boolean {
        val itemMeta = item.itemMeta
        return itemMeta.persistentDataContainer.has(NamespacedKey(HashBook.instance, path))
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> dataTypeToPdcType(type: ItemDataServices.DataType<T>): PersistentDataType<T, T>? {
        return when (type) {
            ItemDataServices.DataType.Byte -> PersistentDataType.BYTE
            ItemDataServices.DataType.Short -> PersistentDataType.SHORT
            ItemDataServices.DataType.Long -> PersistentDataType.LONG
            ItemDataServices.DataType.Float -> PersistentDataType.FLOAT
            ItemDataServices.DataType.Double -> PersistentDataType.DOUBLE
            ItemDataServices.DataType.ByteArray -> PersistentDataType.BYTE_ARRAY
            ItemDataServices.DataType.Boolean -> PersistentDataType.BYTE
            ItemDataServices.DataType.String -> PersistentDataType.STRING
            else -> return null
        } as PersistentDataType<T, T>
    }
}