package com.kisman.cc.features.module.player;

import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.TimerUtils;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;
import java.util.Arrays;

public class SilentXp extends Module {
    private final Setting customPitch = register(new Setting("Custom Pitch", this, true));
    private final Setting lookPitch = register(new Setting("LookPitch", this, 90, 0, -90, false).setVisible(customPitch::getValBoolean));
    private final Setting delay = register(new Setting("Delay", this, 0, 0, 100, NumberType.TIME));
    private final Setting switchMode = register(new Setting("SwitchMode", this, "Packet", new ArrayList<>(Arrays.asList("Packet", "Client"))));

    private final TimerUtils timer = new TimerUtils();

    public SilentXp() {
        super("SilentXP", "SilentXp", Category.PLAYER);
    }

    public void onEnable() {
        timer.reset();
    }

    public void update() {
        if(!timer.passedMillis(delay.getValInt())) return;
        timer.reset();
        if(mc.currentScreen == null && mc.player != null && mc.world != null) {
            int oldSlot = mc.player.inventory.currentItem;
            
            int xpSlot = InventoryUtil.findItem(Items.EXPERIENCE_BOTTLE, 0, 9);
            
            if(xpSlot == -1) {
                ChatUtility.error().printClientModuleMessage("No XP in hotbar! Disabling!");
                disable();
                return;
            }

            switch (switchMode.getValString()) {
                case "Packet":
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.findItem(Items.EXPERIENCE_BOTTLE, 0, 9)));
                    break;
                case "Client":
                    mc.player.inventory.currentItem = InventoryUtil.findItem(Items.EXPERIENCE_BOTTLE, 0, 9);
                    break;
            }

            float oldPitch = mc.player.rotationPitch;
            if(customPitch.getValBoolean()) mc.player.rotationPitch = (float) lookPitch.getValDouble();
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, (float) lookPitch.getValDouble(), true));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            if(customPitch.getValBoolean()) mc.player.rotationPitch = oldPitch;

            switch (switchMode.getValString()) {
                case "Packet":
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                    break;
                case "Client":
                    mc.player.inventory.currentItem = oldSlot;
                    break;
            }
        }
    }
}
