package com.kisman.cc.util.enums

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.interfaces.Validable
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 23:11 of 24.12.2022
 */
enum class XRayBlocks(
    vararg blocks : Block
) {
    Coal(Blocks.COAL_ORE),
    Iron(Blocks.IRON_ORE),
    Gold(Blocks.GOLD_ORE),
    Lapis(Blocks.LAPIS_ORE),
    Redstone(Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE),
    Diamond(Blocks.DIAMOND_ORE),
    Emerald(Blocks.EMERALD_BLOCK)

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