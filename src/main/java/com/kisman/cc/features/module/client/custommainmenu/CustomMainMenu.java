package com.kisman.cc.features.module.client.custommainmenu;
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
            "best haked client",
            "TheKisDevs inc",
            "lava_hack",
            "Get Good. Get BloomWare.",
            "water??",
            "kidman own everyone",
            "ez rat",
            "sus user",
            "kisman > you",
            "kidmad.sex",
            "ddev moment",
            "made by _kisman_#5039",
            "Where are XuluPlus shaders??",
            "Future? No.",
            "meow",
            "Lavahake",
            "Dallas got skidded",
            "lavhak",
            "cubic > you",
            ":^)",
            "ratted by cattyn",
            "ratted by TheKisDevs",
            "ty for downloading and using this rat",
            "https://github.com/kisman2000/lava_hack",
            "https://github.com/TheKisDevs/LavaHack",
            "https://github.com/TheKisDevs/LavaHack-Public",
            "kisman left LavaHack Development",
            "earthhack skid fr",
            "fr",
            "auto crystsa pp v??/?",
            "owned by catdog.cc",
            "bruh",
            "made by Cubic#1411",
            "zenov is still crying",
            "future gold $$$",
            "erta haxe 1 8.7",
            "The Return",
            "Fade Out: 0ms",
            "Ai2473",
            "v/line",
            "doc:hidden",
            "Flora.net v2.0.0-DEV",
            "Hole ESP [zocker]",
            "Latty Craft Community",
            "mKisGen",
            "LameLands",
            "Your place to talk",
            "kapibara",
            "LavaHruk",
            "Discord server got termed",
            "Kisman got termed",
            "LavaHack"
    };

    public static void update() {
    }

    public static String getRandomCustomSplash() {
        int i = (int) (splashes.length * new Random().nextFloat());
        return splashes[i == splashes.length ? i - 1 : i];
    }
}
