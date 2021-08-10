package com.kisman.cc.hud.hudmodule;

import com.kisman.cc.module.client.HUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Coord extends Gui {
    Minecraft mc = Minecraft.getMinecraft();


    @SubscribeEvent
    public void  renderOverlay(RenderGameOverlayEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRenderer;

        if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT && HUD.isCoord) {
            fr.drawStringWithShadow(TextFormatting.AQUA + "X: " + TextFormatting.GRAY + (int)  mc.player.posX + TextFormatting.AQUA + " Y: " + TextFormatting.GRAY + (int) mc.player.posY + TextFormatting.AQUA + " Z: " + TextFormatting.GRAY + (int) mc.player.posZ, 1, sr.getScaledHeight() - fr.FONT_HEIGHT - 1, -1);
        }
    }
}
