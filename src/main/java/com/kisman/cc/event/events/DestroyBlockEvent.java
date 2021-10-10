package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.util.math.BlockPos;

public class DestroyBlockEvent extends Event {
    private BlockPos blockPos;

    public DestroyBlockEvent(BlockPos blockPos) {
        super();
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }
}
