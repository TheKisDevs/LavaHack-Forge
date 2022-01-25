package com.kisman.cc.util.customfont;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.client.CSGOGui;
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
    public static CFontRenderer comfortaal15 = new CFontRenderer(getFontTTF("comfortaa-light", 15), true, true);
    public static CFontRenderer comfortaal16 = new CFontRenderer(getFontTTF("comfortaa-light", 16), true, true);

    public static CFontRenderer comfortaab20 = new CFontRenderer(getFontTTF("comfortaa-bold", 22), true, true);
    public static CFontRenderer comfortaab18 = new CFontRenderer(getFontTTF("comfortaa-bold", 18), true, true);
    public static CFontRenderer comfortaab16 = new CFontRenderer(getFontTTF("comfortaa-bold", 16), true, true);

    public static CFontRenderer comfortaa20 = new CFontRenderer(getFontTTF("comfortaa-regular", 22), true, true);
    public static CFontRenderer comfortaa18 = new CFontRenderer(getFontTTF("comfortaa-regular", 18), true, true);
    public static CFontRenderer comfortaa15 = new CFontRenderer(getFontTTF("comfortaa-regular", 15), true, true);

    public static CFontRenderer consolas18 = new CFontRenderer(getFontTTF("consolas", 18), true, true);
    public static CFontRenderer consolas16 = new CFontRenderer(getFontTTF("consolas", 16), true, true);
    public static CFontRenderer consolas15 = new CFontRenderer(getFontTTF("consolas", 15), true, true);

    public static CFontRenderer sfui19 = new CFontRenderer(getFontTTF("sf-ui", 19), true, true);

    public static CFontRenderer futura20 = new CFontRenderer(getFontTTF("futura-normal", 20), true, true);

    public static CustomFontRenderer verdana18 = Kisman.instance.customFontRenderer;
    public static CustomFontRenderer verdana15 = Kisman.instance.customFontRenderer1;

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
        return customFont() ? getCustomFont().equals("Verdana") ? (Kisman.instance.customFontRenderer.getStringWidth(text)) : getCustomFont().equals("Consolas") ? (consolas18.getStringWidth(text)) : getCustomFont().equalsIgnoreCase("Comfortaa") || getCustomFont().equalsIgnoreCase("Comfortaa Light") || getCustomFont().equalsIgnoreCase("Comfortaa Bold") ? (comfortaa18.getStringWidth(text)) : fontRenderer.getStringWidth(text) : fontRenderer.getStringWidth(text);
    }

    public static int getStringWidth(String text, boolean gui) {
        return customFont() ? getCustomFont().equals("Verdana") ? (CSGOGui.instance.customSize.getValBoolean() && gui) ? (Kisman.instance.customFontRenderer1.getStringWidth(text)) : (Kisman.instance.customFontRenderer.getStringWidth(text)) : getCustomFont().equals("Consolas") ? (CSGOGui.instance.customSize.getValBoolean() && gui) ? (consolas15.getStringWidth(text)) : (consolas18.getStringWidth(text)) : getCustomFont().equalsIgnoreCase("Comfortaa") || getCustomFont().equalsIgnoreCase("Comfortaa Light") || getCustomFont().equalsIgnoreCase("Comfortaa Bold") ? (CSGOGui.instance.customSize.getValBoolean() && gui) ? (comfortaa15.getStringWidth(text)) : (comfortaal18.getStringWidth(text)) : fontRenderer.getStringWidth(text) : fontRenderer.getStringWidth(text);
    }

    public static void drawString(String text, double x, double y, int color, boolean gui) {
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
                case "Consolas": {
                    (CSGOGui.instance.customSize.getValBoolean() && gui ? consolas15 : consolas18).drawString(getStringModofiers() + text, x, y, color);
                    break;
                }
                case "Comfortaa Light": {
                    comfortaal18.drawString(getStringModofiers() + text, x, y, color);
                    break;
                }
                case "Comfortaa Bold":
                    comfortaab18.drawString(text, x, y, color);
                    break;
            }
        } else fontRenderer.drawString(getStringModofiers() + text, (int)x, (int)y, color);
    }

    public static int drawString(String text, double x, double y, int color) {
        if (customFont()) {
            switch (getCustomFont()) {
                case "Verdana": return (int) Kisman.instance.customFontRenderer.drawString(getStringModofiers() + text, x, y - 1.0D, color, false);
                case "Comfortaa": return (int) comfortaa18.drawString(getStringModofiers() + text, x, y, color);
                case "Consolas": return (int) consolas18.drawString(getStringModofiers() + text, x, y, color);
                case "Comfortaa Light": return (int) comfortaal18.drawString(getStringModofiers() + text, x, y, color);
                case "Comfortaa Bold": return (int) comfortaab18.drawString(getStringModofiers() + text, x, y, color);
            }
        }
        return fontRenderer.drawString(getStringModofiers() + text, (int)x, (int)y, color);
    }

    public static int drawStringWithShadow(String text, double x, double y, int color) {
        if (customFont()) {
            switch (getCustomFont()) {
                case "Verdana": return (int) Kisman.instance.customFontRenderer.drawStringWithShadow(getStringModofiers() + text, x, y - 1.0D, color);
                case "Comfortaa": return (int) comfortaa18.drawStringWithShadow(getStringModofiers() + text, x, y, color);
                case "Consolas": return (int) consolas18.drawStringWithShadow(getStringModofiers() + text, x, y, color);
                case "Comfortaa Light": return (int) comfortaal18.drawStringWithShadow(getStringModofiers() + text, x, y, color);
                case "Comfortaa Bold": return (int) comfortaab18.drawStringWithShadow(getStringModofiers() + text, x, y, color);
            }
        }
        return fontRenderer.drawStringWithShadow(getStringModofiers() + text, (float)x, (float)y, color);
    }

    public static void drawCenteredStringWithShadow(String text, double x, double y, int color) {
        if (customFont()) {
            switch (getCustomFont()) {
                case "Verdana": {
                    Kisman.instance.customFontRenderer.drawCenteredStringWithShadow(getStringModofiers() + text, (float) x, (float) y - 1f, color);
                    break;
                }
                case "Comfortaa": {
                    comfortaa18.drawCenteredStringWithShadow(getStringModofiers() + text, x, y - 1f, color);
                    break;
                }
                case "Consolas": {
                    consolas18.drawCenteredStringWithShadow(getStringModofiers() + text, x, y, color);
                    break;
                }
                case "Comfortaa Light": {
                    comfortaal18.drawCenteredStringWithShadow(getStringModofiers() + text, x, y, color);
                    break;
                }
                case "Comfortaa Bold":
                    comfortaab18.drawCenteredStringWithShadow(getStringModofiers() + text, x, y, color);
                    break;
            }
        } else fontRenderer.drawStringWithShadow(getStringModofiers() + text, (float) x - fontRenderer.getStringWidth(getStringModofiers() + text) / 2.0F, (float) y, color);
    }

    public static int getFontHeight(boolean gui) {
        return customFont() ? getCustomFont().equalsIgnoreCase("Verdana") ? (Kisman.instance.customFontRenderer.fontHeight / 2 - 1) : getCustomFont().equalsIgnoreCase("Consolas") ? (CSGOGui.instance.customSize.getValBoolean() && gui ?  (consolas15.fontHeight - 8) / 2 : (consolas18.fontHeight - 8) / 2) : (comfortaa18.fontHeight - 8) / 2 : fontRenderer.FONT_HEIGHT;

    }

    public static int getFontHeight() {
        return customFont() ? getCustomFont().equalsIgnoreCase("Verdana") ? (Kisman.instance.customFontRenderer.fontHeight / 2 - 1) : getCustomFont().equalsIgnoreCase("Consolas") ? (consolas15.fontHeight - 8) / 2 : (comfortaa18.fontHeight - 8) / 2 : fontRenderer.FONT_HEIGHT;
    }

    private static boolean customFont() {
        return CustomFont.turnOn;
    }

    private static String getCustomFont() {
        return CustomFont.instance.mode.getValString();
    }

    private static String getStringModofiers() {
        String str = "";
        if(CustomFont.instance != null) {
            if(CustomFont.instance.italic.getValBoolean()) str += TextFormatting.ITALIC;
            if(CustomFont.instance.bold.getValBoolean() && getCustomFont().equalsIgnoreCase("Verdana")) str += TextFormatting.BOLD;
        }
        return str;
    }
}