package com.kisman.cc.util.customfont;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.client.CustomFont;
import com.kisman.cc.util.customfont.norules.CFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.io.InputStream;

public class CustomFontUtil {
    private static final FontRenderer fontRenderer = (Minecraft.getMinecraft()).fontRenderer;

    public static CFontRenderer comfortaal20 = new CFontRenderer(getFontTTF("comfortaa-light", 22), true, true);
    public static CFontRenderer comfortaal18 = new CFontRenderer(getFontTTF("comfortaa-light", 18), true, true);
    public static CFontRenderer comfortaal16 = new CFontRenderer(getFontTTF("comfortaa-light", 16), true, true);

    public static CFontRenderer comfortaab20 = new CFontRenderer(getFontTTF("comfortaa-bold", 22), true, true);
    public static CFontRenderer comfortaab18 = new CFontRenderer(getFontTTF("comfortaa-bold", 18), true, true);
    public static CFontRenderer comfortaab16 = new CFontRenderer(getFontTTF("comfortaa-bold", 16), true, true);

    public static CFontRenderer comfortaa20 = new CFontRenderer(getFontTTF("comfortaa-regular", 22), true, true);
    public static CFontRenderer comfortaa18 = new CFontRenderer(getFontTTF("comfortaa-regular", 18), true, true);
    public static CFontRenderer comfortaa16 = new CFontRenderer(getFontTTF("comfortaa-regular", 16), true, true);

    private static Font getFontTTF(String name, int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/" + name + ".ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    public static int getStringWidth(String text) {
        return customFont() ? (Kisman.instance.customFontRenderer.getStringWidth(text) + 3) : fontRenderer.getStringWidth(text);//!1
    }

    public static void drawString(String text, double x, double y, int color) {
        if (customFont()) {
            switch (getCustomFont()) {
                case "Verdana": {
                    Kisman.instance.customFontRenderer.drawString(getStringModofiers() + text, x, y - 1.0D, color, false);
                    break;
                }
                case "Comfortaa": {
                    comfortaa18.drawString(getStringModofiers() + text, x, y, color);
                    break;
                }
            }
        } else {
            fontRenderer.drawString(getStringModofiers() + text, (int)x, (int)y, color);
        }
    }

    public static void drawStringWithShadow(String text, double x, double y, int color) {
        if (customFont()) {
            switch (getCustomFont()) {
                case "Verdana": {
                    Kisman.instance.customFontRenderer.drawStringWithShadow(getStringModofiers() + text, x, y - 1.0D, color);
                    break;
                }
                case "Comfortaa": {
                    comfortaa18.drawStringWithShadow(getStringModofiers() + text, x, y, color);
                    break;
                }
            }
        } else {
            fontRenderer.drawStringWithShadow(getStringModofiers() + text, (float)x, (float)y, color);
        }
    }

    public static void drawCenteredStringWithShadow(String text, float x, float y, int color) {
        if (customFont()) {
            switch (getCustomFont()) {
                case "Verdana": {
                    Kisman.instance.customFontRenderer.drawCenteredStringWithShadow(getStringModofiers() + text, x, y - 1f, color);
                    break;
                }
                case "Comfortaa": {
                    comfortaa18.drawCenteredStringWithShadow(getStringModofiers() + text, x, y - 1f, color);
                    break;
                }
            }
        } else {
            fontRenderer.drawStringWithShadow(getStringModofiers() + text, x - fontRenderer.getStringWidth(getStringModofiers() + text) / 2.0F, y, color);
        }
    }

    public static void drawCenteredString(String text, float x, float y, int color) {
        if (customFont()) {
            switch (getCustomFont()) {
                case "Verdana": {
                    Kisman.instance.customFontRenderer.drawCenteredString(getStringModofiers() + text, x, y - 1f, color);
                    break;
                }
                case "Comfortaa": {
                    comfortaa18.drawCenteredString(getStringModofiers() + text, x, y - 1f, color);
                    break;
                }
            }
        } else {
            fontRenderer.drawString(getStringModofiers() + text, (int)(x - (getStringWidth(getStringModofiers() + text) / 2)), (int)y, color);
        }
    }

    public static int getFontHeight() {
        return customFont() ? getCustomFont().equalsIgnoreCase("Verdana") ? (Kisman.instance.customFontRenderer.fontHeight / 2 - 1) : (comfortaa18.fontHeight - 8) / 2 : fontRenderer.FONT_HEIGHT;
    }

    public static boolean validateFont(String font) {
        for (String s : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (s.equals(font))
                return true;
        }
        return false;
    }

    private static boolean customFont() {
        return CustomFont.turnOn;
    }

    private static String getCustomFont() {
        return CustomFont.instance.mode.getValString();
    }

    private static String getStringModofiers() {
        return (CustomFont.instance.bold.getValBoolean() ? TextFormatting.BOLD + "" : "") + (CustomFont.instance.italic.getValBoolean() ? TextFormatting.ITALIC : "");
    }
}