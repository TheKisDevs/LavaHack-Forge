package com.kisman.cc.features.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;

import net.minecraft.network.play.client.CPacketEntityAction;

import java.util.Arrays;

public class Fly extends Module {
    private final Setting mode = register(new Setting("Mode", this, "Vanilla", Arrays.asList("Vanilla", "WellMore", "ReallyWorld")));

    public Fly() {
        super("Fly", "Your flying", Category.MOVEMENT);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        if(mode.getValString().equalsIgnoreCase("Vanilla")) {
            mc.player.capabilities.isFlying = true;
            mc.player.capabilities.setFlySpeed(0.05f);
        } else if(mode.getValString().equalsIgnoreCase("WellMore")) {
            if(mc.player.onGround) mc.player.motionY = 1.0;
            else {
                mc.player.capabilities.isFlying = true;
                mc.player.capabilities.setFlySpeed(1.3f);
                mc.player.motionX = 0.0;
                mc.player.motionY = -0.02;
                mc.player.motionZ = 0.0;
            }
        } else if(mode.getValString().equalsIgnoreCase("ReallyWorld")) {
            if (mc.gameSettings.keyBindJump.pressed) {
                if (mc.player.ticksExisted % 3 == 0) mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                mc.player.jump();
            }
        }
    }

    public void onDisable() {
        if(mc.player == null || mc.world == null) return;

        mc.player.capabilities.isFlying = false;
        mc.player.capabilities.setFlySpeed(0.1f);
    }
}
