package com.kisman.cc.features.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Beta;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.SettingEnum;
import com.kisman.cc.settings.types.SettingGroup;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.ForgeHooks;
import org.lwjgl.input.Keyboard;

/**
 * @author Cubic
 * @since 19.08.2022
 */

@Beta
public class Octopus extends Module {

    private static final Mode DEFAULT = Mode.LeftClick;

    private final SettingEnum<Swap> swap = new SettingEnum<>("Switch", this, Swap.Normal).register();

    private final SettingGroup slot1 = register(new SettingGroup(new Setting("Slot 1", this)));
    private final Setting slot1Active = register(slot1.add(new Setting("Slot1Active", this, true).setTitle("Active")));
    private final Setting slot1Mode = register(slot1.add(new Setting("Slot1Mode", this, DEFAULT)).setTitle("Mode"));
    private final Setting slot1Key = register(slot1.add(new Setting("Slot1Key", this, 0).setTitle("Keybind")));

    private final SettingGroup slot2 = register(new SettingGroup(new Setting("Slot 2", this)));
    private final Setting slot2Active = register(slot2.add(new Setting("Slot1Active", this, true).setTitle("Active")));
    private final Setting slot2Mode = register(slot2.add(new Setting("Slot1Mode", this, DEFAULT)).setTitle("Mode"));
    private final Setting slot2Key = register(slot2.add(new Setting("Slot1Key", this, 0).setTitle("Keybind")));

    private final SettingGroup slot3 = register(new SettingGroup(new Setting("Slot 3", this)));
    private final Setting slot3Active = register(slot3.add(new Setting("Slot1Active", this, true).setTitle("Active")));
    private final Setting slot3Mode = register(slot3.add(new Setting("Slot1Mode", this, DEFAULT)).setTitle("Mode"));
    private final Setting slot3Key = register(slot3.add(new Setting("Slot1Key", this, 0).setTitle("Keybind")));

    private final SettingGroup slot4 = register(new SettingGroup(new Setting("Slot 4", this)));
    private final Setting slot4Active = register(slot4.add(new Setting("Slot1Active", this, true).setTitle("Active")));
    private final Setting slot4Mode = register(slot4.add(new Setting("Slot1Mode", this, DEFAULT)).setTitle("Mode"));
    private final Setting slot4Key = register(slot4.add(new Setting("Slot1Key", this, 0).setTitle("Keybind")));

    private final SettingGroup slot5 = register(new SettingGroup(new Setting("Slot 5", this)));
    private final Setting slot5Active = register(slot5.add(new Setting("Slot1Active", this, true).setTitle("Active")));
    private final Setting slot5Mode = register(slot5.add(new Setting("Slot1Mode", this, DEFAULT)).setTitle("Mode"));
    private final Setting slot5Key = register(slot5.add(new Setting("Slot1Key", this, 0).setTitle("Keybind")));

    private final SettingGroup slot6 = register(new SettingGroup(new Setting("Slot 6", this)));
    private final Setting slot6Active = register(slot6.add(new Setting("Slot1Active", this, true).setTitle("Active")));
    private final Setting slot6Mode = register(slot6.add(new Setting("Slot1Mode", this, DEFAULT)).setTitle("Mode"));
    private final Setting slot6Key = register(slot6.add(new Setting("Slot1Key", this, 0).setTitle("Keybind")));

    private final SettingGroup slot7 = register(new SettingGroup(new Setting("Slot 7", this)));
    private final Setting slot7Active = register(slot7.add(new Setting("Slot1Active", this, true).setTitle("Active")));
    private final Setting slot7Mode = register(slot7.add(new Setting("Slot1Mode", this, DEFAULT)).setTitle("Mode"));
    private final Setting slot7Key = register(slot7.add(new Setting("Slot1Key", this, 0).setTitle("Keybind")));

    private final SettingGroup slot8 = register(new SettingGroup(new Setting("Slot 8", this)));
    private final Setting slot8Active = register(slot8.add(new Setting("Slot1Active", this, true).setTitle("Active")));
    private final Setting slot8Mode = register(slot8.add(new Setting("Slot1Mode", this, DEFAULT)).setTitle("Mode"));
    private final Setting slot8Key = register(slot8.add(new Setting("Slot1Key", this, 0).setTitle("Keybind")));

    private final SettingGroup slot9 = register(new SettingGroup(new Setting("Slot 9", this)));
    private final Setting slot9Active = register(slot9.add(new Setting("Slot1Active", this, true).setTitle("Active")));
    private final Setting slot9Mode = register(slot9.add(new Setting("Slot1Mode", this, DEFAULT)).setTitle("Mode"));
    private final Setting slot9Key = register(slot9.add(new Setting("Slot1Key", this, 0).setTitle("Keybind")));

    public Octopus(){
        super("Octopus", Category.PLAYER);
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null)
            return;

        if(slot1Active.getValBoolean() && Keyboard.isKeyDown(slot1Key.getKey()))
            action((Mode) slot1Mode.getValEnum(), 1);

        if(slot2Active.getValBoolean() && Keyboard.isKeyDown(slot2Key.getKey()))
            action((Mode) slot2Mode.getValEnum(), 2);

