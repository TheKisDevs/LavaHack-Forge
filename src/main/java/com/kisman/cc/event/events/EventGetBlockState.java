package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class EventGetBlockState extends Event {

    private final BlockPos pos;

    private IBlockState returnValue = null;

    public EventGetBlockState(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }

    public IBlockState getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(IBlockState returnValue) {
        this.returnValue = returnValue;
    }
}
