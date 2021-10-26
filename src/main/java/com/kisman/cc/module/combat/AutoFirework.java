package com.kisman.cc.module.combat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.TimerUtils;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class AutoFirework extends Module {
    public static AutoFirework instance;

    private Setting delayLine = new Setting("DLine", this, "Delays");

    private Setting delay = new Setting("Delay", this, 1, 0, 20, true);
    private Setting trapDelay = new Setting("TrapDelay", this, 2, 0, 20, true);


    private Setting placeLine = new Setting("PlaceLine", this, "Place");

    private Setting placeMode = new Setting("PlaceMode", this, "Normal", new ArrayList<>(Arrays.asList("Normal", "Packet")));
    private Setting rayTrace = new Setting("RayTrace", this, true);
    private Setting onlyObby = new Setting("OnlyObby", this, false);
    private Setting range = new Setting("Range", this, 5, 0, 8, false);
    private Setting rotate = new Setting("Rotate", this, true);


    private Setting switchLine = new Setting("SwitchLine", this, "Switch");

    private Setting switchMode = new Setting("SwitchMode", this, InventoryUtil.Switch.NORMAL);
    private Setting switchObbyReturn = new Setting("SwitchReturnObby", this, true);
    private Setting switchFireReturn = new Setting("SwitchReturnFirework", this, true);


    private Setting pauseLine = new Setting("PauseLine", this, "Pause");

    private Setting minHealthPause = new Setting("MinHealthPause", this, false);
    private Setting requiredHealth = new Setting("RequiredHealth", this, 11, 0, 36, true);
    private Setting pauseWhileEating = new Setting("PauseWhileEating", this, false);
    private Setting pauseIfHittingBlock = new Setting("PauseIfHittingBlock", this, false);


    private Setting handLine = new Setting("HandLine", this, "Hand");
    private Setting fireHand = new Setting("FireworkHand", this, "Default", new ArrayList<>(Arrays.asList("Default", "MainHand", "OffHand")));

    private TimerUtils trapTimer = new TimerUtils();
    private TimerUtils delayTimer = new TimerUtils();

    private AimBot aimBot;
    public EntityLivingBase target = null;

    public AutoFirework() {
        super("AutoFirework", "", Category.COMBAT);

        aimBot = AimBot.instance;
        instance = this;

        setmgr.rSetting(delayLine);
        setmgr.rSetting(delay);
        setmgr.rSetting(trapDelay);

        setmgr.rSetting(placeLine);
        setmgr.rSetting(placeMode);
        setmgr.rSetting(rayTrace);
        setmgr.rSetting(onlyObby);
        setmgr.rSetting(range);
        setmgr.rSetting(rotate);

        setmgr.rSetting(switchLine);
        setmgr.rSetting(switchMode);
        setmgr.rSetting(switchObbyReturn);
        setmgr.rSetting(switchFireReturn);

        setmgr.rSetting(pauseLine);
        setmgr.rSetting(minHealthPause);
        setmgr.rSetting(requiredHealth);
        setmgr.rSetting(pauseWhileEating);
        setmgr.rSetting(pauseIfHittingBlock);

        setmgr.rSetting(handLine);
        setmgr.rSetting(fireHand);
    }

    public void onEnable() {
        if(!aimBot.isToggled()) {
            aimBot.setToggled(true);
        }

        aimBot.rotationSpoof = null;
    }

    public void onDisable() {
        aimBot.rotationSpoof = null;
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        if(target != null) {
            super.setDisplayInfo("[" +  target.getDisplayName().getFormattedText() + TextFormatting.GRAY + "]");
        } else {
            super.setDisplayInfo("");
        }

        if(needPause()) {
            return;
        }

        if(target != null) {
            BlockPos playerPos = target.getPosition();
            BlockPos trapPos = new BlockPos(playerPos.getX(), playerPos.getY() + 2, playerPos.getZ());

            if (mc.world.getBlockState(trapPos).getMaterial().isReplaceable()) {
                //plase trap

                if (trapTimer.passedTicks((int) trapDelay.getValDouble())) {
                    final int oldSlot = mc.player.inventory.getBestHotbarSlot();
                    int newSlot = InventoryUtil.getBlockInHotbar(onlyObby.getValBoolean());

                    //switch
                    if (newSlot == -1) {
                        super.setToggled(false);
                        ChatUtils.error(TextFormatting.RED + "No Blocks Found in HotBar");
                    } else {
                        InventoryUtil.switchToSlot(newSlot, (InventoryUtil.Switch) switchMode.getValEnum());
                    }

                    //place
                    BlockInteractionHelper.place(trapPos, (float) range.getValDouble(), rotate.getValBoolean(), false);

                    //switch return
                    if (switchObbyReturn.getValBoolean()) {
                        InventoryUtil.switchToSlot(oldSlot, (InventoryUtil.Switch) switchMode.getValEnum());
                    }

                    //reset timer
                    delayTimer.reset();
                    trapTimer.reset();
                }
            } else if (delayTimer.passedMillis((int) delay.getValDouble())) {
                //place firework

                if (Math.sqrt(mc.player.getDistanceSq(playerPos.getX(), playerPos.getY(), playerPos.getZ())) <= range.getValDouble()) {
                    //switch
                    final int oldSlot = mc.player.inventory.getBestHotbarSlot();
                    int newSlot = InventoryUtil.findItemInHotbar(Items.FIREWORKS.getClass());

                    //rotate
                    if (rotate.getValBoolean()) {
                        final double pos[] =  EntityUtil.calculateLookAt(
                                target.posX + 0.5,
                                target.posY - 0.5,
                                target.posZ + 0.5,
                                mc.player);

                        aimBot.rotationSpoof = new RotationSpoof((float) pos[0], (float) pos[1]);

                        Random rand = new Random(2);

                        aimBot.rotationSpoof.yaw += (rand.nextFloat() / 100);
                        aimBot.rotationSpoof.pitch += (rand.nextFloat() / 100);
                    }

                    //place
                    EnumFacing facing = null;

                    if(rayTrace.getValBoolean()) {
                        RayTraceResult result = mc.world.rayTraceBlocks(
                                new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ),
                                new Vec3d(target.posX + 0.5, target.posY - 0.5,
                                        target.posZ + 0.5));

                        if(result == null || result.sideHit  == null) {
                            facing = EnumFacing.UP;
                        } else {
                            facing = result.sideHit;
                        }
                    }

                    mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(playerPos, facing,
                            fireHand.getValString().equalsIgnoreCase("Default") ?
                                    mc.player.getHeldItemOffhand().getItem() == Items.FIREWORKS ? EnumHand.OFF_HAND : EnumHand.OFF_HAND :
                                    fireHand.getValString().equalsIgnoreCase("MainHand") ?
                                            EnumHand.MAIN_HAND :
                                            EnumHand.OFF_HAND,
                            0, 0, 0
                    ));

                    //switch return
                    if(switchFireReturn.getValBoolean()) {
                        InventoryUtil.switchToSlot(oldSlot, (InventoryUtil.Switch) switchMode.getValEnum());
                    }

                    //reset timer
                    delayTimer.reset();
                    trapTimer.reset();
                }
            }
        } else {
            findNewTarget();
        }
    }

    private void findNewTarget() {
        target = getNearTarget(mc.player);
    }

    private EntityLivingBase getNearTarget(Entity distanceTarget) {
        return mc.world.loadedEntityList.stream()
                .filter(entity -> isValidTarget(entity))
                .map(entity -> (EntityLivingBase) entity)
                .min(Comparator.comparing(entity -> distanceTarget.getDistance(entity)))
                .orElse(null);
    }

    private boolean needPause() {
        if(pauseWhileEating.getValBoolean() && PlayerUtil.IsEating()) {
            return true;
        }

        if(minHealthPause.getValBoolean()) {
            if(mc.player.getHealth() + mc.player.getAbsorptionAmount() < requiredHealth.getValDouble()) {
                return true;
            }
        }

        if(pauseIfHittingBlock.getValBoolean() && mc.playerController.isHittingBlock && mc.player.getHeldItemMainhand().getItem() instanceof ItemTool) {
            return true;
        }

        return false;
    }

    public boolean isValidTarget(Entity entity) {
        if (entity == null)
            return false;

        if (!(entity instanceof EntityLivingBase))
            return false;

        if (entity.isDead || ((EntityLivingBase)entity).getHealth() <= 0.0f)
            return false;

        if (entity.getDistance(mc.player) > 20.0f)
            return false;

        if (entity instanceof EntityPlayer) {
            if (entity == mc.player)
                return false;

            return true;
        }

        return false;
    }
}
