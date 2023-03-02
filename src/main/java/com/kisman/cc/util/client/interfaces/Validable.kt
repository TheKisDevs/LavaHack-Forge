package com.kisman.cc.util.client.interfaces

import com.kisman.cc.util.block
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 23:12 of 24.12.2022
 */
interface Validable<T : Any> {
    fun valid(t : T) : Boolean
}

class BlockValidator(
    private vararg val blocks : Block
) : Validable<BlockPos> {
    override fun valid(
        t : BlockPos
    ) : Boolean {
        for(block in blocks) {
            if(block(t) == block) {
                return true
            }
        }

        return false
    }
}

val validatorAir = BlockValidator(Blocks.AIR, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA)