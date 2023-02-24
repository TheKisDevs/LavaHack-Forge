package com.kisman.cc.util.enums

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.block
import com.kisman.cc.util.client.interfaces.BlockValidator
import com.kisman.cc.util.client.interfaces.Validable
import com.kisman.cc.util.state
import net.minecraft.block.BlockStoneBrick
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
    Web(BlockValidator(Blocks.WEB)),
    NetherPortal(BlockValidator(Blocks.PORTAL)),
    EndPortal(BlockValidator(Blocks.END_PORTAL)),
    Lever(BlockValidator(Blocks.LEVER)),

    Burrow(object : Validable<BlockPos> {
        override fun valid(
            t : BlockPos
        ) : Boolean = mc.world.getBlockState(t).block != Blocks.AIR && mc.world.getEntitiesWithinAABB(EntityPlayer::class.java, mc.world.getBlockState(t).getSelectedBoundingBox(mc.world, t)).isNotEmpty()
    }),

    CrackedStoneBlocks(object : Validable<BlockPos> {
        override fun valid(
            t: BlockPos
        ) : Boolean = block(t) == Blocks.STONEBRICK && state(t).getValue(BlockStoneBrick.VARIANT) == BlockStoneBrick.EnumType.CRACKED
    })
}