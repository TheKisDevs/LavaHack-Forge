package com.kisman.cc.features.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.movement.MoveModifier;
import com.kisman.cc.util.CheckUtil;
import com.kisman.cc.util.chat.other.ChatUtils;
import com.kisman.cc.util.enums.SprintModes;

public class TeleportBack extends Module {
    private double x;
    private double y;
    private double z;

    public TeleportBack() {
        super("TeleportBack", "TeleportBack", Category.PLAYER);
    }

    public void onEnable() {
        if(mc.player != null && mc.world != null) {
            CheckUtil.savecord();
            ChatUtils.complete("Position saved!");
        }
    }

    public void onDisable() {
        if(mc.player != null && mc.world != null) {
            CheckUtil.loadcord();
            ChatUtils.complete("Teleported!");
        }
    }

    public void update() {
        if(mc.player != null && mc.world != null) {
            x = mc.player.posX;
            y = mc.player.posY;
            z = mc.player.posZ;

            MoveModifier moveModifier = (MoveModifier) Kisman.instance.moduleManager.getModule("MoveModifier");
            if(moveModifier.getSprint().getValEnum() != SprintModes.None) {
                moveModifier.getSprint().setValEnum(SprintModes.None);
            }

            if(mc.player.isSprinting()) {
                mc.player.setSprinting(false);
            }

            if(mc.gameSettings.keyBindSneak.isKeyDown()) {
                CheckUtil.loadcord();
            }

            mc.player.onGround = false;
        }
    }
}
