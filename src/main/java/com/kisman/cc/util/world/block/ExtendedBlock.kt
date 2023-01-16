package com.kisman.cc.util.world.block

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.enums.ExtendedBlockVersions
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock

/**
 * @author _kisman_
 * @since 18:09 of 15.01.2023
 */
open class ExtendedBlock(
    val version : ExtendedBlockVersions,
    val display : String,
    val `super` : Block
) : Block(
    Material.AIR
) {
    fun isIt(
        slot : Int
    ) : Boolean {
        val stack = mc.player.inventory.getStackInSlot(slot)
        val item = stack.item

        return item is ItemBlock && item.block == `super` && stack.hasTagCompound() && stack.tagCompound!!.keySet.size == 2
    }
}