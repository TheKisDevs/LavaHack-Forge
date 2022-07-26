package com.kisman.cc.util.world;

import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.math.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.ArrayList;

import static com.kisman.cc.util.world.BlockUtil.getPlaceableSide;

public class BlockUtil2 {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float getBreakingProgress(BlockPos pos, ItemStack stack, long start) {
        return (float) MathUtil.clamp(1 - ((System.currentTimeMillis() - start) / (double) InventoryUtil.time(pos, stack)), 0, 1);
    }

    public static AxisAlignedBB getProgressBB(BlockPos pos, ItemStack stack, long start) {
        return getProgressBB(
                mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos),
                getBreakingProgress(pos, stack, start)
        );
    }

    public static AxisAlignedBB getProgressBB(AxisAlignedBB fullBB, float progress) {
        return new AxisAlignedBB(
            (fullBB.minX + (fullBB.getCenter().x - fullBB.minX) * progress),
            (fullBB.minY + (fullBB.getCenter().y - fullBB.minY) * progress),
            (fullBB.minZ + (fullBB.getCenter().z - fullBB.minZ) * progress),
            (fullBB.maxX + (fullBB.getCenter().x - fullBB.maxX) * progress),
            (fullBB.maxY + (fullBB.getCenter().y - fullBB.maxY) * progress),
            (fullBB.maxZ + (fullBB.getCenter().z - fullBB.maxZ) * progress)
        );
    }

    public static float getHardness(BlockPos pos) {
        return mc.world.getBlockState(pos).getPlayerRelativeBlockHardness(mc.player, mc.world, pos);
    }

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

    public static boolean placeBlock(BlockPos position, EnumHand hand, boolean packet) {
        if (!mc.world.getBlockState(position).getBlock().isReplaceable(mc.world, position)) return false;
        if (getPlaceableSide(position) == null) return false;
        clickBlock(position, getPlaceableSide(position), hand, packet);
        mc.player.connection.sendPacket(new CPacketAnimation(hand));
        return true;
    }

    public static void clickBlock(BlockPos position, EnumFacing side, EnumHand hand, boolean packet) {
        if (packet) mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(position.offset(side), side.getOpposite(), hand, Float.intBitsToFloat(Float.floatToIntBits(17.735476f) ^ 0x7E8DE241), Float.intBitsToFloat(Float.floatToIntBits(26.882437f) ^ 0x7ED70F3B), Float.intBitsToFloat(Float.floatToIntBits(3.0780227f) ^ 0x7F44FE53)));
        else mc.playerController.processRightClickBlock(mc.player, mc.world, position.offset(side), side.getOpposite(), new Vec3d(position), hand);
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

    public static CPacketPlayer.Rotation placeBlockGetRotate(BlockPos blockPos, EnumHand hand, boolean checkAction, ArrayList<EnumFacing> forceSide, boolean swingArm, boolean sneak) {
        if (mc.player == null || mc.world == null || mc.playerController == null) return null;
        if (!mc.world.getBlockState(blockPos).getMaterial().isReplaceable()) return null;
        EnumFacing side = forceSide != null ? BlockUtil.getPlaceableSideExlude(blockPos, forceSide) : BlockUtil.getPlaceableSide(blockPos);
        if (side == null) return null;
        BlockPos neighbour = blockPos.offset(side);
        EnumFacing opposite = side.getOpposite();
        if (!BlockUtil.canBeClicked(neighbour)) return null;
        Vec3d hitVec = new Vec3d(neighbour).add(new Vec3d(0.5, 0.5, 0.5)).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        if (!mc.player.isSneaking() && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock) && sneak) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
        }

        EnumActionResult action = mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, hand);
        if (!checkAction || action == EnumActionResult.SUCCESS) {
            if (swingArm) {
                mc.player.swingArm(hand);
                mc.rightClickDelayTimer = 4;
            } else mc.player.connection.sendPacket(new CPacketAnimation(hand));
        }

        return getFaceVectorPacket(hitVec, true);
    }

    public static CPacketPlayer.Rotation getFaceVectorPacket(Vec3d vec, Boolean roundAngles) {
        float[] rotations = RotationUtils.getNeededRotations2(vec);
        CPacketPlayer.Rotation e = new CPacketPlayer.Rotation(rotations[0], roundAngles ? MathHelper.normalizeAngle((int) rotations[1], 360) : rotations[1], mc.player.onGround);
        mc.player.connection.sendPacket(e);
        return e;
    }
}
