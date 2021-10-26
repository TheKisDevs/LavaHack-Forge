package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.BlockUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class CrystalBasePlace extends Module {
    private Setting switchMode = new Setting("SwitchMode", this, "Normal", new ArrayList<>(Arrays.asList("Normal", "Silent", "Strict")));
    private Setting caCheck = new Setting("AutoCrystalCheck", this, true);
    private Setting yCheck = new Setting("YCheck", this, true);


    private Setting rangeLine = new Setting("RangeLine", this, "Range");

    private Setting placeRange = new Setting("PlaceRange", this, Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(0.007834314f) ^ 0x7CA05B7F)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(2.8965124E37f) ^ 0x7DAE53D7)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(0.67379206f) ^ 0x7E0C7DA3)), false);
    private Setting targetRange = new Setting("TargetRange", this, Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(0.008263736f) ^ 0x7D07649F)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(2.7480012E38f) ^ 0x7F4EBC94)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(0.36163035f) ^ 0x7F49279D)), false);


    private Setting damageLine = new Setting("DamageLine", this, "Damage");

    private Setting minDMG = new Setting("MinDMG", this, Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(0.20678781f) ^ 0x7E93C02F)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(1.8581853E38f) ^ 0x7F0BCB59)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(0.021685882f) ^ 0x7EA1A697)), true);
    private Setting maxSelfDMG = new Setting("MaxSelfDMG", this, Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(0.10188567f) ^ 0x7D10A96F)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(2.8877588E38f) ^ 0x7F594036)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(0.02501596f) ^ 0x7EDCEE45)), true);

    private EntityLivingBase target;
    private BlockPos placedPosition;

    public CrystalBasePlace() {
        super("CrystalBasePlace", "CrystalBasePlace", Category.COMBAT);

        setmgr.rSetting(switchMode);
        setmgr.rSetting(caCheck);
        setmgr.rSetting(yCheck);

        setmgr.rSetting(rangeLine);
        setmgr.rSetting(placeRange);
        setmgr.rSetting(targetRange);

        setmgr.rSetting(damageLine);
        setmgr.rSetting(minDMG);
        setmgr.rSetting(maxSelfDMG);
    }

    public void onEnable() {
        target = null;
        placedPosition = null;

        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);

        target = null;
        placedPosition = null;
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener = new Listener<>(event -> {
        if (caCheck.getValBoolean() && !AutoCrystal.instance.isToggled()) {
            return;
        }
        int slot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        int lastSlot = mc.player.inventory.currentItem;
        BlockPos currentPosition = null;
        double maxDamage = Double.longBitsToDouble(Double.doubleToLongBits(1.1762782938084433E308) ^ 0x7FE4F03E2BCA7647L);
        if (slot == -1) {
            return;
        }
        this.target = getNearTarget((EntityPlayer) mc.player);
        if (this.target == null) {
            return;
        }
        if (this.placedPosition == null) {
//            if (caCheck.getValBoolean()) {
//                if (ModuleAutoCrystal.renderPosition != null) {
//                    return;
//                }
//            }
            for (BlockPos pos : CrystalUtils.getSphere((float) placeRange.getValDouble(), true, false)) {
                float targetDamage = this.filterPosition(pos);
                if (targetDamage == Float.intBitsToFloat(Float.floatToIntBits(-47.056423f) ^ 0x7DBC39C7) || !((double)targetDamage > maxDamage)) continue;
                maxDamage = targetDamage;
                currentPosition = pos;
            }
            if (currentPosition != null) {
                InventoryUtil.switchToSlot(slot, switchMode.getValString().equalsIgnoreCase("Silent"));
                BlockUtils.placeBlock(currentPosition, EnumHand.MAIN_HAND, true);
                this.placedPosition = currentPosition;
                if (switchMode.getValString().equalsIgnoreCase("Strict")) {
                    InventoryUtil.switchToSlot(lastSlot, switchMode.getValString().equalsIgnoreCase("Silent"));
                }
            }
        }
        if (this.placedPosition != null && this.filterPosition(this.placedPosition) == Float.intBitsToFloat(Float.floatToIntBits(-10.85126f) ^ 0x7EAD9EC3)) {
            this.placedPosition = null;
        }
    });

    public float filterPosition(BlockPos position) {
        if (yCheck.getValBoolean() && position.getY() != mc.player.getPosition().getY()) {
            return Float.intBitsToFloat(Float.floatToIntBits(-7.911857f) ^ 0x7F7D2DEF);
        }
        if (!mc.world.getBlockState((BlockPos)position).getBlock().isReplaceable((IBlockAccess)mc.world, (BlockPos)position) || !mc.world.getBlockState(position.up()).getBlock().isReplaceable((IBlockAccess)mc.world, position.up())) {
            return Float.intBitsToFloat(Float.floatToIntBits(-8.346309f) ^ 0x7E858A7B);
        }
        if (BlockUtils.isIntercepted((BlockPos)position) || BlockUtils.isIntercepted(position.up())) {
            return Float.intBitsToFloat(Float.floatToIntBits(-17.143347f) ^ 0x7E092593);
        }
        if (mc.player.getDistanceSq((BlockPos)position) > (double) MathUtil.square(placeRange.getValDouble())) {
            return Float.intBitsToFloat(Float.floatToIntBits(-7.7399526f) ^ 0x7F77ADB1);
        }
        float targetDamage = DamageUtil.calculateDamage((double)position.getX() + Double.longBitsToDouble(Double.doubleToLongBits(3.859775411443625) ^ 0x7FEEE0D1EE507148L), (double)position.getY() + Double.longBitsToDouble(Double.doubleToLongBits(11.291667435420424) ^ 0x7FD695556F20E0B9L), (double)position.getZ() + Double.longBitsToDouble(Double.doubleToLongBits(3.287754204373365) ^ 0x7FEA4D5213888F92L), this.target);
        float selfDamage = DamageUtil.calculateDamage((double)position.getX() + Double.longBitsToDouble(Double.doubleToLongBits(3.0321794875384644) ^ 0x7FE841E751B4A351L), (double)position.getY() + Double.longBitsToDouble(Double.doubleToLongBits(5.216471668479226) ^ 0x7FE4DDAABFC283ECL), (double)position.getZ() + Double.longBitsToDouble(Double.doubleToLongBits(70.4361477151085) ^ 0x7FB19BE9D81B276FL), mc.player);
        if (targetDamage < minDMG.getValDouble()) {
            if (targetDamage < this.target.getHealth() + this.target.getAbsorptionAmount()) {
                return Float.intBitsToFloat(Float.floatToIntBits(-7.3302693f) ^ 0x7F6A9191);
            }
        }
        if (selfDamage > maxSelfDMG.getValDouble()) {
            return Float.intBitsToFloat(Float.floatToIntBits(-8.953656f) ^ 0x7E8F422D);
        }
        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= selfDamage) {
            return Float.intBitsToFloat(Float.floatToIntBits(-6.2899423f) ^ 0x7F494735);
        }
        return targetDamage;
    }

    private EntityLivingBase getNearTarget(Entity distanceTarget) {
        return mc.world.loadedEntityList.stream()
                .filter(entity -> isValidTarget(entity))
                .filter(entity -> entity instanceof EntityPlayer)
                .map(entity -> (EntityLivingBase) entity)
                .min(Comparator.comparing(entity -> distanceTarget.getDistance(entity)))
                .orElse(null);
    }

    public boolean isValidTarget(Entity entity) {
        if (entity == null)
            return false;

        if (!(entity instanceof EntityLivingBase))
            return false;

        if (entity.isDead || ((EntityLivingBase)entity).getHealth() <= 0.0f)
            return false;

        if (entity.getDistance(mc.player) > targetRange.getValDouble())
            return false;

        if (entity instanceof EntityPlayer) {
            if (entity == mc.player)
                return false;

            return true;
        }

        return false;
    }
}
