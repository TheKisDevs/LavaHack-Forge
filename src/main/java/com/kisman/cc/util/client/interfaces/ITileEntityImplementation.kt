package com.kisman.cc.util.client.interfaces

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 23:36 of 24.12.2022
 */
interface ITileEntityImplementation {
    fun valid(tile : TileEntity, callingFromDraw : Boolean?) : Boolean
    fun process(tile : TileEntity, callingFromDraw : Boolean)
}