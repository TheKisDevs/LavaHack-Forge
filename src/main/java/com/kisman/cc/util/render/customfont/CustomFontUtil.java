package com.kisman.cc.util.render.customfont;

import com.kisman.cc.features.module.client.CustomFontModule;
import com.kisman.cc.util.enums.FontStyles;
import com.kisman.cc.util.enums.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

public class CustomFontUtil {
    private static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    public static AbstractFontRenderer comfortaal30 = new ExtendedFontRenderer(getFontTTF("comfortaa-light", 30));
    public static AbstractFontRenderer comfortaal18 = new ExtendedFontRenderer(getFontTTF("comfortaa-light", 18));
    public static AbstractFontRenderer comfortaal15 = new ExtendedFontRenderer(getFontTTF("comfortaa-light", 15));
    public static AbstractFontRenderer comfortaal16 = new ExtendedFontRenderer(getFontTTF("comfortaa-light", 16));
    public static AbstractFontRenderer comfortaal10 = new ExtendedFontRenderer(getFontTTF("comfortaa-light", 10));

    public static AbstractFontRenderer comfortaab72 = new ExtendedFontRenderer(getFontTTF("comfortaa-bold", 72));
    public static AbstractFontRenderer comfortaab55 = new ExtendedFontRenderer(getFontTTF("comfortaa-bold", 55));
    public static AbstractFontRenderer comfortaab30 = new ExtendedFontRenderer(getFontTTF("comfortaa-bold", 30));
    public static AbstractFontRenderer comfortaab18 = new ExtendedFontRenderer(getFontTTF("comfortaa-bold", 18));
    public static AbstractFontRenderer comfortaab16 = new ExtendedFontRenderer(getFontTTF("comfortaa-bold", 16));
    public static AbstractFontRenderer comfortaab10 = new ExtendedFontRenderer(getFontTTF("comfortaa-bold", 10));

    public static AbstractFontRenderer comfortaa30 = new ExtendedFontRenderer(getFontTTF("comfortaa-regular", 30));
    public static AbstractFontRenderer comfortaa18 = new ExtendedFontRenderer(getFontTTF("comfortaa-regular", 18));
    public static AbstractFontRenderer comfortaa15 = new ExtendedFontRenderer(getFontTTF("comfortaa-regular", 15));
    public static AbstractFontRenderer comfortaa10 = new ExtendedFontRenderer(getFontTTF("comfortaa-regular", 10));

    public static AbstractFontRenderer consolas18 = new ExtendedFontRenderer(getFontTTF("consolas", 18));
    public static AbstractFontRenderer consolas16 = new ExtendedFontRenderer(getFontTTF("consolas", 16));
    public static AbstractFontRenderer consolas15 = new ExtendedFontRenderer(getFontTTF("consolas", 15));

    public static AbstractFontRenderer sfui19 = new ExtendedFontRenderer(getFontTTF("sf-ui", 19));
    public static AbstractFontRenderer sfui18 = new ExtendedFontRenderer(getFontTTF("sf-ui", 18));

    public static AbstractFontRenderer futura20 = new ExtendedFontRenderer(getFontTTF("futura-normal", 20));
    public static AbstractFontRenderer futura18 = new ExtendedFontRenderer(getFontTTF("futura-normal", 18));

    public static AbstractFontRenderer lexendDeca18 = new ExtendedFontRenderer(getFontTTF("lexenddeca-regular", 18));

    public static AbstractFontRenderer century18 = new ExtendedFontRenderer(getFontTTF("main", 18));

    public static AbstractFontRenderer verdana18 = new ExtendedFontRenderer(new Font("Verdana", Font.PLAIN, 18));

    public static AbstractFontRenderer jelleeb18 = new ExtendedFontRenderer(getFontTTF("jellee-bold", 18));

    public static AbstractFontRenderer minecraftRus13 = new ExtendedFontRenderer(getFontTTF("minecraft", 13));

    public static Font getFontTTF(String name, int size) {
        return getFontTTF(name, FontStyles.Plain, size);
    }

    public static Font getFontTTF(String name, FontStyles style, int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/" + name + ".ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(style.getStyle(), size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", style.getStyle(), size);
        }
        return font;
    }

    public static int getStringWidth(String text) {
        return CustomFontUtilKt.Companion.getStringWidth(text);
    }

    public static int getStringWidth(String text, boolean gui) {
        return CustomFontUtilKt.Companion.getStringWidth(text, gui);
    }

    public static void drawString(String text, double x, double y, int color, boolean gui) {
        if (customFont()) CustomFontUtilKt.Companion.getCustomFont(gui).drawString(text, x, y, color);
        else fontRenderer.drawString(text, (int)x, (int)y, color);
    }

    public static int drawString(String text, double x, double y, int color) {
        if (customFont()) {
            CustomFontUtilKt.Companion.getCustomFont().drawString(text, x, y, color);
            return 0;
        }
        return fontRenderer.drawString(text, (int)x, (int)y, color);
    }

    public static int drawStringWithShadow(String text, double x, double y, int color) {
        if (customFont()) {
            CustomFontUtilKt.Companion.getCustomFont().drawStringWithShadow(text, (int) x, (int) y, color);
            return 0;
        }
        return fontRenderer.drawStringWithShadow(text, (float)x, (float)y, color);
    }

    public static void drawCenteredStringWithShadow(String text, double x, double y, int color) {
        if (customFont()) CustomFontUtilKt.Companion.getCustomFont().drawCenteredStringWithShadow(text, (int) x, (int) y, color);
        else fontRenderer.drawStringWithShadow(text, (float) x - fontRenderer.getStringWidth(text) / 2.0F, (float) y, color);
    }

    public static int getFontHeight(boolean gui) {
        return CustomFontUtilKt.Companion.getHeight(gui);
    }

    public static int getFontHeight() {
        return getFontHeight(false);
    }

    private static boolean customFont() {
        return CustomFontModule.instance.isToggled();
    }

    public static Fonts getFont() {
        return CustomFontModule.instance.font.getValEnum();
    }
}