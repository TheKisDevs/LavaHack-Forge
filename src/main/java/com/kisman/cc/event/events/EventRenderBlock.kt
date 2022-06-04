package com.kisman.cc.event.events

import com.kisman.cc.event.Event
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 21:49 of 31.05.2022
 */
class EventRenderBlock(
    val state : IBlockState,
    val pos : BlockPos
) : Event()