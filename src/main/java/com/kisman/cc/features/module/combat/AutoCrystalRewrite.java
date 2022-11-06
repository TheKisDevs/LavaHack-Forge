package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.enums.dynamic.SwapEnum2;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.world.CrystalUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class AutoCrystalRewrite extends Module {

    private final SettingEnum<Safety> safety = new SettingEnum<>("Safety", this, Safety.None).register();
    private final Setting safetyBalance = register(new Setting("SafetyBalance", this, 2, 0, 10, false));

    private final SettingEnum<SwapEnum2.Swap> swap = new SettingEnum<>("Switch", this, SwapEnum2.Swap.None).register();
    private final Setting swapDelay = register(new Setting("SwapDelay", this, 0, 0, 10, false));
    private final SettingEnum<SwapEnum2.Swap> antiWeakness = new SettingEnum<>("AntiWeakness", this, SwapEnum2.Swap.None);

    private final SettingEnum<TargetMode> targetMode = new SettingEnum<>("TargetMode", this, TargetMode.Closest).register();
    private final Setting targetRange = register(new Setting("TargetRange", this, 12, 1, 16, false));

    private final Setting placeRange = register(new Setting("PlaceRange", this, 5, 0, 6, false));
    private final Setting placeWallRange = register(new Setting("PlaceWallRange", this, 3, 0, 6, false));
    private final Setting firePlace = register(new Setting("FirePlace", this, false));
    private final Setting terrain = register(new Setting("Terrain", this, false));

    private final Setting minPlaceDamage = register(new Setting("MinPlaceDamage", this, 5, 0, 36, false));
    private final Setting maxSelfPlace = register(new Setting("MaxSelfPlace", this, 8, 0, 36, false));

    public AutoCrystalRewrite(){
        super("AutoCrystalRewrite", Category.COMBAT);
    }

    private EntityPlayer target;

    private PositionInfo calculatePlace(){
        if(target == null)
            return null;

        PositionInfo positionInfo = new PositionInfo(BlockPos.ORIGIN, -1, -1);

        for(BlockPos pos : CrystalUtils.getSphere(placeRange.getValFloat(), true, false)){

            if(
                    !isPosInRange(pos)
                    || !CrystalUtils.canPlaceCrystal(
                            pos,
                            true,
                            true,
                            false,
                            firePlace.getValBoolean()
                    )
            ){
                continue;
            }

            double damage = CrystalUtils.calculateDamage(
                    mc.world,
                    pos.getX() + 0.5,
                    pos.getY() + 1,
                    pos.getZ() + 0.5,
                    target,
                    terrain.getValBoolean()
            );

            if(damage < minPlaceDamage.getValDouble())
                continue;

            double selfDamage = CrystalUtils.calculateDamage(
                    mc.world,
                    pos.getX() + 0.5,
                    pos.getY() + 1,
                    pos.getZ() + 0.5,
                    mc.player,
                    terrain.getValBoolean()
            );

            if(selfDamage > maxSelfPlace.getValDouble())
                continue;

            PositionInfo place = new PositionInfo(pos, damage, selfDamage);

            positionInfo = positionInfo.max(place);
        }

        return positionInfo.targetDamage < 0 ? null : positionInfo;
    }

    private double getSafetyDamage(double damage, double selfDamage){
        switch(safety.getValEnum()){
            case None:
                return damage;
            case MinMax:
                return damage - selfDamage;
            case Balance:
                return (damage + (safetyBalance.getValDouble() * 0.5)) - (selfDamage + safetyBalance.getValDouble());
        }
        return damage;
    }

    private EntityPlayer getTarget(){
        EntityPlayer currentTarget = null;
        double minHealth = 36;
        double maxDamage = 0.5;
        double minDistance = targetRange.getValDouble() + 1;

        for(EntityPlayer player : mc.world.getEntitiesWithinAABB(EntityPlayer.class, playerRelativeAABB(targetRange.getValDouble()))){
            if(isTargetNotValid(player, targetRange.getValDouble()))
                continue;

            double health = player.getHealth() + player.getAbsorptionAmount();
            double damage = getDamageForPlayer(player);
            double distance = mc.player.getDistance(player);

            if(
                    targetMode.getValEnum() == TargetMode.Closest
                    && distance < minDistance
            ){
                currentTarget = player;
                minDistance = distance;
                continue;
            }


            if(
                    targetMode.getValEnum() == TargetMode.Health
                    && health < minHealth
            ){
                currentTarget = player;
                minDistance = health;
                continue;
            }

            if(
                    targetMode.getValEnum() == TargetMode.Damage
                    && damage > maxDamage
            ){
                currentTarget = player;
                maxDamage = damage;
            }
        }

        return currentTarget;
    }

    private AxisAlignedBB playerRelativeAABB(double range){
        return new AxisAlignedBB(
                mc.player.posX - range,
                mc.player.posY - range,
                mc.player.posZ - range,
                mc.player.posX + range,
                mc.player.posY + range,
                mc.player.posZ + range
        );
    }

    private boolean isTargetNotValid(EntityPlayer player, double range){
        return mc.player.getDistanceSq(player) > range
                ||  player.equals(mc.player)
                || player.getHealth() <= 0.0f
                || player.isDead
                || FriendManager.instance.isFriend(player.getName());
    }

    private float getDamageForPlayer(EntityPlayer player){
        float maxDamage = 0.5f;
        for(BlockPos pos : CrystalUtils.getSphere(placeRange.getValFloat(), true, false)){
            if(
                    isPosInRange(pos)
                    && CrystalUtils.canPlaceCrystal(
                            pos,
                            true,
                            true,
                            false,
                            firePlace.getValBoolean()
                    )
            ){
                maxDamage = Math.max(
                        maxDamage,
                        CrystalUtils.calculateDamage(
                                mc.world,
                                pos.getX() + 0.5,
                                pos.getY() + 1,
                                pos.getZ() + 0.5,
                                player,
                                terrain.getValBoolean()
                        )
                );
            }
        }
        return maxDamage;
    }

    private boolean isPosInRange(BlockPos pos){
        return mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= (EntityUtil.canSee(pos) ? placeRange.getValDouble() : placeWallRange.getValDouble());
    }

    private enum Safety {
        None,
        MinMax,
        Balance
    }

    private enum TargetMode {
        Closest,
        Health,
        Damage
    }

    private static class PositionInfo {

        private final BlockPos blockPos;

        private final double targetDamage;

        private final double selfDamage;

        public PositionInfo(BlockPos blockPos, double targetDamage, double selfDamage) {
            this.blockPos = blockPos;
            this.targetDamage = targetDamage;
            this.selfDamage = selfDamage;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public double getTargetDamage() {
            return targetDamage;
        }

        public double getSelfDamage() {
            return selfDamage;
        }

        public PositionInfo max(PositionInfo other){
            return other.targetDamage > targetDamage ? other : this;
        }
    }
}
