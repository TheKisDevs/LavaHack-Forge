package com.kisman.cc.hud.hudmodule;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;

public class Logo extends GuiMainMenu {
    Minecraft mc = Minecraft.getMinecraft();

    public Logo(String name, String version) {
        mc.fontRenderer.drawString(name + " " + version, 4, 4, -1);
    }
}
