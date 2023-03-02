package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

/**
 * @author _kisman_
 * @since 14:31 of 11.06.2022
 */
public class EventRenderBlock extends Event {
    public IBlockState state;
    public BlockPos pos;

    public EventRenderBlock(IBlockState state, BlockPos pos) {
        this.state = state;
        this.pos = pos;
    }

    public static class Start { }
}
