package com.kisman.cc.hud.hudmodule;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.client.HUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Logo {
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    public static int x = 1;
    public static int y = 1;
    public static int x1 = x = Minecraft.getMinecraft().fontRenderer.getStringWidth(Kisman.NAME + " " + Kisman.VERSION);
    public static int y1 = y + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;

    String name;
    String version;

    public Logo(String name, String version) {
        this.name = name;
        this.version = version;
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT && HUD.isLogo) {
            fr.drawStringWithShadow( TextFormatting.AQUA + name + " " + TextFormatting.GRAY + version, 1, 1, -1);
        }
    }
}