        if(slot3Active.getValBoolean() && Keyboard.isKeyDown(slot3Key.getKey()))
            action((Mode) slot3Mode.getValEnum(), 3);

        if(slot4Active.getValBoolean() && Keyboard.isKeyDown(slot4Key.getKey()))
            action((Mode) slot4Mode.getValEnum(), 4);

        if(slot5Active.getValBoolean() && Keyboard.isKeyDown(slot5Key.getKey()))
            action((Mode) slot5Mode.getValEnum(), 5);

        if(slot6Active.getValBoolean() && Keyboard.isKeyDown(slot6Key.getKey()))
            action((Mode) slot6Mode.getValEnum(), 6);

        if(slot7Active.getValBoolean() && Keyboard.isKeyDown(slot7Key.getKey()))
            action((Mode) slot7Mode.getValEnum(), 7);

        if(slot8Active.getValBoolean() && Keyboard.isKeyDown(slot8Key.getKey()))
            action((Mode) slot8Mode.getValEnum(), 8);

        if(slot9Active.getValBoolean() && Keyboard.isKeyDown(slot9Key.getKey()))
            action((Mode) slot9Mode.getValEnum(), 9);
    }

    private void action(Mode mode, int slot){

        int oldSlot = mc.player.inventory.currentItem;

        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;

        if(mode == Mode.LeftClick)
            clickMouse();
        else if(mode == Mode.RightClick)
            rightClickMouse();
        else
            middleClickMouse();

        if(swap.getValEnum() == Swap.SwitchBack){
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            mc.player.inventory.currentItem = oldSlot;
        }
    }

    enum Mode {
        LeftClick,
        RightClick,
        MiddleClick
    }

    enum Swap {
        Normal,
        SwitchBack
    }

    private void clickMouse() {
        if (mc.leftClickCounter <= 0) {
            if (mc.objectMouseOver == null) {
                Kisman.LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
                if (mc.playerController.isNotCreative()) {
                    mc.leftClickCounter = 10;
                }
            } else if (!mc.player.isRowingBoat()) {
                switch (mc.objectMouseOver.typeOfHit) {
                    case ENTITY:
                        mc.playerController.attackEntity(mc.player, mc.objectMouseOver.entityHit);
                        break;
                    case BLOCK:
                        BlockPos blockpos = mc.objectMouseOver.getBlockPos();
                        if (!mc.world.isAirBlock(blockpos)) {
                            mc.playerController.clickBlock(blockpos, mc.objectMouseOver.sideHit);
                            break;
                        }
                    case MISS:
                        if (mc.playerController.isNotCreative()) {
                            mc.leftClickCounter = 10;
                        }

                        mc.player.resetCooldown();
                        ForgeHooks.onEmptyLeftClick(mc.player);
                }

                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
        }
    }

    private void rightClickMouse() {
        if (!mc.playerController.getIsHittingBlock()) {
            mc.rightClickDelayTimer = 4;
            if (!mc.player.isRowingBoat()) {
                if (mc.objectMouseOver == null) {
                    Kisman.LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
                }

                EnumHand[] var1 = EnumHand.values();
                int var2 = var1.length;

                for(int var3 = 0; var3 < var2; ++var3) {
                    EnumHand enumhand = var1[var3];
                    ItemStack itemstack = mc.player.getHeldItem(enumhand);
                    if (mc.objectMouseOver != null) {
                        switch (mc.objectMouseOver.typeOfHit) {
                            case ENTITY:
                                if (mc.playerController.interactWithEntity(mc.player, mc.objectMouseOver.entityHit, mc.objectMouseOver, enumhand) == EnumActionResult.SUCCESS) {
                                    return;
                                }

                                if (mc.playerController.interactWithEntity(mc.player, mc.objectMouseOver.entityHit, enumhand) == EnumActionResult.SUCCESS) {
                                    return;
                                }
                                break;
                            case BLOCK:
                                BlockPos blockpos = mc.objectMouseOver.getBlockPos();
                                if (mc.world.getBlockState(blockpos).getMaterial() != Material.AIR) {
                                    int i = itemstack.getCount();
                                    EnumActionResult enumactionresult = mc.playerController.processRightClickBlock(mc.player, mc.world, blockpos, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec, enumhand);
                                    if (enumactionresult == EnumActionResult.SUCCESS) {
                                        mc.player.swingArm(enumhand);
                                        if (!itemstack.isEmpty() && (itemstack.getCount() != i || mc.playerController.isInCreativeMode())) {
                                            mc.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                                        }

                                        return;
                                    }
                                }
                        }
                    }

                    if (itemstack.isEmpty() && (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit == RayTraceResult.Type.MISS)) {
                        ForgeHooks.onEmptyClick(mc.player, enumhand);
                    }

                    if (!itemstack.isEmpty() && mc.playerController.processRightClick(mc.player, mc.world, enumhand) == EnumActionResult.SUCCESS) {
                        mc.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                        return;
                    }
                }
            }
        }
    }

    private void middleClickMouse() {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit != RayTraceResult.Type.MISS) {
            ForgeHooks.onPickBlock(mc.objectMouseOver, mc.player, mc.world);
        }
    }
}
