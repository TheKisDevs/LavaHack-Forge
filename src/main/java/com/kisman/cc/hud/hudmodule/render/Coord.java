package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.module.client.HUD;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import com.kisman.cc.hud.hudmodule.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Coord extends HudModule {
    Minecraft mc = Minecraft.getMinecraft();

    int posX;
    int nPosX;

    int posY;
    int nPosY;
    
    int posZ;
    int nPosZ;

    public Coord() {
        super("Coords", "coord", HudCategory.RENDER);
    }

    public void update() {
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
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);

        if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            int color = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : -1;
            CustomFontUtil.drawStringWithShadow("X: " + "(" + posX + ")[" + nPosX + "]" + " Y: " + "(" + posY + ")[" + nPosY + "]" + " Z: " + "(" + posZ + ")[" + nPosZ + "]", 1, sr.getScaledHeight() - CustomFontUtil.getFontHeight()  - 1, color);
            CustomFontUtil.drawStringWithShadow("Yaw: " + (int) mc.player.rotationYaw + " Pitch:" + (int) mc.player.rotationPitch, 1, sr.getScaledHeight() - (CustomFontUtil.getFontHeight() * 2) - 2, color);
        }
    }
}
