package com.kisman.cc.util.enums

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.interfaces.Validable
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 9:43 of 01.11.2022
 */
enum class BlockESPBlocks(
    val handler : Validable<BlockPos>
) {
    Web(object : Validable<BlockPos> {
        override fun valid(t : BlockPos) : Boolean = mc.world.getBlockState(t).block == Blocks.WEB
    }),
    NetherPortal(object : Validable<BlockPos> {
        override fun valid(t : BlockPos) : Boolean = mc.world.getBlockState(t).block == Blocks.PORTAL
    }),
    EndPortal(object : Validable<BlockPos> {
        override fun valid(t : BlockPos) : Boolean = mc.world.getBlockState(t).block == Blocks.END_PORTAL
    }),
    Burrow(object : Validable<BlockPos> {
        override fun valid(t : BlockPos) : Boolean = mc.world.getBlockState(t).block != Blocks.AIR && mc.world.getEntitiesWithinAABB(EntityPlayer::class.java, mc.world.getBlockState(t).getSelectedBoundingBox(mc.world, t)).isNotEmpty()
    })
}