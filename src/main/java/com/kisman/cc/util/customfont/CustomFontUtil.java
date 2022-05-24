package com.kisman.cc.util.customfont;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.client.CustomFontModule;
import com.kisman.cc.util.customfont.norules.CFontRenderer;
import com.kisman.cc.util.enums.FontStyles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

public class CustomFontUtil {
    private static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    public static CustomFontRenderer comfortaal20 = new CustomFontRenderer(getFontTTF("comfortaa-light", 22), true, true);
    public static CustomFontRenderer comfortaal18 = new CustomFontRenderer(getFontTTF("comfortaa-light", 18), true, true);
    public static CustomFontRenderer comfortaal15 = new CustomFontRenderer(getFontTTF("comfortaa-light", 15), true, true);
    public static CustomFontRenderer comfortaal16 = new CustomFontRenderer(getFontTTF("comfortaa-light", 16), true, true);

    public static CustomFontRenderer comfortaab72 = new CustomFontRenderer(getFontTTF("comfortaa-bold", 72), true, true);
    public static CustomFontRenderer comfortaab55 = new CustomFontRenderer(getFontTTF("comfortaa-bold", 55), true, true);
    public static CustomFontRenderer comfortaab20 = new CustomFontRenderer(getFontTTF("comfortaa-bold", 22), true, true);
    public static CustomFontRenderer comfortaab18 = new CustomFontRenderer(getFontTTF("comfortaa-bold", 18), true, true);
    public static CustomFontRenderer comfortaab16 = new CustomFontRenderer(getFontTTF("comfortaa-bold", 16), true, true);

    public static CustomFontRenderer comfortaa20 = new CustomFontRenderer(getFontTTF("comfortaa-regular", 22), true, true);
    public static CustomFontRenderer comfortaa18 = new CustomFontRenderer(getFontTTF("comfortaa-regular", 18), true, true);
    public static CustomFontRenderer comfortaa15 = new CustomFontRenderer(getFontTTF("comfortaa-regular", 15), true, true);

    public static CustomFontRenderer consolas18 = new CustomFontRenderer(getFontTTF("consolas", 18), true, true);
    public static CustomFontRenderer consolas16 = new CustomFontRenderer(getFontTTF("consolas", 16), true, true);
    public static CustomFontRenderer consolas15 = new CustomFontRenderer(getFontTTF("consolas", 15), true, true);

    public static CustomFontRenderer sfui19 = new CustomFontRenderer(getFontTTF("sf-ui", 19), true, true);
    public static CustomFontRenderer sfui18 = new CustomFontRenderer(getFontTTF("sf-ui", 18), true, true);

    public static CustomFontRenderer futura20 = new CustomFontRenderer(getFontTTF("futura-normal", 20), true, true);
    public static CustomFontRenderer futura18 = new CustomFontRenderer(getFontTTF("futura-normal", 18), true, true);

    public static CustomFontRenderer lexendDeca18 = new CustomFontRenderer(getFontTTF("lexenddeca-regular", 18), true, true);

    public static CustomFontRenderer century18 = new CustomFontRenderer(getFontTTF("main", 18), true, true);

    public static CustomFontRenderer verdana18 = Kisman.instance.customFontRenderer;
    public static CustomFontRenderer verdana15 = Kisman.instance.customFontRenderer1;

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
        return CustomFontUtilKt.Companion.getStringWidth(getCustomFontName(), text);
    }

    public static int getStringWidth(String text, boolean gui) {
        return CustomFontUtilKt.Companion.getStringWidth(getCustomFontName(), text, gui);
    }

    public static void drawString(String text, double x, double y, int color, boolean gui) {
        if (customFont()) {
            y += 2;
            Object font = CustomFontUtilKt.Companion.getCustomFont(getCustomFontName(), gui);
            if(font instanceof CFontRenderer) ((CFontRenderer) font).drawString(text, x, y, color, false);
            else if(font instanceof CustomFontRenderer) ((CustomFontRenderer) font).drawString(text, x, y, color, false);
        } else fontRenderer.drawString(text, (int)x, (int)y, color);
    }

    public static int drawString(String text, double x, double y, int color) {
        if (customFont()) {
            y += 2;
            Object font = CustomFontUtilKt.Companion.getCustomFont(getCustomFontName());
            if(font instanceof CFontRenderer) return (int) ((CFontRenderer) font).drawString(text, x, y, color, false);
            else if(font instanceof CustomFontRenderer) return (int) ((CustomFontRenderer) font).drawString(text, x, y, color, false);
        }
        return fontRenderer.drawString(text, (int)x, (int)y, color);
    }

    public static int drawStringWithShadow(String text, double x, double y, int color) {
        if (customFont()) {
            y += 2;
            Object font = CustomFontUtilKt.Companion.getCustomFont(getCustomFontName());
            if(font instanceof CFontRenderer) return (int) ((CFontRenderer) font).drawStringWithShadow(text, x, y, color);
            else if(font instanceof CustomFontRenderer) return (int) ((CustomFontRenderer) font).drawStringWithShadow(text, x, y, color);
        }
        return fontRenderer.drawStringWithShadow(text, (float)x, (float)y, color);
    }

    public static void drawCenteredStringWithShadow(String text, double x, double y, int color) {
        if (customFont()) {
            y += 2;
            Object font = CustomFontUtilKt.Companion.getCustomFont(getCustomFontName());
            if(font instanceof CFontRenderer) ((CFontRenderer) font).drawCenteredStringWithShadow(text, x, y, color);
            else if(font instanceof CustomFontRenderer) ((CustomFontRenderer) font).drawCenteredStringWithShadow(text, (float) x, (float) y, color);
        } else fontRenderer.drawStringWithShadow(text, (float) x - fontRenderer.getStringWidth(text) / 2.0F, (float) y, color);
    }

    public static int getFontHeight(boolean gui) {
        return CustomFontUtilKt.Companion.getHeight(getCustomFontName(), gui);
    }

    public static int getFontHeight(CustomFontRenderer customFont) {
        return (customFont.fontHeight - 8) / 2;
    }

    public static int getFontHeight() {
        return CustomFontUtilKt.Companion.getHeight(getCustomFontName());
    }

    private static boolean customFont() {
        return CustomFontModule.turnOn;
    }

    public static String getCustomFontName() {
        return CustomFontModule.instance == null ? null : CustomFontModule.instance.mode.getValString();
    }
}