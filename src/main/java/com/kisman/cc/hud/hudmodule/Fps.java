package com.kisman.cc.hud.hudmodule;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.util.Color;

public class Fps extends GuiMainMenu {
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fr = mc.fontRenderer;

    private int offset = 4 + fr.FONT_HEIGHT + 6;

    public Fps() {
        fr.drawString(TextFormatting.AQUA + "FPS: " + TextFormatting.GRAY + Minecraft.getDebugFPS(), 4, offset, -1);
    }
}
