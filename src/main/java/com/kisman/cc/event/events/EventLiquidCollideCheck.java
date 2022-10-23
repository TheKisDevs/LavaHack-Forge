package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.block.state.IBlockState;

public class EventLiquidCollideCheck extends Event {

    private final IBlockState blockState;

    private final boolean hitIfLiquid;

    public EventLiquidCollideCheck(IBlockState blockState, boolean hitIfLiquid) {
        this.blockState = blockState;
        this.hitIfLiquid = hitIfLiquid;
    }

    public IBlockState getBlockState() {
        return blockState;
    }

    public boolean hitIfLiquid() {
        return hitIfLiquid;
    }
}
