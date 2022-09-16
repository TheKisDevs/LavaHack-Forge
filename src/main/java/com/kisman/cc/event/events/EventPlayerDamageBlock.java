package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EventPlayerDamageBlock extends Event {

    private final BlockPos pos;

    private final EnumFacing facing;

    public EventPlayerDamageBlock(BlockPos pos, EnumFacing facing, Era era) {
        super(era);
        this.pos = pos;
        this.facing = facing;
    }

    public BlockPos getPos() {
        return pos;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public static class Pre extends EventPlayerDamageBlock {

        public Pre(BlockPos pos, EnumFacing facing) {
            super(pos, facing, Era.PRE);
        }
    }

    public static class Post extends EventPlayerDamageBlock {

        public Post(BlockPos pos, EnumFacing facing) {
            super(pos, facing, Era.POST);
        }
    }
}
