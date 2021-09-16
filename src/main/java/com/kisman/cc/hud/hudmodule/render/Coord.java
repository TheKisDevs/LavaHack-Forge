package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.settings.*;

import org.lwjgl.opengl.GL11;

import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Coord extends HudModule {
    Minecraft mc = Minecraft.getMinecraft();
    ScaledResolution sr = new ScaledResolution(mc);
    FontRenderer fr = mc.fontRenderer;

    int posX;
    int nPosX;

    int posY;
    int nPosY;
    
    int posZ;
    int nPosZ;

    int x1 = 1, x2 = fr.getStringWidth("X: " + "(" + posX + ")[" + 
    nPosX +
    "]" +
    " Y: " + 
    "(" +
    posY +
    ")[" + 
    nPosY +
    "]" +
    " Z: " + 
    "(" +
    posZ +
    ")[" + 
    nPosZ +
    "]"
    ) + x1 + 1, x3, x4;
    int y1 = sr.getScaledHeight() - fr.FONT_HEIGHT - 1, y2 = sr.getScaledHeight() - 1, y3, y4;

    int x = 1;
    int y = sr.getScaledHeight() - 20;

    public Coord() {
        super("Coords", "coord", HudCategory.RENDER);
        Kisman.instance.settingsManager.rSetting(new Setting("Coords", this, x1, y1, x2, y2));
    }

    public void update() {
        FontRenderer fr = mc.fontRenderer;
        Minecraft mc = Minecraft.getMinecraft();

        if(mc.player != null && mc.world != null) {
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
        }

        // x1 = 0;
        // y1 = mc.gameSettings.overrideHeight - 2 - fr.FONT_HEIGHT;
        // x2 = fr.getStringWidth("X: " + "(" + posX + ")[" + 
        // nPosX +
        // "]" +
        // " Y: " + 
        // "(" +
        // posY +
        // ")[" + 
        // nPosY +
        // "]" +
        // " Z: " + 
        // "(" +
        // posZ +
        // ")[" + 
        // nPosZ +
        // "]"
        // ) + x1 + 1;
        // y2 = mc.gameSettings.overrideHeight;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRenderer;

        // x3 = 1;
        // y3 = sr.getScaledHeight() - (fr.FONT_HEIGHT * 2) - 2;
        // x4 = x3 + fr.getStringWidth("Yaw: " + (int) mc.player.cameraYaw + " Pitch:" + (int) mc.player.cameraPitch);
        // y4 = y3 + fr.FONT_HEIGHT + 1;

        if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            fr.drawStringWithShadow(
                TextFormatting.WHITE + 
                "X: " + 
                TextFormatting.GRAY + 
                "(" +
                TextFormatting.WHITE +
                posX +
                TextFormatting.GRAY +
                ")[" + 
                TextFormatting.WHITE +
                nPosX +
                TextFormatting.GRAY +
                "]" +
                TextFormatting.WHITE + 
                " Y: " + 
                TextFormatting.GRAY + 
                "(" +
                TextFormatting.WHITE +
                posY +
                TextFormatting.GRAY +
                ")[" + 
                TextFormatting.WHITE +
                nPosY +
                TextFormatting.GRAY +
                "]" +
                TextFormatting.WHITE + 
                " Z: " + 
                TextFormatting.GRAY + 
                "(" +
                TextFormatting.WHITE +
                posZ +
                TextFormatting.GRAY +
                ")[" + 
                TextFormatting.WHITE +
                nPosZ +
                TextFormatting.GRAY +
                "]",
                1, 
                sr.getScaledHeight() - fr.FONT_HEIGHT - 1, 
                -1
            );

            fr.drawStringWithShadow(
                TextFormatting.WHITE + 
                "Yaw: " +
                TextFormatting.GRAY +
                (int) mc.player.cameraYaw + 
                TextFormatting.WHITE +
                " Pitch:" +
                TextFormatting.GRAY +
                (int) mc.player.cameraPitch, 
                1,
                sr.getScaledHeight() - (fr.FONT_HEIGHT * 2) - 2,
                -1  
            );
        }
    }
}
