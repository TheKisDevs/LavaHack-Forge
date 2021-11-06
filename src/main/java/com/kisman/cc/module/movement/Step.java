package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;

public class Step extends Module {
    public static Step instance;

    public Step() {
        super("Step", "setting your step", Category.MOVEMENT);

        instance = this;

        Kisman.instance.settingsManager.rSetting(new Setting("Heigth", this, 0.5f, 0.5f, 2.5f, false));
    }

    public void update() {
        float height = (float) Kisman.instance.settingsManager.getSettingByName(this, "Heigth").getValDouble();
        if(mc.player != null && mc.world != null) Minecraft.getMinecraft().player.stepHeight = height;
    }

    public void onDisable() {
        if(mc.player != null && mc.world != null) Minecraft.getMinecraft().player.stepHeight = 0.5f;
    }
}
