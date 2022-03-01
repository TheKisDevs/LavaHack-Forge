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

/**
 * @author Halq
 * @since 28/02/2022 20:32PM
 */

public class AI {

    public static final Minecraft mc = Minecraft.getMinecraft();


    BlockPos blockPos;

    public static BlockPos placeCalculate(float range, float minDmg, float maxDmg) {

        EntityPlayer targetPlayer = null;

        for (BlockPos pos : CrystalUtils.getSphere(range, true, false)) {

            float targetDamage = EntityUtil.calculate(pos.getX(), pos.getY(), pos.getZ(), targetPlayer);
            float selfDamage = EntityUtil.calculate(pos.getX(), pos.getY(), pos.getZ(), mc.player);

            if (CrystalUtils.canPlaceCrystal(pos)) {

                if (mc.player.getDistance(pos.getX() + 0.5f, pos.getY() + 1.0f, pos.getZ() + 0.5f) > MathUtil.square(range))
                    continue;

                if (selfDamage > maxDmg)
                    continue;

                if (targetDamage < minDmg) ;
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
