package com.kisman.cc.module.combat.autocrystal;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.CrystalUtils;
import com.kisman.cc.util.MathUtil;
import com.kisman.cc.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.TreeMap;

/**
 * @author Halq
 * @since 28/02/22 20:32PM
 */

public class AutoCrystal extends Module {

    public static AutoCrystal instance = new AutoCrystal();

    public final Setting placeRange = new Setting("PlaceRange", this, 4, 1, 6, true);
    public final Setting packetPlace = new Setting("PacketPlace", this, true);
    public final Setting minDMG = new Setting("MinDmg", this, 6, 0, 37, true);
    public final Setting maxSelfDMG = new Setting("MaxSelfDMG", this, 18, 0, 80, true);
    public final Setting placeDelay = new Setting("PlaceDelay", this, 4, 1, 80, true);

    static AI.HalqPos bestCrystalPos = new AI.HalqPos(BlockPos.ORIGIN, 0);

    public AI.HalqPos placeCalculateAI() {

        EntityPlayer targetPlayer = null;

        TreeMap<Float, AI.HalqPos> posList = new TreeMap<>();
        for (BlockPos pos : AIutils.getSphere(placeRange.getValFloat())) {
            float targetDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, targetPlayer, true);
            float selfDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player, true);

            if (CrystalUtils.canPlaceCrystal(pos, true, true, false)) {

                if (mc.player.getDistance(pos.getX() + 0.5f, pos.getY() + 1.0f, pos.getZ() + 0.5f) > MathUtil.square(AutoCrystal.instance.placeRange.getValFloat()))
                    continue;

                if (selfDamage > maxSelfDMG.getValFloat())
                    continue;

                if (targetDamage < minDMG.getValFloat()) ;
                posList.put(targetDamage, new AI.HalqPos(pos, targetDamage));
            }
        }
        if (!posList.isEmpty()) {
            return posList.lastEntry().getValue();
        }
        return null;
    }

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

    public AutoCrystal() {
        super("AutoCrystal", Category.COMBAT);

        setmgr.rSetting(placeRange);
        setmgr.rSetting(packetPlace);
        setmgr.rSetting(minDMG);
        setmgr.rSetting(maxSelfDMG);
        setmgr.rSetting(placeDelay);

    }

    Timer placeTimer = new Timer();
    EntityPlayer targetPlayer;

    public void update() {
        doPlace();
    }

    public void doPlace() {

        bestCrystalPos = placeCalculateAI();

            if (bestCrystalPos == null) {

                if (placeTimer.passedDms(placeDelay.getValDouble())) {
                    if (packetPlace.getValBoolean()) {
                        mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(placeCalculateAI().getBlockPos(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                    } else {
                        mc.playerController.processRightClickBlock(mc.player, mc.world, placeCalculateAI().getBlockPos(), EnumFacing.UP, new Vec3d(0, 0, 0), mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                    }
                    //place end
                }
                placeTimer.reset();
            }
            placeTimer.reset();
        }
}
