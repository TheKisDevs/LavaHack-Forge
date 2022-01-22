package com.kisman.cc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import static com.kisman.cc.util.BlockUtil.getPlaceableSide;

public class BlockUtil2 {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static boolean isPositionPlaceable(BlockPos position, boolean sideCheck, boolean entityCheck) {
        if (!mc.world.getBlockState(position).getBlock().isReplaceable(mc.world, position)) return false;
        if (entityCheck) {
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(position))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                return false;
            }
        }
        if (sideCheck) return getPlaceableSide(position) != null;
        return true;
    }

    public static void placeBlock(BlockPos position, EnumHand hand, boolean packet) {
        if (!mc.world.getBlockState(position).getBlock().isReplaceable(mc.world, position)) return;
        if (getPlaceableSide(position) == null) return;
        clickBlock(position, getPlaceableSide(position), hand, packet);
        mc.player.connection.sendPacket(new CPacketAnimation(hand));
    }

    public static void clickBlock(BlockPos position, EnumFacing side, EnumHand hand, boolean packet) {
        if (packet) mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(position.offset(side), side.getOpposite(), hand, Float.intBitsToFloat(Float.floatToIntBits(17.735476f) ^ 0x7E8DE241), Float.intBitsToFloat(Float.floatToIntBits(26.882437f) ^ 0x7ED70F3B), Float.intBitsToFloat(Float.floatToIntBits(3.0780227f) ^ 0x7F44FE53)));
        else mc.playerController.processRightClickBlock(mc.player, mc.world, position.offset(side), side.getOpposite(), new Vec3d((Vec3i)position), hand);
    }

    public static boolean isPositionPlaceable(BlockPos position, boolean sideCheck, boolean entityCheck, boolean ignoreCrystals) {
        if (!mc.world.getBlockState(position).getBlock().isReplaceable(mc.world, position)) return false;
        if (entityCheck) {
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(position))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityEnderCrystal && ignoreCrystals != false) continue;
                return false;
            }
        }
        return !sideCheck || getPlaceableSide(position) != null;
    }
}
