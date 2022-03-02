package com.kisman.cc.module.combat.autocrystal;

import com.kisman.cc.util.CrystalUtils;
import com.kisman.cc.util.EntityUtil;
import com.kisman.cc.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.TreeMap;

/**
 * @author Halq
 * @apiNote skidded from aurora client
 * @since 28/02/2022 20:32PM
 */

public class AI {

    public static final Minecraft mc = Minecraft.getMinecraft();

    public static AI instance = new AI();





    static BlockPos placePos;

    EntityPlayer targetPlayer;

    static HalqPos bestCrystalPos = new HalqPos(BlockPos.ORIGIN, 0);


    static class HalqPos {
        BlockPos blockPos;
        float targetDamage;

        public HalqPos(BlockPos blockPos, float targetDamage) {
            this.blockPos = blockPos;
            this.targetDamage = targetDamage;
        }

        public float getTargetDamage() {
            return targetDamage;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }
    }


    public HalqPos placeCalculateAI() {

        EntityPlayer targetPlayer = null;

        for (BlockPos pos : AIutils.getSphere(AutoCrystal.instance.placeRange.getValFloat())) {
            float targetDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, targetPlayer, true);
            float selfDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player, true);

            if (CrystalUtils.canPlaceCrystal(pos, true, true, false)) {

                if (mc.player.getDistance(pos.getX() + 0.5f, pos.getY() + 1.0f, pos.getZ() + 0.5f) > MathUtil.square(AutoCrystal.instance.placeRange.getValFloat()))
                    continue;

                if (selfDamage > AutoCrystal.instance.maxSelfDMG.getValFloat())
                    continue;

                if (targetDamage < AutoCrystal.instance.minDMG.getValFloat()) ;
            }
        }
        return null;
    }

    public static EnumFacing getEnumFacing(boolean rayTrace, BlockPos placePosition) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(placePosition.getX() + 0.5, placePosition.getY() - 0.5, placePosition.getZ() + 0.5));

        if (placePosition.getY() == 255)
            return EnumFacing.DOWN;

        if (rayTrace) {
            return (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        }

        return EnumFacing.UP;
    }
}
