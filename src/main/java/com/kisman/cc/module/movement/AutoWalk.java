package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

import com.kisman.cc.settings.Setting;
import net.minecraft.client.settings.KeyBinding;

public class AutoWalk extends Module{
    public AutoWalk() {
        super("AutoWalk", "gay's module!!!!", Category.MOVEMENT);

        Kisman.instance.settingsManager.rSetting(new Setting("voidsetting", this, "void", "setting"));
    }

    public void update() {
        if(mc.player != null && mc.world != null) KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
    }

    public void onDisable() {
        if(mc.player != null && mc.world != null) KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
    }
}
