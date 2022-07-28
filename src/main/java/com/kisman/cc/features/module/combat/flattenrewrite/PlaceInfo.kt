package com.kisman.cc.features.module.combat.flattenrewrite

import com.kisman.cc.features.module.combat.autorer.math.Vec3f
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.EntityLivingBase
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHandSide
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * @author _kisman_
 * @since 12:42 of 11.07.2022
 */
open class PlaceInfo(
    open var target: EntityLivingBase?,
    open var blockPos: BlockPos?
) {
    open var mc: Minecraft = Minecraft.getMinecraft()

    companion object {
        fun getElementFromListByPos(list : List<PlaceInfo>, pos : BlockPos): PlaceInfo? {
            return list.firstOrNull { it.blockPos == pos }
        }

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        @JvmField
        val INVALID = PlaceInfo(object : EntityLivingBase(null) {
            override fun getArmorInventoryList(): MutableIterable<ItemStack> {
                return ArrayList()
            }

            override fun setItemStackToSlot(slotIn: EntityEquipmentSlot, stack: ItemStack) {

            }

            override fun getItemStackFromSlot(slotIn: EntityEquipmentSlot): ItemStack {
                return ItemStack.EMPTY
            }

            override fun getPrimaryHand(): EnumHandSide {
                return EnumHandSide.RIGHT
            }
        }, BlockPos.ORIGIN)
    }
}