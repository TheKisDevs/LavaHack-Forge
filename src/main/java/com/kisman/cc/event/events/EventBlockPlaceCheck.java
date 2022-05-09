package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EventBlockPlaceCheck extends Event {

    private final World world;

    private final BlockPos blockPos;

    private final EnumFacing side;

    private final EntityPlayer player;

    private final ItemStack itemStack;

    public EventBlockPlaceCheck(World world, BlockPos blockPos, EnumFacing side, EntityPlayer player, ItemStack itemStack) {
        this.world = world;
        this.blockPos = blockPos;
        this.side = side;
        this.player = player;
        this.itemStack = itemStack;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public EnumFacing getSide() {
        return side;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
