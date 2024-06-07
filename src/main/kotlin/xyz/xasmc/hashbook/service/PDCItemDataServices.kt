package xyz.xasmc.hashbook.service

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.xasmc.hashbook.HashBook

class PDCItemDataServices : ItemDataServices {
    override fun <T> setItemData(
        item: ItemStack,
        path: String,
        dataType: ItemDataServices.DataType<T>,
        value: T
    ): ItemStack? {
        val itemMeta = item.itemMeta
        itemMeta.persistentDataContainer.set(
            NamespacedKey(HashBook.instance, path),
            DataTypeToPdcType(dataType) ?: return null,
            when (dataType) {
                ItemDataServices.DataType.Boolean -> (if (value as Boolean) 1 else 0).toByte()
                else -> value
            } as (T & Any)
        )
        item.itemMeta = itemMeta
        return item
    }

    override fun <T> getItemData(item: ItemStack, path: String, dataType: ItemDataServices.DataType<T>): T? {
        val itemMeta = item.itemMeta
        val result =
            itemMeta.persistentDataContainer.get(
                NamespacedKey(HashBook.instance, path),
                DataTypeToPdcType(dataType) ?: return null
            )

        return when (dataType) {
            ItemDataServices.DataType.Boolean -> (result != 0) as T
            else -> result
        }
    }

    override fun hasItemData(item: ItemStack, path: String): Boolean {
        val itemMeta = item.itemMeta
        return itemMeta.persistentDataContainer.has(NamespacedKey(HashBook.instance, path))
    }

    private fun <T> DataTypeToPdcType(type: ItemDataServices.DataType<T>): PersistentDataType<T, T>? {
        return when (type) {
            ItemDataServices.DataType.Byte -> PersistentDataType.BYTE as PersistentDataType<T, T>
            ItemDataServices.DataType.Short -> PersistentDataType.SHORT as PersistentDataType<T, T>
            ItemDataServices.DataType.Long -> PersistentDataType.LONG as PersistentDataType<T, T>
            ItemDataServices.DataType.Float -> PersistentDataType.FLOAT as PersistentDataType<T, T>
            ItemDataServices.DataType.Double -> PersistentDataType.DOUBLE as PersistentDataType<T, T>
            ItemDataServices.DataType.ByteArray -> PersistentDataType.BYTE_ARRAY as PersistentDataType<T, T>
            ItemDataServices.DataType.Boolean -> PersistentDataType.BYTE as PersistentDataType<T, T>
            ItemDataServices.DataType.String -> PersistentDataType.STRING as PersistentDataType<T, T>
            else -> return null
        }
    }
}