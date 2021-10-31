package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;

public class CustomFov extends Module {
    public CustomFov() {
        super("CustomFOV", "customisated your fov", Category.RENDER);

        Kisman.instance.settingsManager.rSetting(new Setting("FOV", this, 30, 30, 150, true));
    }

    public void update() {
        int fov = (int) Kisman.instance.settingsManager.getSettingByName(this, "FOV").getValDouble();
        mc.gameSettings.fovSetting = fov;
    }

    public void onDisable() {
        mc.gameSettings.fovSetting = 110;
    }
}
