package com.kisman.cc.hud.hudmodule;

import com.kisman.cc.module.client.HUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Coord extends Gui {
    Minecraft mc = Minecraft.getMinecraft();

    int posX;
    int nPosX;

    int posY;
    int nPosY;
    
    int posZ;
    int nPosZ;

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRenderer;

        if(mc.player.dimension == 0) {
            posX = (int) mc.player.posX;
            posY = (int) mc.player.posY;
            posZ = (int) mc.player.posZ;

            nPosX = (int) (mc.player.posX / 8);
            nPosY = (int) (mc.player.posY / 8);
            nPosZ = (int) (mc.player.posZ / 8);
        } else if(mc.player.dimension == -1) {
            posX = (int) mc.player.posX;
            posY = (int) mc.player.posY;
            posZ = (int) mc.player.posZ;

            nPosX = (int) (mc.player.posX * 8);
            nPosY = (int) (mc.player.posY * 8);
            nPosZ = (int) (mc.player.posZ * 8);
        }

        if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT && HUD.isCoord) {
            fr.drawStringWithShadow(
                TextFormatting.AQUA + 
                "X: " + 
                TextFormatting.GRAY + 
                "(" +
                TextFormatting.AQUA +
                posX +
                TextFormatting.GRAY +
                ")[" + 
                TextFormatting.AQUA +
                nPosX +
                TextFormatting.GRAY +
                "]" +
                TextFormatting.AQUA + 
                " Y: " + 
                TextFormatting.GRAY + 
                "(" +
                TextFormatting.AQUA +
                posY +
                TextFormatting.GRAY +
                ")[" + 
                TextFormatting.AQUA +
                nPosY +
                TextFormatting.GRAY +
                "]" +
                TextFormatting.AQUA + 
                " Z: " + 
                TextFormatting.GRAY + 
                "(" +
                TextFormatting.AQUA +
                posZ +
                TextFormatting.GRAY +
                ")[" + 
                TextFormatting.AQUA +
                nPosZ +
                TextFormatting.GRAY +
                "]",
                1, 
                sr.getScaledHeight() - fr.FONT_HEIGHT - 1, 
                -1
            );

            fr.drawStringWithShadow(
                TextFormatting.AQUA + 
                "Yaw: " +
                TextFormatting.GRAY +
                mc.player.cameraYaw + 
                TextFormatting.AQUA +
                " Pitch:" +
                TextFormatting.GRAY +
                mc.player.cameraPitch, 
                1,
                sr.getScaledHeight() - (fr.FONT_HEIGHT * 2) - 2,
                -1  
            );
        }
    }
}
