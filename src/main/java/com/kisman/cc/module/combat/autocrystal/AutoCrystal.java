package com.kisman.cc.module.combat.autocrystal;

import com.kisman.cc.module.*;
import com.kisman.cc.oldclickgui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.kisman.cc.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.Vec3d;

/**
 * @author Halq
 * @since 28/02/22 20:32PM
 */

public class AutoCrystal extends Module {
    public static AutoCrystal instance = new AutoCrystal();

    public final Setting placeRange = new Setting("PlaceRange", this, 4, 1, 6, true);
    public final Setting placeWallRange = new Setting("Place Wall Range", this, 4.5f, 0, 6, false);
    public final Setting breakRange = new Setting("Break Range", this, 6, 0, 6, false);
    public final Setting breakWallRange = new Setting("Break Wall Range", this, 4.5f, 0, 6, false);
    public final Setting targetRange = new Setting("Target Range", this, 15, 1, 20, true);
    public final Setting packetPlace = new Setting("PacketPlace", this, true);
    public final Setting minDMG = new Setting("MinDmg", this, 6, 0, 37, true);
    public final Setting maxSelfDMG = new Setting("MaxSelfDMG", this, 18, 0, 80, true);
    public final Setting placeDelay = new Setting("PlaceDelay", this, 4, 0, 80, true);
    public final Setting breakDelay = new Setting("Break Delay", this, 4, 0, 80, true);
    public final Setting lethalMult = new Setting("Lethal Mult", this, 0, 0, 6, false);
    public final Setting clientSide = new Setting("Client Side", this, false);
    public final Setting armorBreaker = new Setting("Armor Breaker", this, 100, 0, 100, Slider.NumberType.PERCENT);
    public final Setting switchMode = new Setting("Switch Mode", this, SwitchMode.None);
    public final Setting terrain = new Setting("Terrain", this, false);
    public final Setting place = new Setting("Place", this, true);
    public final Setting break_ = new Setting("Break", this, true);

    static AI.HalqPos bestCrystalPos = new AI.HalqPos(BlockPos.ORIGIN, 0);

    public AI.HalqPos placeCalculateAI() {
        AI.HalqPos posToReturn = new AI.HalqPos(BlockPos.ORIGIN, 0.5f);
        for (BlockPos pos : AIUtils.getSphere(placeRange.getValFloat())) {
            float targetDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, target, terrain.getValBoolean());
            float selfDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player, terrain.getValBoolean());

            if (CrystalUtils.canPlaceCrystal(pos, true, true, false)) {
                if (mc.player.getDistance(pos.getX() + 0.5f, pos.getY() + 1.0f, pos.getZ() + 0.5f) > MathUtil.square(placeRange.getValFloat())) continue;
                if (selfDamage > maxSelfDMG.getValFloat()) continue;
                if (targetDamage < minDMG.getValFloat()) continue;
                if(targetDamage > posToReturn.getTargetDamage()) posToReturn = new AI.HalqPos(pos, targetDamage);
            }
        }
        return posToReturn;
    }

    public AutoCrystal() {
        super("AutoCrystal", Category.COMBAT);
        
        instance = this;

        setmgr.rSetting(placeRange);
        setmgr.rSetting(placeWallRange);
        setmgr.rSetting(breakRange);
        setmgr.rSetting(breakWallRange);
        setmgr.rSetting(targetRange);
        setmgr.rSetting(packetPlace);
        setmgr.rSetting(minDMG);
        setmgr.rSetting(maxSelfDMG);
        setmgr.rSetting(placeDelay);
        setmgr.rSetting(breakDelay);
        setmgr.rSetting(lethalMult);
        setmgr.rSetting(clientSide);
        setmgr.rSetting(armorBreaker);
        setmgr.rSetting(switchMode);
    }

    private final Timer placeTimer = new Timer(), breakTimer = new Timer();
    public EntityPlayer target;

    public void onEnable() {
        placeTimer.reset();
        breakTimer.reset();
        bestCrystalPos = new AI.HalqPos(BlockPos.ORIGIN, 0);
    }

    public void onDisable() {
        target = null;
        super.setDisplayInfo("");
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        target = EntityUtil.getTarget(targetRange.getValFloat());
        if(target == null) {
            super.setDisplayInfo("");
            return;
        }
        super.setDisplayInfo("[" + target.getName() + "]");
        if(place.getValBoolean()) doPlace();
        if(break_.getValBoolean()) doBreak();
    }

    public void doPlace() {
        bestCrystalPos = placeCalculateAI();
            if (bestCrystalPos.getBlockPos() != BlockPos.ORIGIN) {
                if (placeTimer.passedDms(placeDelay.getValDouble())) {
                    int crystalSlot = InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9), oldSlot = mc.player.inventory.currentItem;
                    boolean canSwitch = true, offhand = mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL);
                    if(crystalSlot != mc.player.inventory.currentItem && switchMode.getValString().equalsIgnoreCase("None") && !offhand) return;
                    if(crystalSlot == mc.player.inventory.currentItem || offhand) canSwitch = false;
                    if(canSwitch) InventoryUtil.switchToSlot(crystalSlot, switchMode.getValString().equalsIgnoreCase("Silent"));
                    if (packetPlace.getValBoolean()) mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(bestCrystalPos.getBlockPos(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                    else mc.playerController.processRightClickBlock(mc.player, mc.world, bestCrystalPos.getBlockPos(), EnumFacing.UP, new Vec3d(0, 0, 0), mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                    placeTimer.reset();
                    if(switchMode.getValString().equalsIgnoreCase("Silent")) InventoryUtil.switchToSlot(oldSlot, true);
                    //place end
                }
            }
    }

    public void doBreak() {
        if(!breakTimer.passedDms(breakDelay.getValFloat())) return;
        Entity crystal = getCrystalWithMaxDamage();
        if(crystal == null) return;
        mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        try {if(clientSide.getValBoolean()) mc.world.removeEntityFromWorld(crystal.entityId);} catch (Exception ignored) {}
        breakTimer.reset();
    }

    private Entity getCrystalWithMaxDamage() {
        Entity crystal = null;
        double maxDamage = 0.5;

        for(int i = 0; i < mc.world.loadedEntityList.size(); ++i) {
            Entity entity = mc.world.loadedEntityList.get(i);

            if(entity instanceof EntityEnderCrystal && mc.player.getDistance(entity) < (mc.player.canEntityBeSeen(entity) ? breakRange.getValDouble() : breakWallRange.getValDouble())) {
                double targetDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, target, terrain.getValBoolean());

                if(targetDamage > minDMG.getValInt() || targetDamage * lethalMult.getValDouble() > target.getHealth() + target.getAbsorptionAmount() || InventoryUtil.isArmorUnderPercent(target, armorBreaker.getValInt())) {
                    double selfDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, mc.player, terrain.getValBoolean());

                    if(selfDamage <= maxSelfDMG.getValInt() && selfDamage + 2 <= mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage) {
                        if(maxDamage <= targetDamage) {
                            maxDamage = targetDamage;
                            crystal = entity;
                        }
                    }
                }
            }
        }

        return crystal;
    }

    public enum SwitchMode {None, Normal, Silent}
}
