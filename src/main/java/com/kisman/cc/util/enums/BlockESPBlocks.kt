package com.kisman.cc.util.enums

import com.kisman.cc.features.module.Module
import com.kisman.cc.util.Globals.mc
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 9:43 of 01.11.2022
 */
enum class BlockESPBlocks(
    val handler : IBlock
) {
    Web(object : IBlock {
        override fun valid(pos : BlockPos) : Boolean = mc.world.getBlockState(pos).block == Blocks.WEB
    }),
    NetherPortal(object : IBlock {
        override fun valid(pos : BlockPos) : Boolean = mc.world.getBlockState(pos).block == Blocks.PORTAL
    }),
    EndPortal(object : IBlock {
        override fun valid(pos : BlockPos) : Boolean = mc.world.getBlockState(pos).block == Blocks.END_PORTAL
    }),
    Burrow(object : IBlock {
        override fun valid(pos : BlockPos) : Boolean = mc.world.getBlockState(pos).block != Blocks.AIR && mc.world.getEntitiesWithinAABB(EntityPlayer::class.java, mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos)).isNotEmpty()
    })
}

interface IBlock {
    fun valid(
        pos : BlockPos
    ) : Boolean
}