package com.kisman.cc.module.movement;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

import net.minecraft.client.settings.KeyBinding;

public class AutoWalk extends Module{
    public AutoWalk() {
        super("AutoWalk", "gay's module!!!!", Category.MOVEMENT);
    }

    public void update() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
    }

    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
    }
}
