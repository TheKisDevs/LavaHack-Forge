package com.kisman.cc.module.client;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

public class CustomMainMenuModule extends Module {
    public Setting watermark = new Setting("WaterMark", this, true);
    public Setting viaforge = new Setting("ViaForge Button", this, true);
    public Setting customSplashText = new Setting("Custom Splash Text", this, true);
    public Setting customSplashFont = new Setting("Custom Splash Font", this, true).setVisible(() -> customSplashText.getValBoolean());
    public Setting particles = new Setting("Particles", this, true);

    public static CustomMainMenuModule instance;

    public CustomMainMenuModule() {
        super("CustomMainMenu", Category.CLIENT);

        instance = this;

        setmgr.rSetting(watermark);
        setmgr.rSetting(viaforge);
        setmgr.rSetting(customSplashText);
        setmgr.rSetting(customSplashFont);
        setmgr.rSetting(particles);
    }
}
