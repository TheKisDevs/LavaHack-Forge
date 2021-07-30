package com.kisman.cc.hud.hudmodule;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMainMenu;

public class Coord extends GuiMainMenu {
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fr = mc.fontRenderer;

    private int offsetX = 4;
    private int offsetY = mc.displayHeight  -  (fr.FONT_HEIGHT + 4);

    public Coord() {
        fr.drawString("X: " + mc.player.posX, offsetX,  offsetY, -1);
        offsetX += fr.getStringWidth("X: " + mc.player.posX) + 4;
        fr.drawString("Y: " + mc.player.posY, offsetX, offsetY, -1);
        offsetX += fr.getStringWidth("Y: " + mc.player.posY) + 4;
        fr.drawString("Z: " + mc.player.posZ, offsetX, offsetY, -1);
    }
}
