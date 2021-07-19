package com.kisman.cc.module.modules.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.Minecraft;

public class FullBright extends Module {
    public FullBright() {
        super("FullBright", 0, Category.RENDER);
    }

    public void update() {
        Minecraft.getMinecraft().gameSettings.gammaSetting = 100;
    }

    public void onDisable() {
        Minecraft.getMinecraft().gameSettings.gammaSetting = 1;
    }
}
