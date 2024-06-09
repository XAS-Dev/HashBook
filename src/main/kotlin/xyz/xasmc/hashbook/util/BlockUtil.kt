package xyz.xasmc.hashbook.util

import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block

object BlockUtil {
    fun checkInteractiveBlock(block: Block): Boolean {
        val type = block.type
        if (Tag.WOODEN_DOORS.isTagged(type)) return true
        if (Tag.WOODEN_TRAPDOORS.isTagged(type)) return true
        if (Tag.FENCE_GATES.isTagged(type)) return true
        if (Tag.BUTTONS.isTagged(type)) return true
        if (Tag.ANVIL.isTagged(type)) return true
        if (Tag.ALL_SIGNS.isTagged(type)) return true
        if (Tag.BEDS.isTagged(type)) return true
        if (Tag.SHULKER_BOXES.isTagged(type)) return true

        when (type) {
            Material.CRAFTING_TABLE -> return true
            Material.STONECUTTER -> return true
            Material.CARTOGRAPHY_TABLE -> return true
            Material.SMITHING_TABLE -> return true
            Material.BLAST_FURNACE -> return true
            Material.SMOKER -> return true
            Material.FURNACE -> return true
            Material.GRINDSTONE -> return true
            Material.LOOM -> return true
            Material.NOTE_BLOCK -> return true
            Material.ENCHANTING_TABLE -> return true
            Material.BREWING_STAND -> return true
            Material.BELL -> return true
            Material.BEACON -> return true
            Material.LECTERN -> return true
            Material.CHISELED_BOOKSHELF -> return true
            Material.CHEST -> return true
            Material.TRAPPED_CHEST -> return true
            Material.BARREL -> return true
            Material.ENDER_CHEST -> return true
            Material.LEVER -> return true
            Material.REPEATER -> return true
            Material.COMPARATOR -> return true
            Material.DISPENSER -> return true
            Material.DROPPER -> return true
            else -> {}
        }
        return false
    }
}