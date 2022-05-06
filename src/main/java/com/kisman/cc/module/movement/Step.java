package com.kisman.cc.module.movement;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

import java.util.Locale;

public class Step extends Module {
    public static Step instance;

    public Setting height = new Setting("Height", this, 2, 1, 4, true);

    public Step() {
        super("Step", "setting your step", Category.MOVEMENT);

        instance = this;

        setmgr.rSetting(height);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        super.setDisplayInfo("[" + height.getValInt() + "]");
        mc.player.stepHeight = height.getValFloat();
    }

    public void onDisable() {
        if(mc.player != null && mc.world != null) mc.player.stepHeight = 0.5f;
    }
}
