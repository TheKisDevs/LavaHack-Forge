package com.kisman.cc.features.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.movement.MoveModifier;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.enums.SprintModes;
import net.minecraft.network.play.client.CPacketPlayer;

public class TeleportBack extends Module {
    public TeleportBack() {
        super("TeleportBack", "TeleportBack", Category.PLAYER);
    }

    public void onEnable() {
        if(mc.player != null && mc.world != null) {
            savecord();
            ChatUtility.complete().printClientModuleMessage("Position saved!");
        }
    }

    public void onDisable() {
        if(mc.player != null && mc.world != null) {
            loadcord();
            ChatUtility.complete().printClientModuleMessage("Teleported!");
        }
    }

    public void update() {
        if(mc.player != null && mc.world != null) {
            MoveModifier moveModifier = (MoveModifier) Kisman.instance.moduleManager.getModule("MoveModifier");
            if(moveModifier.getSprint().getValEnum() != SprintModes.None) moveModifier.getSprint().setValEnum(SprintModes.None);
            if(mc.player.isSprinting()) mc.player.setSprinting(false);
            if(mc.gameSettings.keyBindSneak.isKeyDown()) loadcord();
            mc.player.onGround = false;
        }
    }

    private double x;
    private double y;
    private double z;

    public void savecord() {
        x = mc.player.posX;
        y = mc.player.posY;
        z = mc.player.posZ;
    }

    public void loadcord() {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, false));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));

        mc.player.setPositionAndUpdate(x, y, z);
        mc.player.setPosition(x, y, z);
    }
}
