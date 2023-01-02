package com.kisman.cc.util.world;

import com.kisman.cc.settings.util.EasingsPattern;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

import static com.kisman.cc.util.world.BlockUtil.getPlaceableSide;

public class BlockUtil2 {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void pushOutOfBlocks(
            Entity entity,
            double x,
            double y,
            double z
    ) {
        BlockPos pos = new BlockPos(x, y, z);
        double deltaX = x - pos.getX();
        double deltaY = y - pos.getY();
        double deltaZ = z - pos.getZ();

        if (mc.world.collidesWithAnyBlock(entity.getEntityBoundingBox())) {
            EnumFacing facing = EnumFacing.UP;

            double delta = Double.MAX_VALUE;

            if (!mc.world.isBlockFullCube(pos.west()) && deltaX < delta) {
                delta = deltaX;
                facing = EnumFacing.WEST;
            }

            if (!mc.world.isBlockFullCube(pos.east()) && 1.0D - deltaX < delta) {
                delta = 1.0D - deltaX;
                facing = EnumFacing.EAST;
            }

            if (!mc.world.isBlockFullCube(pos.north()) && deltaZ < delta) {
                delta = deltaZ;
                facing = EnumFacing.NORTH;
            }

            if (!mc.world.isBlockFullCube(pos.south()) && 1.0D - deltaZ < delta) {
                delta = 1.0D - deltaZ;
                facing = EnumFacing.SOUTH;
            }

            if (!mc.world.isBlockFullCube(pos.up()) && 1.0D - deltaY < delta) {
//                delta = 1.0D - deltaY;
                facing = EnumFacing.UP;
            }

            float f = new Random().nextFloat() * 0.2F + 0.1F;
            float f1 = (float)facing.getAxisDirection().getOffset();

            if (facing.getAxis() == EnumFacing.Axis.X) {
                entity.motionX = f1 * f;
                entity.motionY *= 0.75D;
                entity.motionZ *= 0.75D;
            } else if (facing.getAxis() == EnumFacing.Axis.Y) {
                entity.motionX *= 0.75D;
                entity.motionY = f1 * f;
                entity.motionZ *= 0.75D;
            } else if (facing.getAxis() == EnumFacing.Axis.Z) {
                entity.motionX *= 0.75D;
                entity.motionY *= 0.75D;
                entity.motionZ = f1 * f;
            }
        }
    }

    public static double getBreakingProgress(BlockPos pos, ItemStack stack, long start) {
        return MathUtil.clamp(1 - ((System.currentTimeMillis() - start) / (double) InventoryUtil.time(pos, stack)), 0, 1);
    }

    public static AxisAlignedBB getProgressBB(BlockPos pos, ItemStack stack, long start) {
        return getProgressBB(
                mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos),
                getBreakingProgress(pos, stack, start)
        );
    }

    public static AxisAlignedBB getMutableProgressBB(BlockPos pos, ItemStack stack, long start, EasingsPattern scalier) {
        return getProgressBB(
                mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos),
                scalier.mutateProgress(getBreakingProgress(pos, stack, start))
        );
    }

    public static AxisAlignedBB getMutableProgressBB2(BlockPos pos, ItemStack stack, long start, EasingsPattern scalier) {
        return getProgressBB(
                mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos),
                scalier.mutateProgress(getBreakingProgress(pos, stack, start))
        );
    }

    public static AxisAlignedBB getProgressBB(AxisAlignedBB fullBB, double progress) {
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
}
