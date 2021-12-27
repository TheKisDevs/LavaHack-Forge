package com.kisman.cc.module.client;

import com.kisman.cc.module.*;
import com.kisman.cc.oldclickgui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;

public class Config extends Module {
    public static Config instance;

    public Setting friends = new Setting("Friends", this, true);
    public Setting nameMode = new Setting("Name Mode", this, NameMode.kismancc);
    public Setting customName = new Setting("CustomName", this, "kisman.cc", "kisman.cc", true);
    public Setting scrollSpeed = new Setting("ScrollSpeed", this, 15, 0, 100, Slider.NumberType.PERCENT);
    public Setting guiGlow = new Setting("GuiGlow", this, false);
    public Setting glowOffset = new Setting("GlowOffset", this, 6, 0, 20, true);
    public Setting guiAstolfo = new Setting("GuiAstolfo", this, false);
    public Setting guiRenderSIze = new Setting("GuiRenderSize", this, false);
    public Setting pulseMin = new Setting("PulseMin", this, 255, 0, 255, true);
    public Setting pulseMax = new Setting("PulseMax", this, 110, 0, 255, true);
    public Setting pulseSpeed = new Setting("PulseSpeed", this, 1.5, 0.1, 10, false);
    public Setting useConsolasFontInCSGui = new Setting("Use Consolas in CSGui", this, false);


    public Config() {
        super("Config", "cfg for this client", Category.CLIENT);

        instance = this;

        setmgr.rSetting(friends);
        setmgr.rSetting(nameMode);
        setmgr.rSetting(customName);
        setmgr.rSetting(scrollSpeed);
        setmgr.rSetting(guiGlow);
        setmgr.rSetting(glowOffset);
        setmgr.rSetting(guiAstolfo);
        setmgr.rSetting(guiRenderSIze);
        setmgr.rSetting(pulseMin);
        setmgr.rSetting(pulseMax);
        setmgr.rSetting(pulseSpeed);
        setmgr.rSetting(useConsolasFontInCSGui);
    }

    public enum NameMode {
        kismancc,
        lavahack,
        custom
    }
}
