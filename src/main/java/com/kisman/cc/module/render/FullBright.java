package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;

public class FullBright extends Module {
    public FullBright() {
        super("FullBright", "Gamma setting", Category.RENDER);

        Kisman.instance.settingsManager.rSetting(new Setting("Gamma", this, 1, 1, 100, true));

        super.setDisplayInfo(() -> "[" + (int) Kisman.instance.settingsManager.getSettingByName(this, "Gamma").getValDouble() + "]");
    }

    public void update() {
        Minecraft.getMinecraft().gameSettings.gammaSetting = (int) Kisman.instance.settingsManager.getSettingByName(this, "Gamma").getValDouble();
    }

    public void onDisable() {
        Minecraft.getMinecraft().gameSettings.gammaSetting = 1;
    }
}
