package com.kisman.cc.hud.hudmodule;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.text.TextFormatting;

public class Coord extends GuiMainMenu {
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fr = mc.fontRenderer;

    private int offsetX = 4;
    private int offsetY = mc.displayHeight  -  (fr.FONT_HEIGHT + 4);

    public Coord() {
        fr.drawString(TextFormatting.AQUA + "X: " + TextFormatting.GRAY + mc.player.posX + TextFormatting.AQUA + " Y: " + TextFormatting.GRAY + mc.player.posY + TextFormatting.AQUA + " Z: " + TextFormatting.GRAY + mc.player.posZ, 4, mc.displayHeight - 4 - fr.FONT_HEIGHT, -1);
    }
}
