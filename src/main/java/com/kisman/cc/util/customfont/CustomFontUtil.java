package com.kisman.cc.util.customfont;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.client.CustomFont;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;

public class CustomFontUtil {
    private static final FontRenderer fontRenderer = (Minecraft.getMinecraft()).fontRenderer;

    public static int getStringWidth(String text) {
        return customFont() ? (Kisman.instance.customFontRenderer.getStringWidth(text) + 3) : fontRenderer.getStringWidth(text);//!1
    }

    public static void drawString(String text, double x, double y, int color) {
        if (customFont()) {
            Kisman.instance.customFontRenderer.drawString(text, x, y - 1.0D, color, false);
        } else {
            fontRenderer.drawString(text, (int)x, (int)y, color);
        }
    }

    public static void drawStringWithShadow(String text, double x, double y, int color) {
        if (customFont()) {
            Kisman.instance.customFontRenderer.drawStringWithShadow(text, x, y - 1.0D, color);
        } else {
            fontRenderer.drawStringWithShadow(text, (float)x, (float)y, color);
        }
    }

    public static void drawCenteredStringWithShadow(String text, float x, float y, int color) {
        if (customFont()) {
            Kisman.instance.customFontRenderer.drawCenteredStringWithShadow(text, x, y - 1.0F, color);
        } else {
            fontRenderer.drawStringWithShadow(text, x - fontRenderer.getStringWidth(text) / 2.0F, y, color);
        }
    }

    public static void drawCenteredString(String text, float x, float y, int color) {
        if (customFont()) {
            Kisman.instance.customFontRenderer.drawCenteredString(text, x, y - 1.0F, color);
        } else {
            fontRenderer.drawString(text, (int)(x - (getStringWidth(text) / 2)), (int)y, color);
        }
    }

    public static int getFontHeight() {
        return customFont() ? (Kisman.instance.customFontRenderer.fontHeight / 2 - 1) : fontRenderer.FONT_HEIGHT;
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
}