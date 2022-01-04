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
    public static CFontRenderer consolas15 = new CFontRenderer(getFontTTF("consolas", 15), true, true);

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
/*        if(CustomFont.instance != null && customFont()) {
            switch(getCustomFont()) {
                case "Verdana": return verdata18.getStringWidth(text);
                case "Comfortaa": return comfortaal18.getStringWidth(text);
                case "Comfortaa Light": return comfortaal18.getStringWidth(text);
                case "Consolas": return consolas18.getStringWidth(text);
            }
        }

        return fontRenderer.getStringWidth(text);*/
        return customFont() ? getCustomFont().equals("Verdana") ? (Kisman.instance.customFontRenderer.getStringWidth(text)) : getCustomFont().equals("Consolas") ? (consolas18.getStringWidth(text)) : getCustomFont().equalsIgnoreCase("Comfortaa") || getCustomFont().equalsIgnoreCase("Comfortaa Light") ? (comfortaa18.getStringWidth(text)) : fontRenderer.getStringWidth(text) : fontRenderer.getStringWidth(text);
//        return customFont() ? (Kisman.instance.customFontRenderer.getStringWidth(text) + 3) : fontRenderer.getStringWidth(text);
    }

    public static int getStringWidth(String text, boolean gui) {
/*        if(CustomFont.instance != null && customFont()) {
            switch(getCustomFont()) {
                case "Verdana": return verdata18.getStringWidth(text);
                case "Comfortaa": return comfortaal18.getStringWidth(text);
                case "Comfortaa Light": return comfortaal18.getStringWidth(text);
                case "Consolas": return consolas18.getStringWidth(text);
            }
        }

        return fontRenderer.getStringWidth(text);*/
        return customFont() ? getCustomFont().equals("Verdana") ? (CSGOGui.instance.customSize.getValBoolean() && gui) ? (Kisman.instance.customFontRenderer1.getStringWidth(text)) : (Kisman.instance.customFontRenderer.getStringWidth(text)) : getCustomFont().equals("Consolas") ? (CSGOGui.instance.customSize.getValBoolean() && gui) ? (consolas15.getStringWidth(text)) : (consolas18.getStringWidth(text)) : getCustomFont().equalsIgnoreCase("Comfortaa") || getCustomFont().equalsIgnoreCase("Comfortaa Light") ? (CSGOGui.instance.customSize.getValBoolean() && gui) ? (comfortaa15.getStringWidth(text)) : (comfortaal18.getStringWidth(text)) : fontRenderer.getStringWidth(text) : fontRenderer.getStringWidth(text);
    }

/*    public static int getStringWidth(String text, boolean gui) {
        if(CustomFont.instance != null && customFont()) {
            switch(getCustomFont()) {
                case "Verdana": return (CSGOGui.instance.customSize.getValBoolean() && gui ? verdata15 : verdata18).getStringWidth(text);
                case "Comfortaa": return (CSGOGui.instance.customSize.getValBoolean() && gui ? comfortaa15 : comfortaa18).getStringWidth(text);
                case "Comfortaa Light": return (CSGOGui.instance.customSize.getValBoolean() && gui ? comfortaal15 : comfortaal18).getStringWidth(text);
                case "Consolas": return (CSGOGui.instance.customSize.getValBoolean() && gui ? consolas15 : consolas18).getStringWidth(text);
            }
        }

        return fontRenderer.getStringWidth(text);
    }*/

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
            }
        } else {
            fontRenderer.drawString(getStringModofiers() + text, (int)x, (int)y, color);
        }
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
                case "Consolas": {
                    consolas18.drawString(getStringModofiers() + text, x, y, color);
                    break;
                }
                case "Comfortaa Light": {
                    comfortaal18.drawString(getStringModofiers() + text, x, y, color);
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
                case "Consolas": {
                    consolas18.drawStringWithShadow(getStringModofiers() + text, x, y, color);
                    break;
                }
                case "Comfortaa Light": {
                    comfortaal18.drawStringWithShadow(getStringModofiers() + text, x, y, color);
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
                case "Consolas": {
                    consolas18.drawCenteredStringWithShadow(getStringModofiers() + text, x, y, color);
                    break;
                }
                case "Comfortaa Light": {
                    comfortaal18.drawCenteredStringWithShadow(getStringModofiers() + text, x, y, color);
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
                case "Consolas": {
                    consolas18.drawCenteredString(getStringModofiers() + text, x, y, color);
                    break;
                }
                case "Comfortaa Light": {
                    comfortaal18.drawCenteredString(getStringModofiers() + text, x, y, color);
                    break;
                }
            }
        } else {
            fontRenderer.drawString(getStringModofiers() + text, (int)(x - (getStringWidth(getStringModofiers() + text) / 2)), (int)y, color);
        }
    }

    public static int getFontHeight(boolean gui) {
/*        if(CustomFont.instance != null) {
            if(CustomFont.turnOn) {
                switch(CustomFont.instance.mode.getValString()) {
                    case "Verdana": return (CSGOGui.instance.customSize.getValBoolean() && gui ? verdana15 : verdana18).fontHeight;
                    case "Comfortaa": return (CSGOGui.instance.customSize.getValBoolean() && gui ? comfortaa15 : comfortaa18).fontHeight;
                    case "Comfortaa Light": return (CSGOGui.instance.customSize.getValBoolean() && gui ? comfortaal15 : comfortaal18).fontHeight;
                    case "Consolas": return (CSGOGui.instance.customSize.getValBoolean() && gui ? consolas15 : consolas18).fontHeight;
                }
            }
        }

        return fontRenderer.FONT_HEIGHT;*/
        return customFont() ? getCustomFont().equalsIgnoreCase("Verdana") ? (Kisman.instance.customFontRenderer.fontHeight / 2 - 1) : getCustomFont().equalsIgnoreCase("Consolas") ? (CSGOGui.instance.customSize.getValBoolean() && gui ?  (consolas15.fontHeight - 8) / 2 : (consolas18.fontHeight - 8) / 2) : (comfortaa18.fontHeight - 8) / 2 : fontRenderer.FONT_HEIGHT;

    }

    public static int getFontHeight() {
/*        if(CustomFont.instance != null) {
            if(customFont()) {
                switch(CustomFont.instance.mode.getValString()) {
                    case "Verdana": return verdata18.fontHeight;
                    case "Comfortaa": return comfortaa15.fontHeight;
                    case "Comfortaa Light": return comfortaal18.fontHeight;
                    case "Consolas": return consolas18.fontHeight;
                }
            }
        }

        return fontRenderer.FONT_HEIGHT;*/
        return customFont() ? getCustomFont().equalsIgnoreCase("Verdana") ? (Kisman.instance.customFontRenderer.fontHeight / 2 - 1) : getCustomFont().equalsIgnoreCase("Consolas") ? (consolas15.fontHeight - 8) / 2 : (comfortaa18.fontHeight - 8) / 2 : fontRenderer.FONT_HEIGHT;
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
        String str = "";
        if(CustomFont.instance != null) {

            if(CustomFont.instance.italic.getValBoolean()) {
                str += TextFormatting.ITALIC;
            }

            if(CustomFont.instance.bold.getValBoolean() && getCustomFont().equalsIgnoreCase("Verdana")) {
                str += TextFormatting.BOLD;
            }

        }
        return str;
    }
}