package com.kisman.cc.features.module.client;

import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.features.module.client.custommainmenu.CustomMainMenu;

public class CustomMainMenuModule extends Module {
    public Setting watermark = register(new Setting("WaterMark", this, true));
    public Setting customSplashText = register(new Setting("Custom Splash Text", this, true));
    public Setting customSplashFont = register(new Setting("Custom Splash Font", this, false).setVisible(() -> customSplashText.getValBoolean()));
    public Setting particles = register(new Setting("Particles", this, false));

    public static CustomMainMenuModule instance;

    public CustomMainMenuModule() {
        super("CustomMainMenu", Category.CLIENT);
        super.setToggled(true);
        instance = this;
    }

    public void update() {
        CustomMainMenu.WATERMARK = watermark.getValBoolean();
        CustomMainMenu.CUSTOM_SPLASH_TEXT = customSplashText.getValBoolean();
        CustomMainMenu.CUSTOM_SPLASH_FONT = customSplashFont.getValBoolean();
        CustomMainMenu.PARTICLES = particles.getValBoolean();
    }

    public void onDisable() {
        CustomMainMenu.WATERMARK = false;
        CustomMainMenu.CUSTOM_SPLASH_TEXT = false;
        CustomMainMenu.CUSTOM_SPLASH_FONT = false;
        CustomMainMenu.PARTICLES = false;
    }
}
