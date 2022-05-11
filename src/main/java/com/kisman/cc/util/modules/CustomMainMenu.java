package com.kisman.cc.util.modules;
import java.util.Random;

public class CustomMainMenu {
    public static boolean WATERMARK, CUSTOM_SPLASH_TEXT, CUSTOM_SPLASH_FONT, PARTICLES;
    public static final String[] splashes = new String[] {
            "TheKisDevs on tope",
            "meowubic",
            "kisman.cc",
            "Fuck you, Muffin.",
            "kisman.cc+",
            "kidman.club",
            "kisman.cc b0.1.6.1",
            "One of the best client lmao",
            "TheKisDevs inc",
            "lava_hack",
            "Get Good. Get BloomWare.",
            "water??",
            "kidman own everyone",
            "u got token logger))",
            "sus user",
            "kisman > you",
            "kidmad.sex",
            "ddev moment",
            "made by _kisman_#5039",
            "Where XuluPlus shaders??",
            "Future? No.",
            "meow"
    };

    public static void update() {
    }

    public static String getRandomCustomSplash() {
        int i = (int) (splashes.length * new Random().nextFloat());
        return splashes[i == splashes.length ? i - 1 : i];
    }
}
