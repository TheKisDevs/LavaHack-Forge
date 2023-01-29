package com.kisman.cc.util.enums

import com.kisman.cc.util.Colour
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.client.interfaces.Validable
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 23:11 of 24.12.2022
 */
enum class XRayBlocks(
    val color : Colour,
    vararg blocks : Block
) {
    Coal(Colour(0, 0, 0), Blocks.COAL_ORE),
    Iron(Colour(0.99f, 0.52f, 0.01f), Blocks.IRON_ORE),
    Gold(Colour(0.99f, 0.75f, 0.01f), Blocks.GOLD_ORE),
    Lapis(Colour(0.01f, 0.11f, 0.99f), Blocks.LAPIS_ORE),
    Redstone(Colour(0.99f, 0.01f, 0.01f), Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE),
    Diamond(Colour(0.01f, 0.56f, 0.99f), Blocks.DIAMOND_ORE),
    Emerald(Colour(0.01f, 0.99f, 0.69f), Blocks.EMERALD_BLOCK)

    ;

    val validator : Validator

    init {
        validator = Validator(*blocks)
    }

    class Validator(
        vararg val blocks : Block
    ) : Validable<BlockPos> {
        override fun valid(t : BlockPos) : Boolean = blocks.contains(mc.world.getBlockState(t).block)
    }
}