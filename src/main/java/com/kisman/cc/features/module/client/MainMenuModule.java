package com.kisman.cc.features.module.client;

import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.features.module.client.custommainmenu.CustomMainMenu;

public class MainMenuModule extends Module {
    public final Setting watermark = register(new Setting("WaterMark", this, true));
    public final Setting customSplashText = register(new Setting("Custom Splash Text", this, true));
    public final Setting customSplashFont = register(new Setting("Custom Splash Font", this, false).setVisible(() -> customSplashText.getValBoolean()));
    public final Setting particles = register(new Setting("Particles", this, false));

    @ModuleInstance
    public static MainMenuModule instance;

    public MainMenuModule() {
        super("MainMenu", Category.CLIENT);
        super.setToggled(true);
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
