package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import net.minecraft.util.ResourceLocation;

public class HalqGuiModule extends Module {
    public final Setting primaryColor = new Setting("Primary Color", this, "Primary Color", new Colour(255, 0, 0));
    public final Setting background = new Setting("Background", this, true);
    public final Setting shadow = new Setting("Shadow", this, false);
    public final Setting test = new Setting("test", this, false);
    public static HalqGuiModule instance;

    public HalqGuiModule() {
        super("HalqGui", Category.CLIENT);

        instance = this;

        setmgr.rSetting(primaryColor);
        setmgr.rSetting(background);
        setmgr.rSetting(shadow);
        setmgr.rSetting(test);
    }

    public void onEnable() {
        mc.displayGuiScreen(Kisman.instance.halqGui);
        super.setToggled(false);
        if(Config.instance.guiBlur.getValBoolean()) mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
    }
}
