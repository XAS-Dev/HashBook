package xyz.xasmc.hashbook.service

import org.bukkit.inventory.ItemStack
import xyz.xasmc.hashbook.config.ItemDataMode
import xyz.xasmc.hashbook.config.PluginConfig

interface ItemDataServices {
    class DataType<T> {
        companion object {
            val Byte: DataType<Byte> = DataType()
            val Short: DataType<Short> = DataType()
            val Long: DataType<Long> = DataType()
            val Double: DataType<Double> = DataType()
            val Float: DataType<Float> = DataType()
            val ByteArray: DataType<ByteArray> = DataType()
            val Boolean: DataType<Boolean> = DataType()
            val String: DataType<String> = DataType()
        }
    }

    fun <T> setItemData(item: ItemStack, path: String, dataType: DataType<T>, value: T): ItemStack?
    fun <T> getItemData(item: ItemStack, path: String, dataType: DataType<T>): T?
    fun hasItemData(item: ItemStack, path: String): Boolean

    companion object {
        private lateinit var instance: ItemDataServices

        fun load(config: PluginConfig) {
            instance = when (config.itemDataMode) {
                ItemDataMode.NBT -> NBTItemDataServices()
                ItemDataMode.PDC -> PDCItemDataServices()
            }
        }

        fun <T> setItemData(item: ItemStack, path: String, dataType: DataType<T>, value: T) =
            instance.setItemData(item, path, dataType, value)

        fun <T> getItemData(item: ItemStack, path: String, dataType: DataType<T>) =
            instance.getItemData(item, path, dataType)

        fun hasItemData(item: ItemStack, path: String) = instance.hasItemData(item, path)
    }
}