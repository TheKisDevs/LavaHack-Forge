package com.kisman.cc.util.modules;

import com.kisman.cc.module.client.CustomMainMenuModule;

import java.util.Random;

public class CustomMainMenu {
    public static boolean WATERMARK, CUSTOM_SPLASH_TEXT, VIAFORGE, CUSTOM_SPLASH_FONT, PARTICLES;
    public static final String[] splashes = new String[] {"kisman.cc", "kisman.cc+", "kidman.club", "kisman.cc b0.1.6.1", "All of the best client lmao", "TheKisDevs inc", "lava_hack", "water??", "kidman own everyone", "u got token logger))", "sus user", "kisman > you", "ddev moment", "made by _kisman_#5039"};

    public static void update() {
        WATERMARK = CustomMainMenuModule.instance.watermark.getValBoolean() && CustomMainMenuModule.instance.isToggled();
        CUSTOM_SPLASH_TEXT = CustomMainMenuModule.instance.customSplashText.getValBoolean() && CustomMainMenuModule.instance.isToggled();
        VIAFORGE = CustomMainMenuModule.instance.viaforge.getValBoolean() && CustomMainMenuModule.instance.isToggled();
        CUSTOM_SPLASH_FONT = CustomMainMenuModule.instance.customSplashFont.getValBoolean() && CustomMainMenuModule.instance.isToggled();
        PARTICLES = CustomMainMenuModule.instance.particles.getValBoolean() && CustomMainMenuModule.instance.isToggled();
    }

    public static String getRandomCustomSplash() {
        Random rand = new Random();
        int i = (int) (splashes.length * rand.nextFloat());
        return splashes[i == splashes.length ? i - 1 : i];
    }
}
