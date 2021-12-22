package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class CustomFov extends Module {
    public CustomFov() {
        super("CustomFov", "customisated your fov", Category.RENDER);

        Kisman.instance.settingsManager.rSetting(new Setting("Fov", this, 30, 30, 150, true));
    }

    public void update() {
        mc.gameSettings.fovSetting = Kisman.instance.settingsManager.getSettingByName(this, "Fov").getValInt();
    }

    public void onDisable() {
        mc.gameSettings.fovSetting = 110;
    }
}
