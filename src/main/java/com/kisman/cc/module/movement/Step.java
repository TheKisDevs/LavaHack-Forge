package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

public class Step extends Module {
    public static Step instance;

    public Step() {
        super("Step", "setting your step", Category.MOVEMENT);

        instance = this;

        Kisman.instance.settingsManager.rSetting(new Setting("Heigth", this, 0.5f, 0.5f, 4, false));
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        float height = (float) Kisman.instance.settingsManager.getSettingByName(this, "Heigth").getValDouble();
        mc.player.stepHeight = height;
    }

    public void onDisable() {
        if(mc.player != null && mc.world != null) mc.player.stepHeight = 0.5f;
    }
}
