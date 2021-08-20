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
import org.lwjgl.util.Color;

public class Fps extends Gui {
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fr = mc.fontRenderer;

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT && HUD.isFps) {
            fr.drawStringWithShadow(
                TextFormatting.AQUA + 
                "FPS: " + 
                TextFormatting.GRAY + 
                Minecraft.getDebugFPS(), 
                1,
                Kisman.instance.logo.getHeight(), 
                -1
            );
        }
    }
}
