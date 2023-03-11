package com.kisman.cc.util.enums

import com.kisman.cc.util.Colour
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
    val handler : Validable<BlockPos>,
    val color : Colour? = null
) {
    Web(BlockValidator(Blocks.WEB)),
    NetherPortal(BlockValidator(Blocks.PORTAL)),
    EndPortal(BlockValidator(Blocks.END_PORTAL)),
    Lever(BlockValidator(Blocks.LEVER)),

    Burrow(object : Validable<BlockPos> {
        override fun valid(
            t : BlockPos
        ) : Boolean = block(t) != Blocks.AIR && mc.world.getEntitiesWithinAABB(EntityPlayer::class.java, state(t).getSelectedBoundingBox(mc.world, t)).isNotEmpty()
    }),

    CrackedStoneBlocks(object : Validable<BlockPos> {
        override fun valid(
            t : BlockPos
        ) : Boolean = block(t) == Blocks.STONEBRICK && state(t).getValue(BlockStoneBrick.VARIANT) == BlockStoneBrick.EnumType.CRACKED
    }),

    Coal(BlockValidator(Blocks.COAL_ORE), Colour(0, 0, 0)),
    Iron(BlockValidator(Blocks.IRON_ORE), Colour(0.99f, 0.52f, 0.01f)),
    Gold(BlockValidator(Blocks.GOLD_ORE), Colour(0.99f, 0.75f, 0.01f)),
    Lapis(BlockValidator(Blocks.LAPIS_ORE), Colour(0.01f, 0.11f, 0.99f)),
    Redstone(BlockValidator(Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE), Colour(0.99f, 0.01f, 0.01f)),
    Diamond(BlockValidator(Blocks.DIAMOND_ORE), Colour(0.01f, 0.56f, 0.99f)),
    Emerald(BlockValidator(Blocks.EMERALD_BLOCK), Colour(0.01f, 0.99f, 0.69f))
}