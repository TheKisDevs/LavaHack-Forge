package com.kisman.cc.features.module.movement;

import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;

import java.util.Arrays;

public class AirJump extends Module {
    private final Setting mode = register(new Setting("Mode", this, "Vanilla", Arrays.asList("Vanilla", "NCP", "Matrix")));

    public AirJump() {
        super("AirJump", "Allows to jump in air", Category.MOVEMENT);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;
        if (mode.getValString().equalsIgnoreCase("Vanilla") && mc.gameSettings.keyBindJump.isPressed()) mc.player.motionY = 0.7;
        else if (mode.getValString().equalsIgnoreCase("NCP")) {
            mc.player.onGround = true;
            mc.player.isAirBorne = false;
        } else if(mode.getValString().equalsIgnoreCase("Matrix") && mc.gameSettings.keyBindJump.pressed) {
            mc.player.jump();
            mc.player.motionY -= 0.25f;
            if(mc.gameSettings.keyBindForward.pressed) {
                mc.timer.elapsedTicks = (int) 1.05f;
                mc.player.motionX *= 1.1f;
                mc.player.motionZ *= 1.1f;
                mc.player.onGround = false;
            }
        }
    }
}