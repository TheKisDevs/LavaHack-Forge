package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;

public class FullBright extends Module {
    public FullBright() {
        super("FullBright", "Gamma setting", Category.RENDER);
    }

    public void update() {
        Minecraft.getMinecraft().gameSettings.gammaSetting = 100;
    }

    public void onDisable() {
        Minecraft.getMinecraft().gameSettings.gammaSetting = 1;
    }
}
