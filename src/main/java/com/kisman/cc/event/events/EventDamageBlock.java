package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EventDamageBlock extends Event {
    private BlockPos blockPos;
    private EnumFacing faceDirection;

    private float damage;

    private int delay;

    public EventDamageBlock(BlockPos blockPos, EnumFacing faceDirection, float damage, int delay) {
        this.blockPos = blockPos;
        this.faceDirection = faceDirection;
        this.damage = damage;
        this.delay = delay;
    }

    public EventDamageBlock(BlockPos pos, EnumFacing faceDirection) {
        this(pos, faceDirection, 0f, 0);
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public EnumFacing getFaceDirection() {
        return faceDirection;
    }


    public float getDamage() {
        return damage;
    }

    public int getDelay() {
        return delay;
    }
}