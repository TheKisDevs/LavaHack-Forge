package com.kisman.cc.features.module.movement;

import com.kisman.cc.features.module.*;

public class AutoWalk extends Module{
    public AutoWalk() {
        super("AutoWalk", "auto walking", Category.MOVEMENT);
    }

    public void onDisable() {
        if(mc.player == null && mc.world == null) return;

        mc.gameSettings.keyBindForward.pressed = false;
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        mc.gameSettings.keyBindForward.pressed = true;
    }
}
