package com.kisman.cc.features.nocom.modules.tracker

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos

/**
 * @author _kisman_
 * @since 12:41 of 28.08.2022
 */
class TrackedChunk(
    x : Int,
    y : Int
) {
    private var pos = ChunkPos(
        x,
        y
    )

    fun set(
        x : Int,
        y : Int
    ) {
        pos = ChunkPos(
            x,
            y
        )
    }

    fun getChunkPos() : ChunkPos = pos
    fun getBlockPos() : BlockPos = pos.getBlock(0, 0, 0)
}