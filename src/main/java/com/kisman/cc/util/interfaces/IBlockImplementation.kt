package com.kisman.cc.util.interfaces

import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 23:24 of 24.12.2022
 */
interface IBlockImplementation {
    fun valid(pos : BlockPos) : Boolean
    fun process(pos : BlockPos)
}