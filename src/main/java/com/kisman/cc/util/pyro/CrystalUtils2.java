package com.kisman.cc.util.pyro;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class CrystalUtils2 {
    private static Minecraft mc = Minecraft.getMinecraft();
    public static List<BlockPos> obbyRock;
    public static List<BlockPos> crystalBlocks;

    public static boolean canPlaceCrystalAt(final BlockPos blockpos, final IBlockState state) {
        final World worldIn = (World)mc.world;
        final BlockPos blockpos2 = blockpos.up();
        final BlockPos blockpos3 = blockpos.up().up();
        boolean flag = !worldIn.isAirBlock(blockpos2) && !worldIn.getBlockState(blockpos).getBlock().isReplaceable((IBlockAccess)worldIn, blockpos2);
        flag |= (!worldIn.isAirBlock(blockpos3) && !worldIn.getBlockState(blockpos3).getBlock().isReplaceable((IBlockAccess)worldIn, blockpos3));
        if (flag) {
            return false;
        }
        final double d0 = blockpos.getX();
        final double d2 = blockpos.getY();
        final double d3 = blockpos.getZ();
        return worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(d0, d2, d3, d0 + 1.0, d2 + 2.0, d3 + 1.0)).isEmpty();
    }
}
